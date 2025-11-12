package org.recnos.pg.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.auth.LoginRequest;
import org.recnos.pg.model.dto.request.auth.RegisterRequest;
import org.recnos.pg.model.dto.response.auth.LoginResponse;
import org.recnos.pg.model.dto.response.auth.RegisterResponse;
import org.recnos.pg.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth/user")
@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "APIs for user registration and login")
public class UserAuthController {

    private final AuthService authService;

    /**
     * POST /v1/auth/user/register - Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /v1/auth/user/login - Login user
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}