package br.com.feiraviva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)                       // 404
    public Map<String, String> naoEncontrado(ResourceNotFoundException ex) {
        return Map.of("erro", "nao_encontrado", "mensagem", ex.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)                        // 409
    public Map<String, String> regraDeNegocio(RegraDeNegocioException ex) {
        return Map.of("erro", "regra_de_negocio", "mensagem", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)                     // 400
    public Map<String, String> validacao(MethodArgumentNotValidException ex) {
        String campos = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(" | "));
        return Map.of("erro", "validacao", "mensagem", campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)                     // 400
    public Map<String, String> jsonInvalido(HttpMessageNotReadableException ex) {
        return Map.of("erro", "json_invalido",
                "mensagem", "Corpo da requisição ausente ou JSON malformado");
    }
}