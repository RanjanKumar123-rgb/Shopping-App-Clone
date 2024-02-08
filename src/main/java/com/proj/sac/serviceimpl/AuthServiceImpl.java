package com.proj.sac.serviceimpl;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proj.sac.cache.CacheStore;
import com.proj.sac.entity.Customer;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.User;
import com.proj.sac.exception.UserAlreadyExistEception;
import com.proj.sac.repo.CustomerRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.UserRepo;
import com.proj.sac.requestdto.OTPmodel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.MessageStructure;
import com.proj.sac.util.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService
{
	private UserRepo userRepo;
	private CustomerRepo customerRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<UserResponse> structure;
	private CacheStore<String> otpCacheStore;
	private CacheStore<User> userCacheStore;
	private JavaMailSender javaMailSender;
	

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) 
	{
			if(userRepo.existsByEmail(userRequest.getEmail())) 
				throw new UserAlreadyExistEception("User already exists. Try a new email id !!!");
			
			String OTP = generateOTP();
			User user = mapToRespective(userRequest);
			userCacheStore.add(userRequest.getEmail(), user);
			otpCacheStore.add(userRequest.getEmail(), OTP);
			
			
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatusCode(HttpStatus.ACCEPTED.value())
				.setMessage("Please verify your email id using OTP sent. OTP: "+OTP)
				.setData(mapToResponse(user)), HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPmodel OTP) 
	{
		User user = userCacheStore.get(OTP.getEmail());
		String otp = otpCacheStore.get(OTP.getEmail());
		
		if(otp!=null)
		{
			if(user!=null)
			{
				if(otp.equals(OTP.getOtp()))
				{
					user.setEmailVerified(true);
					userRepo.save(user);
					return new ResponseEntity<ResponseStructure<UserResponse>>(HttpStatus.OK);
				}else
					throw new RuntimeException("Invalid OTP");
			}else 
				throw new UserAlreadyExistEception("User already exists !!!");
		}else 
			throw new RuntimeException("Otp expired !!!");
	}

	@Async
	private void sendMail(MessageStructure message) throws MessagingException
	{
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText());
		
		javaMailSender.send(mimeMessage);
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
		user.setPassword(userRequest.getPassword());
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