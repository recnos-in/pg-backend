package org.recnos.pg.service.auth;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.config.JwtConfig;
import org.recnos.pg.exception.BadRequestException;
import org.recnos.pg.exception.DuplicateResourceException;
import org.recnos.pg.exception.InvalidCredentialsException;
import org.recnos.pg.mapper.OwnerMapper;
import org.recnos.pg.mapper.UserMapper;
import org.recnos.pg.model.dto.request.auth.LoginRequest;
import org.recnos.pg.model.dto.request.auth.OtpVerificationRequest;
import org.recnos.pg.model.dto.request.auth.RegisterRequest;
import org.recnos.pg.model.dto.response.auth.LoginResponse;
import org.recnos.pg.model.dto.response.auth.RegisterResponse;
import org.recnos.pg.model.dto.response.auth.TokenResponse;
import org.recnos.pg.model.dto.response.owner.OwnerLoginResponse;
import org.recnos.pg.model.entity.Owner;
import org.recnos.pg.model.entity.User;
import org.recnos.pg.repository.OwnerRepository;
import org.recnos.pg.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final UserMapper userMapper;
    private final OwnerMapper ownerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final OtpService otpService;

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

    @Transactional
    public LoginResponse loginWithOtp(OtpVerificationRequest request) {
        //Verify user exists
        if(!userRepository.existsByMobile(request.getMobile())) {
            throw new BadRequestException("User with given mobile number does not exist. Please register first.");
        }
        // Verify OTP first
        boolean isOtpValid = otpService.verifyOtp(request);
        if (!isOtpValid) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        if ("USER".equals(request.getUserType())) {
            return loginUserWithOtp(request.getMobile());
        } else {
            throw new BadRequestException("Invalid user type");
        }
    }

    @Transactional
    public OwnerLoginResponse loginOwnerWithOtp(OtpVerificationRequest request) {
        // Verify OTP first
        if(!ownerRepository.existsByMobile(request.getMobile())) {
            throw new BadRequestException("Owner with given mobile number does not exist. Please register first.");
        }

        boolean isOtpValid = otpService.verifyOtp(request);
        if (!isOtpValid) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        return loginOwnerWithOtpInternal(request.getMobile());
    }

    private LoginResponse loginUserWithOtp(String mobile) {
        // Find or create user
        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobile(mobile);
                    newUser.setName("User_" + mobile);
                    newUser.setEmail(mobile + "@temp.com"); // Temporary email
                    newUser.setIsMobileVerified(true);
                    newUser.setIsEmailVerified(false);
                    newUser.setMfaEnabled(false);
                    newUser.setLoginAttempts(0);
                    newUser.setIsBlocked(false);
                    return userRepository.save(newUser);
                });

        // Check if user is blocked
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new InvalidCredentialsException("Account is blocked. Please contact support.");
        }

        // Update mobile verification and login time
        user.setIsMobileVerified(true);
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

    private OwnerLoginResponse loginOwnerWithOtpInternal(String mobile) {
        // Find or create owner
        Owner owner = ownerRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    Owner newOwner = new Owner();
                    newOwner.setMobile(mobile);
                    newOwner.setName("Owner_" + mobile);
                    newOwner.setEmail(mobile + "@temp.com"); // Temporary email
                    newOwner.setIsMobileVerified(true);
                    newOwner.setIsEmailVerified(false);
                    newOwner.setMfaEnabled(false);
                    newOwner.setLoginAttempts(0);
                    newOwner.setIsBlocked(false);
                    newOwner.setIsVerified(false);
                    newOwner.setVerificationStatus("pending");
                    newOwner.setTrustScore(50);
                    newOwner.setComplaintCount(0);
                    newOwner.setAutoRespondEnabled(false);
                    return ownerRepository.save(newOwner);
                });

        // Check if owner is blocked
        if (Boolean.TRUE.equals(owner.getIsBlocked())) {
            throw new InvalidCredentialsException("Account is blocked. Please contact support.");
        }

        // Update mobile verification and login time
        owner.setIsMobileVerified(true);
        owner.setLastLogin(Instant.now());
        ownerRepository.save(owner);

        // Generate tokens
        TokenResponse tokens = generateTokens(owner.getId());

        return OwnerLoginResponse.builder()
                .message("Login successful")
                .owner(ownerMapper.toProfileResponse(owner))
                .tokens(tokens)
                .build();
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

    public OwnerLoginResponse registerOwner(RegisterRequest registerRequest) {
        // Check for duplicate email
        if (ownerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check for duplicate mobile
        if (ownerRepository.existsByMobile(registerRequest.getMobile())) {
            throw new DuplicateResourceException("Mobile number already registered");
        }

        // Create new owner
        Owner owner = new Owner();
        owner.setName(registerRequest.getName());
        owner.setEmail(registerRequest.getEmail());
        owner.setMobile(registerRequest.getMobile());
        owner.setPasswordHash(null);
        owner.setIsEmailVerified(false);
        owner.setIsMobileVerified(false);
        owner.setMfaEnabled(false);
        owner.setLoginAttempts(0);
        owner.setIsBlocked(false);
        owner.setIsVerified(false);
        owner.setVerificationStatus("pending");
        owner.setTrustScore(0);
        owner.setComplaintCount(0);
        owner.setAutoRespondEnabled(false);

        Owner savedOwner = ownerRepository.save(owner);

        // Generate tokens
        TokenResponse tokens = generateTokens(savedOwner.getId());

        return OwnerLoginResponse.builder()
                .message("Owner registered successfully")
                .owner(ownerMapper.toProfileResponse(savedOwner))
                .tokens(tokens)
                .build();
    }
}