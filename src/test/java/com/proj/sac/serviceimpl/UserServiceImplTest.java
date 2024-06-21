package com.proj.sac.serviceimpl;

import com.proj.sac.entity.User;
import com.proj.sac.enums.UserRole;
import com.proj.sac.exception.UserNotFoundException;
import com.proj.sac.repo.UserRepo;
import com.proj.sac.util.ResponseStructure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ResponseStructure<User> userStructure;

    @Autowired
    private UserServiceImpl userServiceImpl;


    @Test
    void testFindUserByUserId_UserExists() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("Test User");
        user.setUserRole(UserRole.SELLER);
        user.setDeleted(false);
        user.setEmail("spunkysahoo@gmail.com");
        user.setPassword("Abcdefgh@1234");
        user.setEmailVerified(true);

        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userStructure.getData()).thenReturn(user);
        when(userStructure.getMessage()).thenReturn("User Data Fetched");
        when(userStructure.getStatusCode()).thenReturn(HttpStatus.FOUND.value());

        // Act
        ResponseEntity<ResponseStructure<User>> response = userServiceImpl.findUserByUserId(1);

        // Assert
        verify(userStructure).setData(user);
        verify(userStructure).setMessage("User Data Fetched");
        verify(userStructure).setStatusCode(HttpStatus.FOUND.value());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getData());
        assertEquals("User Data Fetched", response.getBody().getMessage());
        assertEquals(HttpStatus.FOUND.value(), response.getBody().getStatusCode());
    }

    @Test
    void testFindUserByUserId_UserNotExists() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.findUserByUserId(1);
        });

        assertEquals("Failed to find the user !!!", exception.getMessage());
    }
}
