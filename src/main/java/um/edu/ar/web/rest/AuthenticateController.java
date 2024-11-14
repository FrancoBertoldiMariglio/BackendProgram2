package um.edu.ar.web.rest;

import static um.edu.ar.security.SecurityUtils.AUTHORITIES_KEY;
import static um.edu.ar.security.SecurityUtils.JWT_ALGORITHM;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import um.edu.ar.domain.Authority;
import um.edu.ar.domain.User;
import um.edu.ar.service.UserService;
import um.edu.ar.web.rest.vm.LoginVM;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    public AuthenticateController(
        JwtEncoder jwtEncoder,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserService userService
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthorizeResponse> authorize(@Valid @RequestBody LoginVM loginVM) {
        LOG.debug("REST request to authenticate user: {}", loginVM.getUsername());

        LOG.debug("Creating authentication token");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        LOG.debug("Attempting authentication for user: {}", loginVM.getUsername());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOG.debug("Generating JWT token");
        String jwt = this.createToken(authentication, loginVM.isRememberMe());

        LOG.debug("Retrieving user details for: {}", loginVM.getUsername());
        Optional<User> userOptional = userService.getUserWithAuthoritiesByLogin(loginVM.getUsername());

        if (!userOptional.isPresent()) {
            LOG.error("Authentication failed - User not found: {}", loginVM.getUsername());
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        Set<Authority> roles = user.getAuthorities();

        LOG.info("User authenticated successfully: {}", loginVM.getUsername());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new AuthorizeResponse(jwt, user.getId(), roles), httpHeaders, HttpStatus.OK);
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param principal the authentication principal.
     * @return the login if the user is authenticated.
     */
    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated(Principal principal) {
        LOG.debug("REST request to check if the current user is authenticated");
        LOG.debug("Checking authentication status");

        boolean isAuth = principal != null;
        if (isAuth) {
            LOG.debug("User is authenticated: {}", principal.getName());
        } else {
            LOG.debug("No authenticated user found");
        }

        return principal == null ? null : principal.getName();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        LOG.debug("Creating new token for user: {}", authentication.getName());

        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            LOG.debug("Creating remember-me token");
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            LOG.debug("Creating standard token");
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        LOG.debug("Token created successfully for user: {}", authentication.getName());
        // No logging of token values or claims

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class AuthorizeResponse {

        private String idToken;
        private Long userId;
        private Set<Authority> roles;

        AuthorizeResponse(String idToken, Long userId, Set<Authority> roles) {
            this.idToken = idToken;
            this.userId = userId;
            this.roles = roles;
        }

        @JsonProperty("idToken")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("userId")
        Long getUserId() {
            return userId;
        }

        void setUserId(Long userId) {
            this.userId = userId;
        }

        @JsonProperty("roles")
        Set<Authority> getRoles() {
            return roles;
        }

        void setRoles(Set<Authority> roles) {
            this.roles = roles;
        }
    }
}
