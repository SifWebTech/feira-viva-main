package br.com.feiraviva.strategy;

import br.com.feiraviva.config.ConfiguracoesFeiraViva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("PADRAO")
public class FretePadraoStrategy implements StrategyFrete {

    private final ConfiguracoesFeiraViva config;   // singleton da Aula 10

    public FretePadraoStrategy(ConfiguracoesFeiraViva config) {
        this.config = config;
    }

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return subtotal.compareTo(config.getFreteGratisAcimaDe()) >= 0
                ? BigDecimal.ZERO
                : config.getFreteFixo();
    }
}