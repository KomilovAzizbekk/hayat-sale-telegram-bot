package uz.mediasolutions.saleservicebot.secret;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.mediasolutions.saleservicebot.entity.User;

import java.sql.Timestamp;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {
    @Value("${app.jwt.access.token.key}")
    private String JWT_SECRET_KEY_FOR_ACCESS_TOKEN;

    @Value("${app.jwt.access.token.expiration-in-ms}")
    private Long JWT_EXPIRED_TIME_FOR_ACCESS_TOKEN;

    public String generateAccessToken(User user, Timestamp issuedAt) {
        Timestamp expireDate = new Timestamp(System.currentTimeMillis() + JWT_EXPIRED_TIME_FOR_ACCESS_TOKEN);
        String userId = String.valueOf(user.getId());
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(issuedAt)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY_FOR_ACCESS_TOKEN)
                .compact();
    }

    public boolean validToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET_KEY_FOR_ACCESS_TOKEN).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY_FOR_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());

    }
}
