package com.vouched.config;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.vouched.auth.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public RSASSAVerifier rsaVerifier(PublicKey publicKey) {
        return new RSASSAVerifier((RSAPublicKey) publicKey);
    }

    @Bean
    public SecurityFilterChain filterChain(AuthTokenFilter authTokenFilter, HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> {
                            httpSecurityCorsConfigurer
                                    .configurationSource(httpServletRequest -> {
                                                CorsConfiguration corsConfiguration = new CorsConfiguration();
                                                corsConfiguration.setAllowedOrigins(List.of("*"));
                                                corsConfiguration.setAllowedMethods(List.of("*"));
                                                corsConfiguration.setAllowedHeaders(List.of("*"));
//                                                corsConfiguration.setAllowCredentials(true);
                                                corsConfiguration.setMaxAge(3600L);
                                                return corsConfiguration;
                                            }
                                    );
                        }
                )
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/up").permitAll()
                .anyRequest().authenticated()
                // Add your authorization rules here
                .and()
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}