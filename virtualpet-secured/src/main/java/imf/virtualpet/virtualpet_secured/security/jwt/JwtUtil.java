package imf.virtualpet.virtualpet_secured.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    private static final String HMAC_SHA256 = "HmacSHA256";

    private final String secret;
    private final long expiration;

    public JwtUtil(@Value("${jwt.expiration}") long expiration) {
        this.secret = imf.virtualpet.virtualpet_secured.security.jwt.SecretKey.getSecretKey();
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        try {
            String header = encoder.encodeToString(objectMapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encoder.encodeToString(objectMapper.writeValueAsBytes(Map.of(
                    "sub", username,
                    "iat", new Date().getTime(),
                    "exp", new Date(System.currentTimeMillis() + expiration).getTime()
            )));
            String signature = calculateHMAC(header + "." + payload, secret);

            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String calculatedSignature = calculateHMAC(header + "." + payload, secret);
            if (!calculatedSignature.equals(signature)) {
                return false;
            }

            Map<String, Object> claims = objectMapper.readValue(
                    decoder.decode(payload),
                    new TypeReference<>() {}
            );
            String tokenUsername = (String) claims.get("sub");
            long exp = ((Number) claims.get("exp")).longValue();

            return tokenUsername.equals(username) && new Date().getTime() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = parts[1];
            Map<String, Object> claims = objectMapper.readValue(
                    decoder.decode(payload),
                    new TypeReference<>() {}
            );
            return (String) claims.get("sub");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract username", e);
        }
    }

    private String calculateHMAC(String data, String secret) throws Exception {
        Mac hmac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
        hmac.init(keySpec);
        return encoder.encodeToString(hmac.doFinal(data.getBytes()));
    }
}
