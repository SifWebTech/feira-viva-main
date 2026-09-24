package br.com.feiraviva;

import br.com.feiraviva.config.ConfiguracoesFeiraViva;
import br.com.feiraviva.dto.ItemCarrinhoDTO;
import br.com.feiraviva.dto.PedidoRequestDTO;
import br.com.feiraviva.factory.CupomFactory;
import br.com.feiraviva.model.Cliente;
import br.com.feiraviva.model.Endereco;
import br.com.feiraviva.repository.ClienteRepository;
import br.com.feiraviva.repository.EnderecoRepository;
import br.com.feiraviva.repository.ProdutoRepository;
import br.com.feiraviva.service.CarrinhoService;
import br.com.feiraviva.service.CategoriaService;
import br.com.feiraviva.service.PedidoService;
import br.com.feiraviva.strategy.CalculadoraFrete;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class Aulas10e11IntegrationTests {

    @Autowired private ConfiguracoesFeiraViva configuracoes;
    @Autowired private ApplicationContext applicationContext;
    @Autowired private CarrinhoService carrinhoService;
    @Autowired private CategoriaService categoriaService;
    @Autowired private CalculadoraFrete calculadoraFrete;
    @Autowired private CupomFactory cupomFactory;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EnderecoRepository enderecoRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoService pedidoService;
    @Autowired private OpenAPI openAPI;

    @Test
    void deveAplicarSingletonFactoryCompositeEStrategyNoFluxoDeCompra() {
        assertEquals("Feira Viva API", openAPI.getInfo().getTitle());
        assertEquals("0.9.0", openAPI.getInfo().getVersion());
        assertEquals(4, openAPI.getTags().size());
        assertEquals(configuracoes, applicationContext.getBean(ConfiguracoesFeiraViva.class));
        assertEquals(0, new BigDecimal("15.00").compareTo(calculadoraFrete.calcular("FIXO", BigDecimal.TEN)));
        assertEquals(0, BigDecimal.ZERO.compareTo(calculadoraFrete.calcular("RETIRADA", BigDecimal.TEN)));
        assertEquals(0, new BigDecimal("15.00").compareTo(
                cupomFactory.criar("BEMVINDO").calcularDesconto(new BigDecimal("50.00"))));

        var arvore = categoriaService.arvore();
        assertEquals(2, arvore.size());
        assertTrue(arvore.stream().allMatch(categoria -> !categoria.folha()));
        assertTrue(arvore.stream().flatMap(categoria -> categoria.subcategorias().stream())
                .allMatch(categoria -> categoria.folha()));

        var cliente = new Cliente();
        cliente.setNome("Teste Aulas 10 e 11");
        cliente.setEmail("aulas10e11@feiraviva.test");
        cliente.setSenhaHash("senha-de-teste");
        cliente = clienteRepository.save(cliente);

        var endereco = new Endereco();
        endereco.setCliente(cliente);
        endereco.setCep("01001000");
        endereco.setLogradouro("Rua de Teste");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCidade("Sao Paulo");
        endereco.setUf("SP");
        endereco = enderecoRepository.save(endereco);

        var produto = produtoRepository.findAll().getFirst();
        var carrinho = carrinhoService.adicionarItem(cliente.getId(),
                new ItemCarrinhoDTO(produto.getId(), 2));
        carrinho = carrinhoService.definirEstrategiaFrete(cliente.getId(), "RETIRADA");
        carrinho = carrinhoService.aplicarCupom(cliente.getId(), "FEIRA10");

        assertEquals("RETIRADA", carrinho.estrategiaFrete());
        assertEquals("FEIRA10", carrinho.cupom());
        assertEquals(0, BigDecimal.ZERO.compareTo(carrinho.frete()));
        assertEquals(0, carrinho.subtotal().multiply(new BigDecimal("0.10")).compareTo(carrinho.desconto()));

        var pedido = pedidoService.finalizar(cliente.getId(), new PedidoRequestDTO(endereco.getId()));
        assertEquals(0, carrinho.subtotal().subtract(carrinho.desconto()).compareTo(pedido.total()));

        var carrinhoLimpo = carrinhoService.obter(cliente.getId());
        assertTrue(carrinhoLimpo.itens().isEmpty());
        assertNull(carrinhoLimpo.cupom());
        assertEquals("PADRAO", carrinhoLimpo.estrategiaFrete());
        assertFalse(carrinhoLimpo.itens().stream().findAny().isPresent());
    }

    @Test
    void itemRecemAdicionadoDeveVoltarComIdNaResposta() {
        var cliente = new Cliente();
        cliente.setNome("Teste Id do Item");
        cliente.setEmail("iditem@feiraviva.test");
        cliente.setSenhaHash("senha-de-teste");
        cliente = clienteRepository.save(cliente);

        var produto = produtoRepository.findAll().getFirst();
        var carrinho = carrinhoService.adicionarItem(cliente.getId(),
                new ItemCarrinhoDTO(produto.getId(), 1));

        var itemId = carrinho.itens().getFirst().id();
        assertNotNull(itemId);

        // o id devolvido já serve para o PUT /carrinho/itens/{itemId}
        carrinho = carrinhoService.alterarQuantidade(cliente.getId(), itemId, 2);
        assertEquals(2, carrinho.itens().getFirst().quantidade());
    }
}
