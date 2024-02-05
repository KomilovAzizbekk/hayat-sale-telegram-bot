package uz.mediasolutions.saleservicebot.secret;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;
import uz.mediasolutions.saleservicebot.entity.User;
import uz.mediasolutions.saleservicebot.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(Rest.AUTHORIZATION_HEADER);
        if (authorization != null) {
            User user = null;
            if (authorization.startsWith("Bearer ")) {
                user = getUserFromBearerToken(authorization);
            }
            if (user != null) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    public User getUserFromBearerToken(String token) {
        try {
            token = token.substring("Bearer".length()).trim();
            if (jwtProvider.validToken(token)) {
                UUID userId = jwtProvider.getUserIdFromToken(token);
                Optional<User> optionalUser = userRepository.findByIdAndEnabledIsTrueAndAccountNonExpiredIsTrueAndCredentialsNonExpiredIsTrueAndAccountNonLockedIsTrue(userId);
                return optionalUser.orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}