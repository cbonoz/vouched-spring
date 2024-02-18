package com.vouched.auth;


import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.VouchedUser;
import com.vouched.service.CustomUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  private final CustomUserService customUserService;
  private final UserDao userDao;
  private final RSASSAVerifier verifier;

  // Get super token from app props
  @Value("${app.supertoken}")
  private String superToken;

  @Inject
  public AuthTokenFilter(CustomUserService customUserService, UserDao userDao,
      RSASSAVerifier verifier) {
    this.customUserService = customUserService;
    this.userDao = userDao;
    this.verifier = verifier;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    final String token = extractToken(request);

    if (token == null) {
      // No token, no auth
      chain.doFilter(request, response);
      return;
    }

    // Super token check
    if (superToken.equals(token)) {
      // Get header from request for email
      String email = request.getHeader("X-Email");
      if (email.isBlank()) {
        throw new SoftException("X-Email header is required for super token");
      }
      UserToken user = createSuperUser(email);
      Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
          null);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(request, response);
      return;
    }

    try {
      SignedJWT signedJWT;
      signedJWT = SignedJWT.parse(token);
      signedJWT.verify(verifier);

      Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
      String username = (String) claims.get("sub");

      if (username != null
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserToken user = customUserService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
            null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      // Token is invalid or expired
      throw new SoftException(ex);
      // Handle the exception or ignore it
    }

    chain.doFilter(request, response);
  }

  private UserToken createSuperUser(String email) {
    VouchedUser vouchedUser = userDao.getUserByEmail(email).orElseThrow();
    return UserToken.createSuperUserToken(vouchedUser.getId(),
        vouchedUser.getExternalId(), vouchedUser.getEmail());
  }

  private String extractToken(HttpServletRequest request) {
    // Extract the token from the Authorization header or any other source
    // Example:
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
