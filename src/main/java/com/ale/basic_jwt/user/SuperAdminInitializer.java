package com.ale.basic_jwt.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Component
public class SuperAdminInitializer {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${superadmin.firstname}")
  private String adminFirstname;

  @Value("${superadmin.lastname}")
  private String adminLastname;

  @Value("${superadmin.email}")
  private String adminEmail;

  @Value("${superadmin.password}")
  private String adminPassword;

  @Bean
  public ApplicationRunner initSuperAdmin() {
    return args -> {

      if (userRepository.findByEmail(adminEmail).isEmpty()) {
        User superAdmin = User.builder()
            .firstname(adminFirstname)
            .lastname(adminLastname)
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .role(Role.ADMIN)
            .build();

        userRepository.save(superAdmin);
      } else {
        return;
      }
    };
  };
}
