package br.com.fiap.security;

import br.com.fiap.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class TokenService {

    private static final long VALIDADE_SEGUNDOS = 8 * 60 * 60;

    private final SecretKeySpec chave;

    public TokenService() {
        String segredo = System.getenv("TOKEN_SECRET");
        if (segredo == null || segredo.length() < 32) {
            throw new IllegalStateException(
                    "Defina a variável de ambiente TOKEN_SECRET com pelo menos 32 caracteres.");
        }
        this.chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String gerar(int idUsuario) {
        long expira = Instant.now().getEpochSecond() + VALIDADE_SEGUNDOS;
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((idUsuario + ":" + expira).getBytes(StandardCharsets.UTF_8));
        return payload + "." + assinar(payload);
    }

    public int validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 2) {
                throw tokenInvalido();
            }

            byte[] esperado = assinar(partes[0]).getBytes(StandardCharsets.UTF_8);
            if (!MessageDigest.isEqual(esperado, partes[1].getBytes(StandardCharsets.UTF_8))) {
                throw tokenInvalido();
            }

            String[] dados = new String(Base64.getUrlDecoder().decode(partes[0]),
                    StandardCharsets.UTF_8).split(":");
            if (Instant.now().getEpochSecond() > Long.parseLong(dados[1])) {
                throw tokenInvalido();
            }
            return Integer.parseInt(dados[0]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw tokenInvalido();
        }
    }

    private String assinar(String dado) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(chave);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(dado.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao assinar o token.", e);
        }
    }

    private RegraNegocioException tokenInvalido() {
        return new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Sessão inválida ou expirada.");
    }
}