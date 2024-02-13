package com.proj.sac.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proj.sac.cache.CacheStore;
import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.Customer;
import com.proj.sac.entity.RefreshToken;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.User;
import com.proj.sac.exception.UserAlreadyExistEception;
import com.proj.sac.exception.UserNotFoundException;
import com.proj.sac.exception.UserNotLoggedInException;
import com.proj.sac.repo.AccessTokenRepo;
import com.proj.sac.repo.CustomerRepo;
import com.proj.sac.repo.RefreshTokenRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.UserRepo;
import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OTPmodel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.security.JwtService;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.CookieManager;
import com.proj.sac.util.MessageStructure;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService
{
	private UserRepo userRepo;
	private CustomerRepo customerRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<UserResponse> structure;
	private ResponseStructure<AuthResponse> authStructure;
	private SimpleResponseStructure simpleStructure;
	private CacheStore<String> otpCacheStore;
	private CacheStore<User> userCacheStore;
	private JavaMailSender javaMailSender;
	private AuthenticationManager authenticationManager;
	private CookieManager cookieManager;
	private JwtService jwtService;
	private AccessTokenRepo accessTokenRepo;
	private RefreshTokenRepo refreshTokenRepo;
	private PasswordEncoder passwordEncoder;
	
	@Value("${myapp.access.expiry}")
	private int accessExpiryInSecs;
	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSecs;
	
	public AuthServiceImpl(UserRepo userRepo, 
				CustomerRepo customerRepo, 
				SellerRepo sellerRepo,
				ResponseStructure<UserResponse> structure,
				ResponseStructure<AuthResponse> authStructure, 
				SimpleResponseStructure simpleStructure,
				CacheStore<String> otpCacheStore,
				CacheStore<User> userCacheStore, 
				JavaMailSender javaMailSender, 
				AuthenticationManager authenticationManager,
				CookieManager cookieManager,
				JwtService jwtService,
				RefreshTokenRepo refreshTokenRepo,
				AccessTokenRepo accessTokenRepo,
				PasswordEncoder passwordEncoder) 
	{
		super();
		this.userRepo = userRepo;
		this.customerRepo = customerRepo;
		this.sellerRepo = sellerRepo;
		this.structure = structure;
		this.authStructure = authStructure;
		this.simpleStructure = simpleStructure;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) 
	{
		if(userRepo.existsByEmail(userRequest.getEmail())) 
			throw new UserAlreadyExistEception("User already exists. Try a new email id !!!");
			
		String OTP = generateOTP();
		User user = mapToRespective(userRequest);
		userCacheStore.add(userRequest.getEmail(), user);
		otpCacheStore.add(userRequest.getEmail(), OTP);			
		
		try {
			sendOtpToMail(user, OTP);
		} catch (MessagingException e) {
			log.error("The email address doesn't exist!!!");
		}
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatusCode(HttpStatus.ACCEPTED.value())
				.setMessage("Please verify your email id using OTP sent to your mail")
				.setData(mapToResponse(user)), HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPmodel OTP) 
	{
		User user = userCacheStore.get(OTP.getEmail());
		String otp = otpCacheStore.get(OTP.getEmail());
		System.out.println(user);
		System.out.println(otp);
		if(otp==null) throw new RuntimeException("OTP Expired!!!");
		if(user==null) throw new UsernameNotFoundException("Userid doesnot exist!!!");
		if(otp.equals(OTP.getOtp()))
			user.setEmailVerified(true);
		userRepo.save(user);
		try {
			sendRegSucessMail(user);
		} catch (MessagingException e) {
			log.error("The email address doesn't exist!!!");
		}
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure
				.setData(mapToResponse(user))
				.setMessage("Regsitration Successfull")
				.setStatusCode(HttpStatus.OK.value()), HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(String refreshToken, String accessToken, AuthRequest authRequest, HttpServletResponse response) 
	{
		if(accessToken != null || refreshToken !=null)  throw new RuntimeException("User already  logged in !!!");
		else {
		String username = authRequest.getEmail().split("@")[0];
		String password = authRequest.getPassword();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = authenticationManager.authenticate(token);
		if(!authentication.isAuthenticated())
			throw new UsernameNotFoundException("Failed to authenticate the User");
		else
			return userRepo.findByUsername(username).map(user -> {
				grantAccess(response, user);
				return ResponseEntity.ok(authStructure.setStatusCode(HttpStatus.OK.value())
									.setData(AuthResponse.builder()
											.userId(user.getUserId())
											.username(username)
											.role(user.getUserRole().name())
											.isAuthenticated(true)
											.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSecs))
											.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSecs))
											.build()).setMessage(""));
			}).get();
		}
	}
	
	@Override
	public ResponseEntity<SimpleResponseStructure> logout(String rt, String at ,HttpServletResponse response) 
	{
		if(rt == null && at == null)
			throw new UserNotFoundException("Username doesnt exist");
		
		AccessToken accessToken = accessTokenRepo.findByToken(at);
		accessToken.setBlocked(true);
		accessTokenRepo.save(accessToken);
		
		RefreshToken refreshToken = refreshTokenRepo.findByToken(rt);
		refreshToken.setBlocked(true);
		refreshTokenRepo.save(refreshToken);
		
		response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));
		
		simpleStructure.setMessage("Logout Successful");
		simpleStructure.setStatusCode(HttpStatus.GONE.value());
		
		return new ResponseEntity<SimpleResponseStructure>(simpleStructure, HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAllDevices() 
	{
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
	       userRepo.findByUsername(username).ifPresent(user -> {
	           accessTokenRepo.findByUserAndIsBlocked(user,false).forEach(accessToken -> {
	               accessToken.setBlocked(true);
	               accessTokenRepo.save(accessToken);
	           });
	           refreshTokenRepo.findByUserAndIsBlocked(user,false).forEach(refreshToken -> {
	               refreshToken.setBlocked(true);
	               refreshTokenRepo.save(refreshToken);
	           });
	       });
	       simpleStructure.setMessage("Revoked from all devices");
	       simpleStructure.setStatusCode(HttpStatus.OK.value());
	        return new ResponseEntity<SimpleResponseStructure>(simpleStructure,HttpStatus.OK);
	}
	
	@Override
    public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(String accessToken, String refreshToken, HttpServletResponse response) 
	{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepo.findByUsername(username).ifPresent(user -> {
            blockAccessTokens(accessTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, accessToken));
            blockRefreshTokens(refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
            
        });
        simpleStructure.setMessage("Revoked from all other devices");
        simpleStructure.setStatusCode(HttpStatus.OK.value());
        
        return new ResponseEntity<>(simpleStructure,HttpStatus.OK);
    }
	
	@Override
	public ResponseEntity<SimpleResponseStructure> refreshLogin(String accessToken, String refreshToken, HttpServletResponse response) 
	{
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		userRepo.findByUsername(userName).ifPresent(user->{
			if(accessToken==null) {
				grantAccess(response, user);
			}else {
				blockAccessTokens(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));
			}
			if(refreshToken==null) {
				throw new UserNotLoggedInException("user not logged in");
			}else {
				blockRefreshTokens(refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
				grantAccess(response, user);
			}
		});
		
		simpleStructure.setMessage("Refresh Token Refreshed");
        simpleStructure.setStatusCode(HttpStatus.OK.value());
        
        return new ResponseEntity<>(simpleStructure,HttpStatus.OK);
	}
	
	
	
	
//	=========================================================================================================================================
	
	private void blockAccessTokens(List<AccessToken> accessTokens)
	{
		accessTokens.forEach(at -> {
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});
	}
	
	private void blockRefreshTokens(List<RefreshToken> refreshTokens)
	{
		refreshTokens.forEach(rt -> {
			rt.setBlocked(true);
			refreshTokenRepo.save(rt);
		});
	}
	
	@Async
	private void sendMail(MessageStructure message) throws MessagingException
	{
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText(), true);
		
		javaMailSender.send(mimeMessage);
	}
	
	private void grantAccess(HttpServletResponse response, User user)
	{
		//Generating access and refresh tokens
		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());
		
		//Adding access and refresh tokens cookies to the response
		response.addCookie(cookieManager.configureCookie(new Cookie("at", accessToken), accessExpiryInSecs));
		response.addCookie(cookieManager.configureCookie(new Cookie("rt", refreshToken), refreshExpiryInSecs));
		
		//saving the access and refresh cookies to the database
		accessTokenRepo.save(AccessToken.builder()
				.token(accessToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSecs))
				.user(user)
				.build());
		
		refreshTokenRepo.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInSecs))
				.user(user)
				.build());
	}
	
	private void sendOtpToMail(User user, String otp) throws MessagingException
	{
		sendMail(MessageStructure.builder()
		.to(user.getEmail())
		.subject("OTP for Registration in the Shopping App")
		.sentDate(new Date())
		.text(
				"Hey, "+user.getUsername()
				+".<br> Good to see you intrested in our Shopping App. <br>Complete your Registration using the OTP: <h1>"
						+otp+"<h1>. <br>Note: The OTP expires within 5 mins.<br> With best Regards<br><br> Shopping App Clone"
				)
		.build());
	}
	
	private void sendRegSucessMail(User user) throws MessagingException
	{
		sendMail(MessageStructure.builder()
		.to(user.getEmail())
		.subject("Mail Id successfully Registered")
		.sentDate(new Date())
		.text(
				"Hey, "+user.getUsername()
				+",<br><p> Your Mail Id has been successfully registred in our Shopping App Clone. Enjoy Shopping."
				+ "<br><br> With best Regards<br> Shopping App Clone"
				)
		.build());
	}
	
	public UserResponse mapToResponse(User user) 
	{
		return UserResponse.builder()
				.email(user.getEmail())
				.username(user.getUsername())
				.userRole(user.getUserRole())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}

	@SuppressWarnings("unchecked")
	private <T extends User>T mapToRespective(UserRequest userRequest) 
	{
		User user=null;

		switch (userRequest.getUserRole()) 
		{
		case CUSTOMER ->{ user = new Customer();}
		case SELLER->{ user = new Seller();}
		default -> throw new RuntimeException();
		}
		
		user.setUsername(userRequest.getEmail().split("@")[0]);
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setUserRole(userRequest.getUserRole());
		user.setEmailVerified(false);
		user.setDeleted(false);
		
		return (T) user;
	}
	
	private User saveUser(User user) 
	{
		switch (user.getUserRole()) 
		{
			case CUSTOMER ->{user = customerRepo.save((Customer)user);}
			case SELLER->{user = sellerRepo.save((Seller)user);}
			default -> throw new RuntimeException();
		}
		return user;
	}
	
	private String generateOTP()
	{
		return String.valueOf(new Random().nextInt(100000, 999999));
	}
}