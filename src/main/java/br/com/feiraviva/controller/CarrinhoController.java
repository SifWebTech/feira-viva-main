package br.com.feiraviva.controller;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrinho")
@Tag(name = "Carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    @Operation(summary = "Obter carrinho do cliente",
            description = "Retorna itens, cupom, estratégia de frete e totais calculados.")
    @ApiResponse(responseCode = "200", description = "Carrinho retornado (pode estar vazio)")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO obter(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId) {
        return carrinhoService.obter(clienteId);
    }

    @PostMapping("/itens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar item ao carrinho",
            description = "Aplica a regra R1 (estoque); soma quantidade se o produto já estiver no carrinho.")
    @ApiResponse(responseCode = "201", description = "Item adicionado")
    @ApiResponse(responseCode = "400", description = "DTO inválido (quantidade < 1)")
    @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Produto sem estoque")
    public CarrinhoResponseDTO adicionar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Valid @RequestBody ItemCarrinhoDTO dto) {
        return carrinhoService.adicionarItem(clienteId, dto);
    }

    @PutMapping("/itens/{itemId}")
    @Operation(summary = "Alterar quantidade de item",
            description = "Aplica a regra R1 (estoque); quantidade 0 ou negativa remove o item.")
    @ApiResponse(responseCode = "200", description = "Quantidade alterada")
    @ApiResponse(responseCode = "404", description = "Cliente ou item não encontrado")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public CarrinhoResponseDTO alterar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "ID do item no carrinho", required = true, example = "1")
            @PathVariable Long itemId,
            @Parameter(description = "Nova quantidade", required = true, example = "3")
            @RequestParam int quantidade) {
        return carrinhoService.alterarQuantidade(clienteId, itemId, quantidade);
    }

    @DeleteMapping("/itens/{itemId}")
    @Operation(summary = "Remover item do carrinho")
    @ApiResponse(responseCode = "200", description = "Item removido (carrinho atualizado)")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO remover(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "ID do item no carrinho", required = true, example = "1")
            @PathVariable Long itemId) {
        return carrinhoService.removerItem(clienteId, itemId);
    }

    @PostMapping("/cupom")
    @Operation(summary = "Aplicar cupom de desconto",
            description = "Cupom criado pela CupomFactory; desconto fixo nunca supera o subtotal (R7).")
    @ApiResponse(responseCode = "200", description = "Cupom aplicado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou cupom inválido")
    public CarrinhoResponseDTO aplicarCupom(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "Código do cupom", required = true, example = "FEIRA10")
            @RequestParam String codigo) {
        return carrinhoService.aplicarCupom(clienteId, codigo);
    }

    @DeleteMapping("/cupom")
    @Operation(summary = "Remover cupom de desconto")
    @ApiResponse(responseCode = "200", description = "Cupom removido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO removerCupom(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId) {
        return carrinhoService.removerCupom(clienteId);
    }

    @PostMapping("/frete")
    @Operation(summary = "Definir estratégia de frete",
            description = "Padrão Strategy: PADRAO, FIXO ou RETIRADA.")
    @ApiResponse(responseCode = "200", description = "Estratégia de frete definida")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou estratégia inválida")
    public CarrinhoResponseDTO definirFrete(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "Estratégia de frete", required = true, example = "PADRAO")
            @RequestParam String tipo) {
        return carrinhoService.definirEstrategiaFrete(clienteId, tipo);
    }
}
