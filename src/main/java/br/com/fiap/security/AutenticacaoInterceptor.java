package br.com.fiap.security;

import br.com.fiap.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AutenticacaoInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // pré-requisição do CORS
        }

        String cabecalho = request.getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Faça login para continuar.");
        }

        request.setAttribute("idUsuario", tokenService.validar(cabecalho.substring(7)));
        return true;
    }
}