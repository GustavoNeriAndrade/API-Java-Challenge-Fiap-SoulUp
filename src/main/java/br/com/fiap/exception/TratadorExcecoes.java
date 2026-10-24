package br.com.fiap.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class TratadorExcecoes {

    private static final Logger log = LoggerFactory.getLogger(TratadorExcecoes.class);

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos.");
        return ResponseEntity.badRequest().body(Map.of("message", mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Corpo da requisição inválido."));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> banco(SQLException e) {
        if (e.getErrorCode() == 1) { // ORA-00001: violação de UNIQUE
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Já existe um registro com estes dados."));
        }
        log.error("Erro de banco de dados", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro ao acessar o banco de dados."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generico(Exception e) {
        // Erros padrão do Spring (404, 405, 415...) mantêm o status original
        if (e instanceof ErrorResponse erro) {
            HttpStatus status = HttpStatus.valueOf(erro.getStatusCode().value());
            String mensagem = switch (status) {
                case NOT_FOUND -> "Recurso não encontrado.";
                case METHOD_NOT_ALLOWED -> "Método HTTP não permitido para este recurso.";
                case UNSUPPORTED_MEDIA_TYPE -> "Tipo de conteúdo não suportado.";
                default -> "Requisição inválida.";
            };
            return ResponseEntity.status(status).body(Map.of("message", mensagem));
        }

        log.error("Erro inesperado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro interno do servidor."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Parâmetro inválido."));
    }
}