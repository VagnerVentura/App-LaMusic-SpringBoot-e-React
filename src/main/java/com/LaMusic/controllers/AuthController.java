package com.LaMusic.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.AuthRequest;
import com.LaMusic.dto.AuthResponse;
import com.LaMusic.dto.RegisterRequest;
import com.LaMusic.entity.User;
import com.LaMusic.enums.Role;
import com.LaMusic.repositories.UserRepository;
import com.LaMusic.security.JwtUtil;
import com.LaMusic.services.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponse(jwt);
    }

    @PostMapping("/register")
    public AuthResponse registerCliente(@RequestBody RegisterRequest request) {
        return registerUser(request, Role.CUSTOMER);
    }

    @PostMapping("/register-admin")
    public AuthResponse registerAdmin(@RequestBody RegisterRequest request) {
        return registerUser(request, Role.ADMIN);
    }

    private AuthResponse registerUser(RegisterRequest request, Role role) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("E-mail já cadastrado.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setEmail_verified(false);

        userRepository.save(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponse(jwt);
    }
}
