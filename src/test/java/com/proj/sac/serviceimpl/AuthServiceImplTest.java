package com.proj.sac.serviceimpl;

import com.proj.sac.cache.CacheStore;
import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.RefreshToken;
import com.proj.sac.entity.User;
import com.proj.sac.enums.UserRole;
import com.proj.sac.exception.*;
import com.proj.sac.mappers.AuthMapper;
import com.proj.sac.repo.*;
import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OtpModel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.security.JwtService;
import com.proj.sac.util.CookieManager;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceImplTest {

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CustomerRepo customerRepo;
    @MockBean
    private SellerRepo sellerRepo;
    @MockBean
    private ResponseStructure<UserResponse> structure;
    @MockBean
    private ResponseStructure<AuthResponse> authStructure;
    @MockBean
    private SimpleResponseStructure simpleStructure;
    @MockBean
    private CacheStore<String> otpCacheStore;
    @MockBean
    private CacheStore<User> userCacheStore;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private CookieManager cookieManager;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AccessTokenRepo accessTokenRepo;
    @MockBean
    private RefreshTokenRepo refreshTokenRepo;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private MailServiceImpl mailService;
    @MockBean
    private AuthMapper authMapper;
    @MockBean
    private HttpServletResponse httpServletResponse;
    @MockBean
    private Authentication authentication;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    private User user;
    private UserRequest userRequest;
    private OtpModel otpModel;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private String otp;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("testuser@example.com");
        user.setUsername(user.getEmail().split("@")[0]);
        user.setPassword("password");
        user.setUserRole(UserRole.SELLER);
        user.setEmailVerified(false);
        user.setDeleted(false);

        userRequest = new UserRequest();
        userRequest.setEmail("testuser@example.com");
        userRequest.setPassword("password");
        userRequest.setUserRole(UserRole.SELLER);

        otpModel = new OtpModel();
        otpModel.setEmail("testuser@example.com");
        otpModel.setOtp("123456");

        authRequest = new AuthRequest();
        authRequest.setEmail("testuser@example.com");
        authRequest.setPassword("password");

        authResponse = AuthResponse.builder().userId(1).username(user.getEmail().split("@")[0]).role(UserRole.SELLER.name()).isAuthenticated(true).accessExpiration(LocalDateTime.now().plusSeconds(3600)).refreshExpiration(LocalDateTime.now().plusSeconds(7200)).build();

        otp = "123456";
        accessToken = "access-token";
        refreshToken = "refresh-token";
    }

    @Test
    void testRegister_UserAlreadyExists() {
        when(userRepo.existsByEmail(userRequest.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> {
            authServiceImpl.register(userRequest);
        });

        verify(userRepo, times(1)).existsByEmail(userRequest.getEmail());
    }

    @Test
    void testRegister_Success() {
        when(userRepo.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encoded-password");
        when(authMapper.mapToRespective(userRequest, passwordEncoder)).thenReturn(user);
        when(authMapper.mapToResponse(user)).thenReturn(new UserResponse());

        ResponseEntity<ResponseStructure<UserResponse>> response = authServiceImpl.register(userRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Please verify your email id using OTP sent to your mail", response.getBody().getMessage());
    }

    @Test
    void testVerifyOTP_Success() {
        when(userCacheStore.get(otpModel.getEmail())).thenReturn(user);
        when(otpCacheStore.get(otpModel.getEmail())).thenReturn(otp);
        when(authMapper.mapToResponse(user)).thenReturn(new UserResponse());

        ResponseEntity<ResponseStructure<UserResponse>> response = authServiceImpl.verifyOTP(otpModel);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Successfully Registered", response.getBody().getMessage());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testVerifyOTP_Expired() {
        when(userCacheStore.get(otpModel.getEmail())).thenReturn(any(User.class));
        when(otpCacheStore.get(otpModel.getEmail())).thenReturn(null);

        assertThrows(OtpExpiredException.class, () -> {
            authServiceImpl.verifyOTP(otpModel);
        });
    }

    @Test
    void testVerifyOTP_UserNotFound() {
        when(userCacheStore.get(otpModel.getEmail())).thenReturn(null);
        when(otpCacheStore.get(otpModel.getEmail())).thenReturn("validOtp"); // Ensure OTP is valid

        assertThrows(UsernameNotFoundException.class, () -> {
            authServiceImpl.verifyOTP(otpModel);
        });
    }

    @Test
    void testLogin_UserAlreadyLoggedIn() {
        assertThrows(UserAlreadyLoggedInException.class, () -> {
            authServiceImpl.login(refreshToken, accessToken, authRequest, httpServletResponse);
        });
    }

    @Test
    void testLogin_Success() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail().split("@")[0], authRequest.getPassword());
        when(authenticationManager.authenticate(token)).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepo.findByUsername(authRequest.getEmail().split("@")[0])).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(anyString(), anyString())).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(anyString(), anyString())).thenReturn(refreshToken);

        ResponseEntity<ResponseStructure<AuthResponse>> response = authServiceImpl.login(null, null, authRequest, httpServletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse.getUsername(), response.getBody().getData().getUsername());
        verify(userRepo, times(1)).findByUsername(authRequest.getEmail().split("@")[0]);
    }

    @Test
    void testLogout_Success() {
        AccessToken accessTokenEntity = new AccessToken();
        accessTokenEntity.setToken(accessToken);
        accessTokenEntity.setBlocked(false);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setBlocked(false);

        when(accessTokenRepo.findByToken(accessToken)).thenReturn(accessTokenEntity);
        when(refreshTokenRepo.findByToken(refreshToken)).thenReturn(refreshTokenEntity);

        ResponseEntity<SimpleResponseStructure> response = authServiceImpl.logout(refreshToken, accessToken, httpServletResponse);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Logout Successful", response.getBody().getMessage());
        verify(accessTokenRepo, times(1)).save(accessTokenEntity);
        verify(refreshTokenRepo, times(1)).save(refreshTokenEntity);
    }

    @Test
    void testLogout_UserNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            authServiceImpl.logout(null, null, httpServletResponse);
        });
    }

    @Test
    void testRevokeAllDevices_Success() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        ResponseEntity<SimpleResponseStructure> response = authServiceImpl.revokeAllDevices();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Revoked from all devices", response.getBody().getMessage());
        verify(accessTokenRepo, times(1)).findByUserAndIsBlocked(user, false);
        verify(refreshTokenRepo, times(1)).findByUserAndIsBlocked(user, false);
    }

    @Test
    void testRevokeOtherDevices_Success() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        ResponseEntity<SimpleResponseStructure> response = authServiceImpl.revokeOtherDevices(accessToken, refreshToken, httpServletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Revoked from all other devices", response.getBody().getMessage());
        verify(accessTokenRepo, times(1)).findByUserAndIsBlockedAndTokenNot(user, false, accessToken);
        verify(refreshTokenRepo, times(1)).findByUserAndIsBlockedAndTokenNot(user, false, refreshToken);
    }

    @Test
    void testRefreshLogin_Success() {
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getUsername());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(anyString(), anyString())).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(anyString(), anyString())).thenReturn(refreshToken);

        ResponseEntity<ResponseStructure<AuthResponse>> response = authServiceImpl.refreshLogin(null, refreshToken, httpServletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse.getUsername(), response.getBody().getData().getUsername());
        verify(userRepo, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void testRefreshLogin_UserNotLoggedIn() {
        assertThrows(UserNotLoggedInException.class, () -> {
            authServiceImpl.refreshLogin(null, null, httpServletResponse);
        });
    }

    @Test
    void testRefreshLogin_UserNotFound() {
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getUsername());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authServiceImpl.refreshLogin(null, refreshToken, httpServletResponse);
        });
    }
}
