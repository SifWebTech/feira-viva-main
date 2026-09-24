package br.com.feiraviva.strategy;

import br.com.feiraviva.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Locale;

@Component
public class CalculadoraFrete {

    // O Spring injeta TODOS os beans StrategyFrete, chaveados pelo nome do bean
    private final Map<String, StrategyFrete> strategies;

    public CalculadoraFrete(Map<String, StrategyFrete> strategies) {
        this.strategies = strategies;
    }

    public boolean existe(String tipo) {
        return strategies.containsKey(normalizar(tipo));
    }

    public BigDecimal calcular(String tipo, BigDecimal subtotal) {
        var normalizado = normalizar(tipo);
        if (!strategies.containsKey(normalizado)) {
            throw new ResourceNotFoundException("Estratégia de frete inválida: " + tipo);
        }
        return strategies.get(normalizado).calcular(subtotal);
    }

    public String normalizar(String tipo) {
        return (tipo == null || tipo.isBlank()) ? "PADRAO" : tipo.trim().toUpperCase(Locale.ROOT);
    }
}
