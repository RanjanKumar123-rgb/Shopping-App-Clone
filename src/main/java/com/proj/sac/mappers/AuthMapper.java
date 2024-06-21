package com.proj.sac.mappers;

import com.proj.sac.entity.Customer;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.User;
import com.proj.sac.exception.UserRoleNotFoundException;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public <T extends User> T mapToRespective(UserRequest userRequest, PasswordEncoder passwordEncoder) {
        User user;

        switch (userRequest.getUserRole()) {
            case CUSTOMER -> user = new Customer();
            case SELLER -> user = new Seller();
            default -> throw new UserRoleNotFoundException("Failed to extract user role from user request");
        }

        user.setUsername(userRequest.getEmail().split("@")[0]);
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setUserRole(userRequest.getUserRole());
        user.setEmailVerified(false);
        user.setDeleted(false);

        return (T) user;
    }

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .isDeleted(user.isDeleted())
                .isEmailVerified(user.isEmailVerified())
                .build();
    }
}
