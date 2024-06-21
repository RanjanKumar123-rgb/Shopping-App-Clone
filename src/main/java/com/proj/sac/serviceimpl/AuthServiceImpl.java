package com.proj.sac.serviceimpl;

import com.proj.sac.cache.CacheStore;
import com.proj.sac.enums.UserRole;
import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OtpModel;
import com.proj.sac.entity.*;
import com.proj.sac.exception.*;
import com.proj.sac.mappers.AuthMapper;
import com.proj.sac.repo.*;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final CustomerRepo customerRepo;
    private final SellerRepo sellerRepo;
    private final CacheStore<String> otpCacheStore;
    private final CacheStore<User> userCacheStore;
    private final AuthenticationManager authenticationManager;
    private final CookieManager cookieManager;
    private final JwtService jwtService;
    private final AccessTokenRepo accessTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final Random random;
    private final MailServiceImpl mailService;
    private final AuthMapper authMapper;


    @Value("${app.jwt.token.access_expiry_seconds}")
    private int accessExpiryInSecs;
    @Value("${app.jwt.token.refresh_expiry_seconds}")
    private int refreshExpiryInSecs;

    public AuthServiceImpl(UserRepo userRepo, CustomerRepo customerRepo, SellerRepo sellerRepo, CacheStore<String> otpCacheStore, CacheStore<User> userCacheStore, AuthenticationManager authenticationManager, CookieManager cookieManager, JwtService jwtService, RefreshTokenRepo refreshTokenRepo, AccessTokenRepo accessTokenRepo, PasswordEncoder passwordEncoder, Random random, MailServiceImpl mailService, AuthMapper authMapper) {
        super();
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.sellerRepo = sellerRepo;
        this.otpCacheStore = otpCacheStore;
        this.userCacheStore = userCacheStore;
        this.authenticationManager = authenticationManager;
        this.cookieManager = cookieManager;
        this.jwtService = jwtService;
        this.accessTokenRepo = accessTokenRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.random = random;
        this.mailService = mailService;
        this.authMapper = authMapper;
    }

    @Override
    public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) {
        if (userRepo.existsByEmail(userRequest.getEmail()))
            throw new UserAlreadyExistException("User already exists. Try a new email id !!!");

        String otp = generateOTP();
        User user = authMapper.mapToRespective(userRequest, passwordEncoder);
        userCacheStore.add(userRequest.getEmail(), user);
        otpCacheStore.add(userRequest.getEmail(), otp);

        try {
            sendOtpToMail(user, otp);
        } catch (MessagingException e) {
            log.error("The email address doesn't exist!!!");
        }
        ResponseStructure<UserResponse> structure = new ResponseStructure<>();
        structure.setStatusCode(HttpStatus.ACCEPTED.value());
        structure.setMessage("Please verify your email id using OTP sent to your mail");
        structure.setData(authMapper.mapToResponse(user));

        return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel) {
        User user = userCacheStore.get(otpModel.getEmail());
        String otp = otpCacheStore.get(otpModel.getEmail());

        if (otp == null) throw new OtpExpiredException("OTP Expired!!!");
        if (user == null) throw new UsernameNotFoundException("UserID doesn't exist!!!");
        if (otp.equals(otpModel.getOtp())) user.setEmailVerified(true);
        userRepo.save(user);
        try {
            sendRegSuccessMail(user);
        } catch (MessagingException e) {
            log.error("The email address doesn't exist!!!");
        }

        ResponseStructure<UserResponse> structure = new ResponseStructure<>();
        structure.setData(authMapper.mapToResponse(user));
        structure.setMessage("Successfully Registered");
        structure.setStatusCode(HttpStatus.OK.value());

        return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ResponseStructure<AuthResponse>> login(String refreshToken, String accessToken, AuthRequest authRequest, HttpServletResponse response) {
        if (accessToken != null || refreshToken != null)
            throw new UserAlreadyLoggedInException("User already  logged in !!!");
        else {
            String username = authRequest.getEmail().split("@")[0];
            String password = authRequest.getPassword();
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);
            if (!authentication.isAuthenticated())
                throw new UsernameNotFoundException("Failed to authenticate the User");
            else return userRepo.findByUsername(username).map(user -> {
                grantAccess(response, user);
                ResponseStructure<AuthResponse> authStructure = new ResponseStructure<>();
                authStructure.setStatusCode(HttpStatus.OK.value());
                authStructure.setData(AuthResponse.builder().userId(user.getUserId()).username(username).role(user.getUserRole().name()).isAuthenticated(true).accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSecs)).refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSecs)).build());
                authStructure.setMessage("Logged In");

                return ResponseEntity.ok(authStructure);
            }).orElseThrow(() -> new UsernameNotFoundException("Failed to find username"));
        }
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> logout(String rt, String at, HttpServletResponse response) {
        if (rt == null && at == null) throw new UserNotFoundException("Username doesn't exist");

        if (at != null) {
            AccessToken accessToken = accessTokenRepo.findByToken(at);
            if (accessToken != null) {
                accessToken.setBlocked(true);
                accessTokenRepo.save(accessToken);
            }
        }

        if (rt != null) {
            RefreshToken refreshToken = refreshTokenRepo.findByToken(rt);
            if (refreshToken != null) {
                refreshToken.setBlocked(true);
                refreshTokenRepo.save(refreshToken);
            }
        }

        response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
        response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));

        SimpleResponseStructure simpleStructure = new SimpleResponseStructure();
        simpleStructure.setMessage("Logout Successful");
        simpleStructure.setStatusCode(HttpStatus.GONE.value());

        return new ResponseEntity<>(simpleStructure, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> revokeAllDevices() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepo.findByUsername(username).ifPresent(user -> {
            accessTokenRepo.findByUserAndIsBlocked(user, false).forEach(accessToken -> {
                accessToken.setBlocked(true);
                accessTokenRepo.save(accessToken);
            });
            refreshTokenRepo.findByUserAndIsBlocked(user, false).forEach(refreshToken -> {
                refreshToken.setBlocked(true);
                refreshTokenRepo.save(refreshToken);
            });
        });

        SimpleResponseStructure simpleStructure = new SimpleResponseStructure();
        simpleStructure.setMessage("Revoked from all devices");
        simpleStructure.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(simpleStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(String accessToken, String refreshToken, HttpServletResponse response) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepo.findByUsername(username).ifPresent(user -> {
            blockAccessTokens(accessTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, accessToken));
            blockRefreshTokens(refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken));

        });

        SimpleResponseStructure simpleStructure = new SimpleResponseStructure();
        simpleStructure.setMessage("Revoked from all other devices");
        simpleStructure.setStatusCode(HttpStatus.OK.value());

        return new ResponseEntity<>(simpleStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String accessToken, String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new UserNotLoggedInException("user not logged in");
        }

        String username = jwtService.extractUsername(refreshToken);

        return userRepo.findByUsername(username).map(user -> {
            if (accessToken == null) {
                grantAccess(response, user);
            } else {
                blockAccessTokens(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));
            }

            blockRefreshTokens(refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
            grantAccess(response, user);

            ResponseStructure<AuthResponse> authStructure = new ResponseStructure<>();
            authStructure.setStatusCode(HttpStatus.OK.value());
            authStructure.setData(AuthResponse.builder().userId(user.getUserId()).username(username).role(user.getUserRole().name()).isAuthenticated(true).accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSecs)).refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSecs)).build());
            authStructure.setMessage("Refresh Token Refreshed !!!");

            return ResponseEntity.ok(authStructure);
        }).orElseThrow(() -> new UsernameNotFoundException("Failed to find username"));
    }


//	=========================================================================================================================================


    private void blockAccessTokens(List<AccessToken> accessTokens) {
        accessTokens.forEach(at -> {
            at.setBlocked(true);
            accessTokenRepo.save(at);
        });
    }

    private void blockRefreshTokens(List<RefreshToken> refreshTokens) {
        refreshTokens.forEach(rt -> {
            rt.setBlocked(true);
            refreshTokenRepo.save(rt);
        });
    }


    private void grantAccess(HttpServletResponse response, User user) {
        //Generating access and refresh tokens
        String accessToken = jwtService.generateAccessToken(user.getUserRole().toString(), user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUserRole().toString(), user.getUsername());

        //Adding access and refresh tokens cookies to the response
        response.addCookie(cookieManager.configureCookie(new Cookie("at", accessToken), accessExpiryInSecs));
        response.addCookie(cookieManager.configureCookie(new Cookie("rt", refreshToken), refreshExpiryInSecs));

        //saving the access and refresh cookies to the database
        accessTokenRepo.save(AccessToken.builder().token(accessToken).isBlocked(false).expiration(LocalDateTime.now().plusSeconds(accessExpiryInSecs)).user(user).build());

        refreshTokenRepo.save(RefreshToken.builder().token(refreshToken).isBlocked(false).expiration(LocalDateTime.now().plusSeconds(refreshExpiryInSecs)).user(user).build());
    }


    public void sendRegSuccessMail(User user) throws MessagingException {
        mailService.sendMail(MessageStructure.builder().to(user.getEmail()).subject("Mail Id successfully Registered").sentDate(new Date()).text("Hey, " + user.getUsername() + ",<br><p> Your Mail Id has been successfully registered in our Shopping App Clone. Enjoy Shopping." + "<br><br> With best Regards<br> Shopping App Clone").build());
    }

    public void sendOtpToMail(User user, String otp) throws MessagingException {
        mailService.sendMail(MessageStructure.builder().to(user.getEmail()).subject("OTP for Registration in the Shopping App").sentDate(new Date()).text("<html>" + "<body>" + "<p>Hey, " + user.getUsername() + ",</p>" + "<p>Good to see you interested in our Shopping App.</p>" + "<p>Complete your Registration using the OTP: <h1>" + otp + "</h1></p>" + "<p>Note: The OTP expires within 5 minutes.</p>" + "<br>" + "<p>With best Regards,<br>Shopping App Clone</p>" + "</body>" + "</html>").build());
    }

    @SuppressWarnings("unused")
    private User saveUser(User user) {
        switch (user.getUserRole()) {
            case UserRole.CUSTOMER -> user = customerRepo.save((Customer) user);
            case UserRole.SELLER -> user = sellerRepo.save((Seller) user);
            default -> throw new UserRoleNotFoundException("Failed to extract user role from user request");
        }
        return user;
    }

    protected String generateOTP() {
        return String.valueOf(random.nextInt(100000, 999999));
    }
}