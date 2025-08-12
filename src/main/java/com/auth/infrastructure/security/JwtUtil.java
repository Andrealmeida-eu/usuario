package com.auth.infrastructure.security;

import com.auth.infrastructure.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtUtil {

    // Chave secreta usada para assinar e verificar tokens JWT
    @Value("${jwt.secret}")
    private String secretKey;



    public String generateToken(Usuario usuario) {
        try {
            // 1. Verificação de segurança da secretKey
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Secret key não pode ser nula ou vazia");
            }

            // 2. Conversão mais segura da chave
            SecretKey key = Keys.hmacShaKeyFor(
                    secretKey.getBytes(StandardCharsets.UTF_8)
            );

            // 3. Geração do token com tratamento robusto
            return Jwts.builder()
                    .subject(usuario.getEmail())
                    .claim("roles", List.of("ROLE_" + usuario.getRole().name())) // "roles" no plural
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                    .signWith(key, Jwts.SIG.HS256) // Especificando o algoritmo explicitamente
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar token JWT", e);
        }
    }


    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // Novo método
                .build()
                .parseSignedClaims(token) // Novo método (substitui parseClaimsJws)
                .getPayload(); // Novo método (substitui getBody)

        return claims.get("roles", List.class); // Extrai a lista de roles
    }



    // Extrai as claims do token JWT (informações adicionais do token)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // Define a chave secreta para validar a assinatura do token
                .build()
                .parseClaimsJws(token) // Analisa o token JWT e obtém as claims
                .getBody(); // Retorna o corpo das claims
    }

    // Extrai o email do usuário do token JWT
    public String extrairEmailToken(String token) {
        // Obtém o assunto (nome de usuário) das claims do token
        return extractClaims(token).getSubject();
    }

    // Verifica se o token JWT está expirado
    public boolean isTokenExpired(String token) {
        // Compara a data de expiração do token com a data atual
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valida o token JWT verificando o nome de usuário e se o token não está expirado
    public boolean validateToken(String token, String username) {
        // Extrai o nome de usuário do token
        final String extractedUsername = extrairEmailToken(token);
        // Verifica se o nome de usuário do token corresponde ao fornecido e se o token não está expirado
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
