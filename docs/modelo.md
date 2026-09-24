# Feira Viva — Modelo de Domínio

> Documento vivo do modelo de domínio do projeto.
> **Stack:** Java 21 LTS • Spring Boot 4.1.1 • Maven • React (Vite)
> **Pacote base:** `br.com.feiraviva`
> **Ambiente de desenvolvimento:** H2 em memória (`jdbc:h2:mem:feiraviva`) • porta **8090** • console em `/h2-console` (user `sa`, senha vazia)
> **Versão:** 0.9 — atualizado na Aula 12 (Módulo 2 concluído)

## 1. Identificação

| Item | Definição |
|---|---|
| **Projeto** | Feira Viva — loja online de produtos locais |
| **Arquitetura** | Monólito modular baseado em MVC |
| **Stack Backend** | Java 21 (LTS) · Spring Boot 4.1.1 · Maven · Jakarta EE 11 |
| **Stack Frontend** | React + Vite (a partir da Aula 17) |
| **Pacote base** | `br.com.feiraviva` |
| **Model** | Entidades + regras de negócio (Spring/Jakarta) |
| **View** | Interface React |
| **Controller** | Rotas/endpoints (Spring MVC) |
| **Parceiro** | Associação de Produtores Locais |
| **Problema real** | Produtores vendem apenas na feira presencial; a comunidade não compra fora do horário da feira |
| **Público-alvo** | Moradores da região e pequenos produtores |

## Atualização v0.9 — Documentação e organização

### Singleton e Factory

- `ConfiguracoesFeiraViva` é o singleton imutável das configurações de frete, loja e moeda.
- O endpoint didático `/debug/instancias` foi removido antes da entrega do backend
  (endpoint de debug removido — singleton já comprovado em aula).
- `CupomFactory` cria `FEIRA10` (10%) e `BEMVINDO` (R$ 15,00); o carrinho persiste somente o código.
- **R7:** desconto fixo não pode superar o subtotal.

### Composite e Strategy

- `Categoria` implementa o Composite uniforme com `categoriaPai` e `subcategorias`.
  `GET /categorias/arvore` entrega a árvore recursiva.
- O seeder cria Horta e Orgânicos/Laticínios com Verduras, Temperos e Queijos como subcategorias.
- `StrategyFrete` possui `PADRAO`, `FIXO` e `RETIRADA`; `CalculadoraFrete` seleciona a estratégia pelo código.
- `POST /carrinho/frete?clienteId=&tipo=` define a estratégia. Tipos inválidos retornam 404.
- Na finalização, frete e desconto compõem o total do pedido; itens, cupom e estratégia são limpos.

### Documentação e acesso

- Swagger UI: `http://localhost:8090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/api-docs`
- H2 Console: `http://localhost:8090/h2-console`
- Convenção documentada para versões futuras: prefixo `/api/v1`.

### Padrões GoF aplicados

| Padrão | Implementação |
|---|---|
| Singleton | Bean Spring `ConfiguracoesFeiraViva` |
| Factory | `CupomFactory` e cupons `FEIRA10`/`BEMVINDO` |
| Composite | Árvore recursiva de `Categoria` |
| Strategy | `CalculadoraFrete` com `PADRAO`, `FIXO` e `RETIRADA` |

### Registro da versão

**0.9 — Aula 12:** Swagger UI com springdoc-openapi, README final do backend,
anotações OpenAPI, remoção do endpoint de debug e consolidação dos padrões GoF.
Endpoint de debug removido — singleton já comprovado em aula.

### Rotas das aulas

| Rota | Método | Finalidade |
|---|---|---|
| `/carrinho/cupom` | POST / DELETE | Aplicar/remover cupom |
| `/carrinho/frete` | POST | Definir estratégia de frete |
| `/categorias/arvore` | GET | Consultar a árvore de categorias |

## 2. MVP

Compra de ponta a ponta: catálogo → detalhe do produto → carrinho → login/cadastro → endereço de entrega → confirmação do pedido (pagamento simulado).

## 3. Entidades (Model)

| Entidade | Principais atributos | Relacionamentos | Status |
|---|---|---|---|
| **Produto** | id, nome, descricao, preco, sku, estoque, ativo, urlImagem | N:1 Categoria | ✅ Aula 07 |
| **Categoria** | id, nome, descricao, categoriaPai, subcategorias | 1:N Produto; N:1 Categoria (árvore Composite) | ✅ Aula 11 |
| **Cliente** | id, nome, email, senhaHash, telefone, papel | 1:N Endereco; 1:N Pedido; 1:1 Carrinho | ✅ Aula 08 |
| **Endereco** | id, cep, logradouro, numero, complemento, bairro, cidade, uf | N:1 Cliente; 1:N Pedido | ✅ Aula 08 |
| **Carrinho** | id, dataCriacao, codigoCupom, estrategiaFrete (totais **calculados no Service**) | 1:1 Cliente; 1:N ItemCarrinho | ✅ Aula 11 |
| **ItemCarrinho** | id, quantidade, precoUnitario | N:1 Carrinho; N:1 Produto | ✅ Aula 09 |
| **Pedido** | id, numero (`FV-0001`), status, subtotal, frete, total, dataCriacao | N:1 Cliente; N:1 Endereco; 1:N ItemPedido; 1:1 Pagamento | ✅ Aula 11 |
| **ItemPedido** | id, quantidade, precoUnitario (**snapshot**) | N:1 Pedido; N:1 Produto | ✅ Aula 09 |
| **Pagamento** | id, metodo, status, idTransacao | 1:1 Pedido | 🕒 planejado (Aula 24) |

> **Snapshot de preço (Aula 09):** o `ItemPedido.precoUnitario` congela o preço no momento da compra; alterações futuras de preço não afetam pedidos históricos.

### Enums

- `Papel`: `CLIENTE`, `ADMIN` — hoje persistido como `String` (`papel`); vira enum + JWT nas Aulas 14–15
- `PedidoStatus`: `CRIADO`, `PAGO`, `EM_PREPARO`, `ENVIADO`, `ENTREGUE`, `CANCELADO` — ✅ Aula 09 (`@Enumerated(STRING)`)
- `MetodoPagamento`: `CARTAO`, `PIX`, `BOLETO` — 🕒 planejado (Aula 24)
- `PagamentoStatus`: `APROVADO`, `RECUSADO`, `PENDENTE` — 🕒 planejado (Aula 24)

### Dados de desenvolvimento (DataSeeder)

- Categorias: *Horta e Orgânicos*, *Laticínios*
- Produtos: Mel orgânico (estoque 12), Queijo minas (8), Café da serra (20)

## 4. Diagrama de Relacionamentos

```text
Cliente (1) ──── (N) Endereco
Cliente (1) ──── (N) Pedido
Cliente (1) ──── (1) Carrinho
Carrinho (1) ──── (N) ItemCarrinho (N) ──── (1) Produto
Pedido (1) ──── (N) ItemPedido (N) ──── (1) Produto
Pedido (N) ──── (1) Endereco
Pedido (1) ──── (1) Pagamento            (planejado — Aula 24)
Produto (N) ──── (1) Categoria
Categoria (N) ──── (1) Categoria (categoriaPai)
```

## 5. Regras de Negócio

| ID | Regra | Camada | Status |
|---|---|---|---|
| **R1** | Produto com estoque zero não entra no carrinho; quantidade nunca excede o estoque; baixa na finalização e devolução no cancelamento | Service / Model | ✅ Aula 09 |
| **R2** | Total do pedido = soma dos itens (snapshot) + frete − desconto | Service | ✅ Aula 11 |
| **R3** | Pedido exige cliente autenticado; rotas `/admin/**` exigem papel `ADMIN` | Security / Controller | 🕒 Aulas 14–15 |
| **R4** | Cancelamento pelo cliente só com status `CRIADO` (devolve estoque) | Service | ✅ Aula 09 |
| **R5** | Senhas armazenadas com hash; autenticação via JWT | Security | 🕒 Aulas 14–15 |
| **R6** | Frete calculado pela Strategy `PADRAO`, `FIXO` ou `RETIRADA` | Service / Strategy | ✅ Aula 11 |
| **R7** | Desconto fixo nunca supera o subtotal | Factory / Model | ✅ Aula 10 |

## 6. Leitura MVC

### View (telas em React — a partir da Aula 17)

1. Home/Catálogo (busca e filtro por categoria)
2. Detalhe do produto
3. Carrinho
4. Login/Cadastro
5. Checkout (endereço via CEP + pagamento)
6. Confirmação do pedido *(iteração)*
7. Meus pedidos *(iteração)*
8. Administração *(iteração)*

### Controller (rotas Spring MVC)

| Rota | Método | Finalidade | Acesso | Status |
|---|---|---|---|---|
| `/produtos` | GET | Listar com filtros | Público | ✅ Aula 06/07 |
| `/produtos/{id}` | GET | Detalhar | Público | ✅ Aula 07 |
| `/categorias/arvore` | GET | Consultar árvore de categorias | Público | ✅ Aula 11 |
| `/clientes` | POST | Cadastrar cliente (201) | Público | ✅ Aula 08 |
| `/clientes/{id}` | GET | Consultar cliente c/ endereços | Público → autenticado | ✅ Aula 08 |
| `/clientes/{id}/enderecos` | POST | Adicionar endereço (201) | Público → autenticado | ✅ Aula 08 |
| `/carrinho` | GET | Exibir carrinho | `?clienteId=` | ✅ Aula 09 |
| `/carrinho/itens` | POST | Adicionar item (201) | `?clienteId=` | ✅ Aula 09 |
| `/carrinho/itens/{itemId}` | PUT | Alterar quantidade | `?clienteId=&quantidade=` | ✅ Aula 09 |
| `/carrinho/itens/{itemId}` | DELETE | Remover item | `?clienteId=` | ✅ Aula 09 |
| `/carrinho/cupom` | POST / DELETE | Aplicar/remover cupom | `?clienteId=` | ✅ Aula 10 |
| `/carrinho/frete` | POST | Definir Strategy de frete | `?clienteId=&tipo=` | ✅ Aula 11 |
| `/pedidos` | POST | Finalizar pedido (201) | `?clienteId=` | ✅ Aula 09 |
| `/pedidos` | GET | Histórico do cliente | `?clienteId=` | ✅ Aula 09 |
| `/pedidos/{id}` | GET | Detalhar pedido | `?clienteId=` | ✅ Aula 09 |
| `/pedidos/{id}/cancelamento` | POST | Cancelar (R4) | `?clienteId=` | ✅ Aula 09 |
| `/auth/login` · `/auth/registro` | POST | Autenticar / registrar (JWT) | Público | 🕒 Aulas 14–15 |
| `/admin/produtos` | POST/PUT/DELETE | Gerenciar produtos | `ADMIN` | 🕒 iteração |
| `/admin/pedidos/{id}/status` | PUT | Atualizar status | `ADMIN` | 🕒 iteração |

> ⚠️ **Dívida técnica (registrada na Aula 09):** o `clienteId` viaja como **query parameter** porque ainda não há autenticação. Nas Aulas 14–15 ele será substituído pelo principal do **token JWT** e estes parâmetros desaparecerão.

### Tratamento de erros (contrato da API)

| Situação | Status | Corpo |
|---|---|---|
| Criação com sucesso | 201 | DTO de resposta |
| Consulta com sucesso | 200 | DTO de resposta |
| DTO inválido (`@Valid`) | 400 | `{"erro":"validacao","mensagem":"campo: msg | ..."}` |
| Recurso inexistente | 404 | `{"erro":"nao_encontrado","mensagem":"..."}` |
| Regra de negócio (estoque, e-mail duplicado, cancelamento) | 409 | `{"erro":"regra_de_negocio","mensagem":"..."}` |
| JSON malformado | 400 | `{"erro":"json_invalido","mensagem":"..."}` |

## 7. Estrutura de Pacotes

```text
feira-viva/
├── docs/
│   └── modelo.md                  → este documento
├── frontend/                      → View (React + Vite) — a partir da Aula 17
└── src/main/java/br/com/feiraviva/
    ├── FeiravivaApplication.java
    ├── model/                     → Produto, Categoria, Cliente, Endereco,
    │                                 Carrinho, ItemCarrinho, Pedido, ItemPedido,
    │                                 PedidoStatus
    ├── repository/                → ProdutoRepository, CategoriaRepository,
    │                                 ClienteRepository, CarrinhoRepository,
    │                                 PedidoRepository (+ itens)
    ├── service/                   → ProdutoService, ClienteService,
    │                                 CarrinhoService, PedidoService (R1, R2, R4, R6)
    ├── controller/                → ProdutoController, ClienteController,
    │                                 CarrinhoController, PedidoController
    ├── dto/                       → records de entrada/saída (ClienteDTO,
    │                                 EnderecoDTO, ItemCarrinhoDTO, PedidoRequestDTO,
    │                                 CarrinhoResponseDTO, PedidoResponseDTO, ...)
    ├── exception/                 → ResourceNotFoundException, RegraDeNegocioException,
    │                                 ApiErrorHandler (@RestControllerAdvice)
    └── config/                    → DataSeeder, H2ConsoleConfig (dev)
```

## 8. Iterações Futuras

- **Aula 13:** tabelas viram migrations versionadas com Flyway (substitui `ddl-auto=update`)
- **Aulas 14–15:** BCrypt + JWT (R3, R5); remoção do query param `clienteId`
- **Aula 23:** ViaCEP/Google Maps — frete por CEP e pontos de retirada
- **Aula 24:** Pagamento 1:1 com Pedido (sandbox); status `PAGO` deixa de ser manual
- **Iterações:** painel admin, acompanhamento de pedido, retirada na barraca, cupons

## 9. Histórico de Versões

| Versão | Aula | Mudança |
|---|---|---|
| `0.1` | 04 | Levantamento inicial: 5 entidades, 5 telas, 5 rotas, 3 regras e MVP |
| `0.2` | 05 | Modelo completo: 9 entidades, enums, regras R1–R5 e leitura MVC |
| `0.3` | 06 | Estrutura em código: pacote `br.com.feiraviva`, Java 21, Spring Boot 4.1.1, `GET /produtos` com mock |
| `0.4` | 07 | JPA/Hibernate: Produto e Categoria persistidos (H2), consultas derivadas |
| `0.5` | 08 | Cliente/Endereco, DTOs, validação (`jakarta.validation`) e erros centralizados (400/404/409) |
| `0.6` | 09 | Carrinho/Pedido: snapshot de preço, estoque (R1), frete provisório (R6), status e cancelamento (R4); dívida técnica `clienteId` registrada |
| `0.7` | 10 | GoF: singleton `ConfiguracoesFeiraViva`, `CupomFactory` + hierarquia `Cupom` (value objects), cupom no carrinho (`codigo_cupom`, rotas POST/DELETE `/carrinho/cupom`), `CarrinhoResponseDTO` com `cupom`/`desconto`, regra R7, R2/R6 atualizadas, `/debug/instancias` provisório e dívidas registradas |
| `0.8` | 11 | Composite em categorias e Strategy de frete: árvore recursiva, seeder com subcategorias, estratégias `PADRAO`/`FIXO`/`RETIRADA`, cupom e estratégia integrados à finalização do pedido |
| `0.9` | 12 | Swagger/OpenAPI, README final do backend, documentação v0.9, convenção `/api/v1` e remoção do endpoint de debug (singleton já comprovado em aula) |
