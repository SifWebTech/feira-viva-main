# 🛒 Feira Viva — Backend API

E-commerce de produtos locais — backend monolítico MVC em Spring Boot.

Java 21 LTS | Spring Boot 4.1.1 | Status: Módulo 2 concluído

## Sobre

Backend do projeto Feira Viva, desenvolvido na disciplina
Desenvolvimento para Servidores II (IDS002) em parceria com a
Associação de Produtores Locais. Resolve um problema real: levar a feira
presencial para a internet, permitindo compra de ponta a ponta.

**Stack:** Java 21 LTS · Spring Boot 4.1.1 · Maven · H2 (dev) · Jakarta EE 11 · springdoc-openapi

**Pacote base:** `br.com.feiraviva` · **Porta:** `8090`

## Como rodar

```bash
git clone https://github.com/SifWebTech/feira-viva-main.git
cd feira-viva-main
./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
```

- API: `http://localhost:8090`
- Swagger UI: `http://localhost:8090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/api-docs`
- H2 Console: `http://localhost:8090/h2-console`
  (JDBC: `jdbc:h2:mem:feiraviva` · user: `sa` · senha: vazia)

Para executar os testes:

```bash
./mvnw test                   # Windows: .\mvnw.cmd test
```

## Rotas principais

| Grupo | Rotas |
|---|---|
| Catálogo | `GET /produtos` · `GET /produtos/{id}` · `GET /categorias/arvore` |
| Clientes | `POST /clientes` · `GET /clientes/{id}` · `POST /clientes/{id}/enderecos` |
| Carrinho | `GET/POST/PUT/DELETE` em `/carrinho[/itens/...]` · `/carrinho/cupom` · `/carrinho/frete` |
| Pedidos | `POST /pedidos` · `GET /pedidos` · `GET /pedidos/{id}` · `POST /pedidos/{id}/cancelamento` |

Contrato completo e testável no Swagger UI.

## Contrato de erros

| Situação | Status | Corpo |
|---|---:|---|
| DTO inválido (`@Valid`) | 400 | `{"erro":"validacao","mensagem":"campo: msg ..."}` |
| Recurso inexistente | 404 | `{"erro":"nao_encontrado","mensagem":"..."}` |
| Regra de negócio | 409 | `{"erro":"regra_de_negocio","mensagem":"..."}` |
| JSON malformado | 400 | `{"erro":"json_invalido","mensagem":"..."}` |

## Padrões GoF aplicados

- Singleton (escopo Spring): `ConfiguracoesFeiraViva`
- Factory: `CupomFactory` (`FEIRA10`, `BEMVINDO`)
- Composite: árvore de categorias (`GET /categorias/arvore`)
- Strategy: cálculo de frete (`PADRAO`, `FIXO`, `RETIRADA`) via `CalculadoraFrete`

## Regras de negócio

- R1: estoque verificado ao adicionar e ao finalizar; devolvido no cancelamento
- R2: total = subtotal + frete − desconto
- R4: cancelamento só com status `CRIADO`
- R7: desconto fixo nunca supera o subtotal
- Frete delegado ao padrão Strategy (R6 hardcoded aposentada na Aula 11)

## Documentação de domínio

O contrato interno (entidades, relacionamentos, enums, status de implementação)
está em [docs/modelo.md](docs/modelo.md).

## Versionamento de API

As próximas versões públicas usarão o prefixo `/api/v1`. A migração das rotas
atuais fica para o Módulo 7, quando houver deploy e clientes externos.

## Próximos passos (Módulo 3 em diante)

- Migrações de banco com Flyway (substitui `ddl-auto=update`)
- Autenticação/autorização com Spring Security + JWT (remove o `?clienteId=` das rotas)
- Frontend React no módulo 4
- Integração ViaCEP/Maps e sandbox de pagamento
- Deploy em produção (Módulo 7)

## Licença

Projeto acadêmico de extensão — uso educacional e comunitário.
