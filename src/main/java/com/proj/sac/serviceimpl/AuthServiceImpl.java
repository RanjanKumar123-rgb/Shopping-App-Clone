package com.proj.sac.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Customer;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.User;
import com.proj.sac.exception.UserAlreadyExistEception;
import com.proj.sac.repo.CustomerRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.UserRepo;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService
{
	private UserRepo userRepo;
	private CustomerRepo customerRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<UserResponse> structure;

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) 
	{
		User user = userRepo.findByUsername(userRequest.getEmail().split("@")[0]).map(u->{
			if(u.isEmailVerified()) 
				throw new UserAlreadyExistEception("User already exists. Try a new email id !!!");
			else {
				System.out.println("Email Sent !!");
				//send a email to the client with OTP
				return u;
			}
		}).orElseGet(() -> saveUser(mapToRespective(userRequest)));	
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatusCode(HttpStatus.ACCEPTED.value())
				.setMessage("Please verify your email id using OTP sent")
				.setData(mapToResponse(user)), HttpStatus.ACCEPTED);
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
}