package com.elearning.backend.AuthTest; // Correct package name based on your structure

import com.elearning.backend.dto.AuthDTO.AuthResponse;
import com.elearning.backend.dto.AuthDTO.LoginRequest;
import com.elearning.backend.dto.AuthDTO.SignupRequest;
import com.elearning.backend.entity.Instructor;
import com.elearning.backend.entity.Role;
import com.elearning.backend.entity.Student;
import com.elearning.backend.entity.User;
import com.elearning.backend.exception.EmailAlreadyExistsException;
import com.elearning.backend.exception.InvalidPasswordException;
import com.elearning.backend.exception.UserNotFoundException;
import com.elearning.backend.repository.InstructorRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.UserRepository;
import com.elearning.backend.security.JwtService;
import com.elearning.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private StudentRepository studentRepo;
    @Mock
    private InstructorRepository instructorRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest studentSignupRequest;
    private SignupRequest instructorSignupRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private final String MOCK_TOKEN = "mocked.jwt.token";

    @BeforeEach
    void setUp() {
        studentSignupRequest = new SignupRequest(
                "John Doe", "john.doe@test.com", "password123", Role.STUDENT
        );
        instructorSignupRequest = new SignupRequest(
                "Jane Smith", "jane.smith@test.com", "password123", Role.INSTRUCTOR
        );
        loginRequest = new LoginRequest("test@login.com", "validpass");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Test User");
        testUser.setEmail("test@login.com");
        testUser.setPassword("encodedPassword"); // Password after encoding
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void signup_NewStudentUser_Success() {
        when(userRepo.existsByEmail(studentSignupRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(studentSignupRequest.password())).thenReturn("encodedPassword");
        when(jwtService.generateToken(anyString(), any(Map.class))).thenReturn(MOCK_TOKEN);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        }).when(userRepo).saveAndFlush(any(User.class));


        AuthResponse response = authService.signup(studentSignupRequest);

        assertNotNull(response);
        assertEquals(Role.STUDENT, response.role());
        assertEquals(MOCK_TOKEN, response.token());

        verify(userRepo, times(1)).saveAndFlush(any(User.class));
        verify(studentRepo, times(1)).save(any(Student.class));
        verify(instructorRepo, never()).save(any(Instructor.class));
    }

    @Test
    void signup_NewInstructorUser_Success() {

        when(userRepo.existsByEmail(instructorSignupRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(instructorSignupRequest.password())).thenReturn("encodedPassword");
        when(jwtService.generateToken(anyString(), any(Map.class))).thenReturn(MOCK_TOKEN);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return user;
        }).when(userRepo).saveAndFlush(any(User.class));

        AuthResponse response = authService.signup(instructorSignupRequest);

        assertNotNull(response);
        assertEquals(Role.INSTRUCTOR, response.role());

        verify(userRepo, times(1)).saveAndFlush(any(User.class));
        verify(instructorRepo, times(1)).save(any(Instructor.class));
        verify(studentRepo, never()).save(any(Student.class));
    }

    @Test
    void signup_EmailAlreadyExists_ThrowsException() {

        when(userRepo.existsByEmail(studentSignupRequest.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.signup(studentSignupRequest));
        verify(userRepo, never()).saveAndFlush(any(User.class));
        verify(jwtService, never()).generateToken(anyString(), anyMap());
    }

    @Test
    void login_ValidCredentials_Success() {
        when(userRepo.findByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.password(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser.getEmail(), Map.of("role", testUser.getRole().name())))
                .thenReturn(MOCK_TOKEN);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(MOCK_TOKEN, response.token());
        verify(jwtService, times(1)).generateToken(anyString(), anyMap());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepo.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyMap());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepo.findByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.password(), testUser.getPassword())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> authService.login(loginRequest));
        verify(jwtService, never()).generateToken(anyString(), anyMap());
    }

    @Test
    void updatePassword_UserFound_PasswordUpdated() {
        // Arrange
        String email = "update@test.com";
        String newPassword = "newPassword123";
        User userToUpdate = new User();
        userToUpdate.setEmail(email);
        userToUpdate.setPassword("oldHash");

        when(userRepo.findByEmail(email.toLowerCase())).thenReturn(Optional.of(userToUpdate));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedHash");

        authService.updatePassword(email, newPassword);

        verify(userRepo, times(1)).save(userToUpdate);
        assertEquals("newEncodedHash", userToUpdate.getPassword());
    }

    @Test
    void updatePassword_UserNotFound_NoActionTaken() {
        String email = "notfound@test.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        authService.updatePassword(email, "somepass");

        verify(userRepo, times(1)).findByEmail(email);
        verify(userRepo, never()).save(any(User.class));
    }
}