package br.com.feiraviva.dto;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do carrinho com totais calculados")
public record CarrinhoResponseDTO(
        @Schema(example = "1") Long id,
        List<ItemResponseDTO> itens,
        @Schema(description = "Codigo do cupom aplicado", example = "FEIRA10") String cupom,
        @Schema(description = "Desconto em reais", example = "7.00") BigDecimal desconto,
        @Schema(description = "Estrategia de frete vigente", example = "PADRAO") String estrategiaFrete,
        @Schema(example = "70.00") BigDecimal subtotal,
        @Schema(example = "15.00") BigDecimal frete,
        @Schema(example = "78.00") BigDecimal total) { }
