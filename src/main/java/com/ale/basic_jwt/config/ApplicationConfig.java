package com.ale.basic_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ale.basic_jwt.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
  private final UserRepository userRepository;
  @Bean
  public UserDetailsService userDetailsService() {
    // anonymous class syntax. 
    // UserDetailsService is an interface, this is the new instance of a class without a name that implements this interface.
    // this is useful when we only need a one-time implementation without creating
    // a separate named class.
    // this could be replaced with a lamda expression.
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
      }
    };
  }
}
