package com.ale.basic_jwt.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ale.basic_jwt.config.JwtService;
import com.ale.basic_jwt.user.Role;
import com.ale.basic_jwt.user.User;
import com.ale.basic_jwt.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest registerRequest) {
    userRepository.findByEmail(registerRequest.getEmail())
        .ifPresent(user -> {
          throw new IllegalArgumentException("Email already in use.");
        });

    var user = User.builder()
        .firstname(registerRequest.getFirstname())
        .lastname(registerRequest.getLastame())
        .email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .role(Role.USER)
        .build();
    userRepository.save(user);

    var jwtToken = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
    var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }
}
