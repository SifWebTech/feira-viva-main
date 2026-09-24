package br.com.feiraviva.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI feiraVivaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Feira Viva API")
                        .version("0.9.0")
                        .description("Backend MVC do e-commerce Feira Viva. Documentacao gerada por springdoc-openapi.")
                        .contact(new Contact().name("Fabrica de Software").email("fabrica@feira-viva.local"))
                        .license(new License().name("Uso educacional")))
                .tags(List.of(
                        new Tag().name("Catalogo").description("Produtos e categorias"),
                        new Tag().name("Clientes").description("Cadastro e enderecos"),
                        new Tag().name("Carrinho").description("Itens, cupom e estrategia de frete"),
                        new Tag().name("Pedidos").description("Finalizacao, historico e cancelamento")
                ));
    }
}
