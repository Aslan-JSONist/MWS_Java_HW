package com.example.todolist.config;

import com.example.todolist.security.JwtAuthFilter;
import com.example.todolist.security.JwtUtils;
import com.example.todolist.security.RestAccessDeniedHandler;
import com.example.todolist.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT API security. CSRF is disabled only because this API is stateless (no session
 * cookies), so CSRF against browser cookies does not apply.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder(@Value("${app.security.password-pepper}") String pepper) {
    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        return bcrypt.encode(appendPepper(rawPassword, pepper));
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return bcrypt.matches(appendPepper(rawPassword, pepper), encodedPassword);
      }

      private String appendPepper(CharSequence raw, String p) {
        return raw + p;
      }
    };
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    var user = User.withUsername("user")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();
    var reader = User.withUsername("reader")
        .password(passwordEncoder.encode("password"))
        .authorities("ROLE_USER", "READ_PRIVILEGE")
        .build();
    return new InMemoryUserDetailsManager(user, reader);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public JwtAuthFilter jwtAuthFilter(JwtUtils jwtUtils) {
    return new JwtAuthFilter(jwtUtils);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthFilter jwtAuthFilter,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler,
      @Value("${app.security.legacy-api-permit-all:false}") boolean legacyApiPermitAll) throws Exception {
    String[] legacyApiMatchers = {
        "/api/tasks/**", "/api/attachments/**", "/api/preferences/**", "/api/favorites/**"
    };
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/api/v1/auth/login").permitAll();
          auth.requestMatchers("/external/**").permitAll();
          auth.requestMatchers("/actuator/**").permitAll();
          auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll();
          auth.requestMatchers("/error").permitAll();
          auth.requestMatchers(HttpMethod.GET, "/api/v1/profile").hasRole("USER");
          auth.requestMatchers(HttpMethod.GET, "/api/v1/docs").hasAuthority("READ_PRIVILEGE");
          auth.requestMatchers("/api/v1/**").authenticated();
          if (legacyApiPermitAll) {
            auth.requestMatchers(legacyApiMatchers).permitAll();
          } else {
            auth.requestMatchers(legacyApiMatchers).authenticated();
          }
          auth.anyRequest().authenticated();
        })
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
