package com.ale.basic_jwt.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<User>> getAll() {
    List<User> users = userRepository.findAll();

    if (users.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(users);
  }

  @PatchMapping("/{id}/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> updateRole(@PathVariable Integer id, @RequestParam String newRole,
      Authentication authentication) {
    Object authenticatedAdmin = authentication.getPrincipal();
    if (authenticatedAdmin == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated.");
    }
    
    
    return ResponseEntity.ok("Rol actualizado con éxito.");
  }
}
