package br.com.fiap.config;

import br.com.fiap.security.AutenticacaoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacaoInterceptor;

    public ConfiguracaoWeb(AutenticacaoInterceptor autenticacaoInterceptor) {
        this.autenticacaoInterceptor = autenticacaoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns(
                        "/api/auth/me",
                        "/api/missoes/concluidas",
                        "/api/missoes/*/concluir",
                        "/api/conversoes/**",
                        "/api/impactos/**");
    }
}