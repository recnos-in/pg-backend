package org.recnos.pg.service.auth;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.config.JwtConfig;
import org.recnos.pg.exception.DuplicateResourceException;
import org.recnos.pg.exception.InvalidCredentialsException;
import org.recnos.pg.mapper.UserMapper;
import org.recnos.pg.model.dto.request.auth.LoginRequest;
import org.recnos.pg.model.dto.request.auth.RegisterRequest;
import org.recnos.pg.model.dto.response.auth.LoginResponse;
import org.recnos.pg.model.dto.response.auth.RegisterResponse;
import org.recnos.pg.model.dto.response.auth.TokenResponse;
import org.recnos.pg.model.entity.User;
import org.recnos.pg.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check for duplicate mobile
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateResourceException("Mobile number already registered");
        }

        // Check for duplicate Google ID if provided
        if (request.getGoogleId() != null &&
            userRepository.findByGoogleId(request.getGoogleId()).isPresent()) {
            throw new DuplicateResourceException("Google account already linked to another user");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setGoogleId(request.getGoogleId());
        user.setIsEmailVerified(false);
        user.setIsMobileVerified(false);
        user.setMfaEnabled(false);
        user.setLoginAttempts(0);
        user.setIsBlocked(false);

        User savedUser = userRepository.save(user);

        // Generate tokens
        TokenResponse tokens = generateTokens(savedUser.getId());

        return RegisterResponse.builder()
                .message("User registered successfully")
                .user(userMapper.toProfileResponse(savedUser))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Find user by email or mobile
        User user = userRepository.findByEmail(request.getEmailOrMobile())
                .or(() -> userRepository.findByMobile(request.getEmailOrMobile()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Check if user is blocked
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new InvalidCredentialsException("Account is blocked. Please contact support.");
        }

        // Check if account is locked due to too many failed attempts
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Account is temporarily locked. Please try again later.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Reset login attempts on successful login
        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        TokenResponse tokens = generateTokens(user.getId());

        return LoginResponse.builder()
                .message("Login successful")
                .user(userMapper.toProfileResponse(user))
                .tokens(tokens)
                .build();
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        // Lock account for 15 minutes after 5 failed attempts
        if (attempts >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        }

        userRepository.save(user);
    }

    private TokenResponse generateTokens(java.util.UUID userId) {
        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000) // Convert to seconds
                .build();
    }
}