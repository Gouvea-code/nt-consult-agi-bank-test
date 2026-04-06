# Desafio técnico QA - Blog do Agi

Projeto de automação criado em **Java + Selenium + Cucumber (BDD)** para a frente Web, complementado com **REST Assured** para API e uma suíte de **performance smoke** em Java para validar o fluxo de busca de artigos do Blog do Agi.

## Objetivo

Validar a funcionalidade de pesquisa de artigos (ícone de lupa no cabeçalho) com foco em cenários críticos para o usuário e para o negócio:

- **Busca com termo válido**: garante que o usuário encontre conteúdos aderentes ao que pesquisou.
- **Busca com termo inexistente**: garante comportamento previsível, sem resultados incorretos e com preservação do termo pesquisado.

Além disso, o projeto também entrega:

- **Testes de API** usando o endpoint público do WordPress (`wp-json/wp/v2/posts`).
- **Teste de performance smoke** para acompanhar tempo médio e P95 da busca.
- **Postman collection** para uso exploratório/manual.
- **GitLab CI/CD** e **GitHub Actions** para execução automatizada.
- **Documentação de estratégia e planejamento** para reforçar a abordagem de QA Sênior.

## Stack adotada

- Java 17
- Maven 3.9+
- Selenium WebDriver
- Cucumber + JUnit Platform
- REST Assured
- WebDriverManager

## Estrutura do projeto

- `src/test/resources/features/web`: cenários BDD da busca do blog
- `src/test/java/br/com/agibank/qa/bdd/web`: runner, hooks e steps do Cucumber
- `src/test/java/br/com/agibank/qa/tests/api`: testes de API com REST Assured
- `src/test/java/br/com/agibank/qa/tests/performance`: performance smoke
- `src/test/java/br/com/agibank/qa/pages`: page objects
- `docs`: estratégia, análise e plano de performance
- `postman`: collection para execução manual
- `.gitlab-ci.yml`: pipeline GitLab
- `.github/workflows/ci.yml`: pipeline GitHub Actions

## Pré-requisitos

- Java 17 configurado no `JAVA_HOME`
- Maven 3.9+
- Google Chrome ou Microsoft Edge instalado
- Internet para acessar o ambiente público do Blog do Agi

> Observação: por padrão a automação roda em **headless**. Para acompanhar a execução visualmente, use `-Dheadless=false`.

## Como executar

### Executar toda a suíte

```bash
mvn clean test
```

### Executar apenas Web em BDD/Cucumber

```bash
mvn clean test -Pweb -Dheadless=true -Dbrowser=chrome
```

### Executar apenas API

```bash
mvn clean test -Papi
```

### Executar apenas Performance

```bash
mvn clean test -Pperformance
```

## Parâmetros úteis

Você pode customizar a execução por propriedades Maven/Sistema:

```bash
mvn clean test -Pweb -Dheadless=false -Dbrowser=chrome -Dvalid.search.term=emprestimo
```

Principais propriedades:

- `baseUrl` (default: `https://blog.agibank.com.br/`)
- `apiBaseUrl` (default: `https://blog.agibank.com.br/wp-json/wp/v2`)
- `browser` (`chrome` ou `edge`)
- `headless` (`true` / `false`)
- `valid.search.term` (default: `emprestimo`)
- `invalid.search.term` (default: `termoinexistenteagixpto`)
- `performance.p95.threshold.ms` (default: `4000`)
- `performance.avg.threshold.ms` (default: `2500`)

## Evidências geradas

- Screenshots de falha: `target/screenshots`
- Relatório da performance smoke: `target/performance/search-performance-smoke-report.json`
- Relatório HTML do Cucumber Web: `target/cucumber/web-report.html`
- Relatório JSON do Cucumber Web: `target/cucumber/web-report.json`

## Cenários automatizados

### Web

Os cenários Web foram reescritos em BDD/Cucumber porque isso deixa mais explícito o comportamento esperado pelo desafio e facilita a leitura para avaliadores técnicos e não técnicos.

1. **Buscar artigos com um termo aderente ao conteúdo do blog**
   - acessa a home do Blog do Agi
   - usa a lupa do cabeçalho para pesquisar
   - valida redirecionamento para a página de resultados
   - valida artigos aderentes ao termo pesquisado
   - valida persistência do termo no campo de busca

2. **Buscar artigos com um termo inexistente**
   - acessa a home do Blog do Agi
   - usa a lupa do cabeçalho para pesquisar
   - valida redirecionamento para a página de resultados
   - valida estado vazio da busca
   - valida ausência de artigos e persistência do termo

Arquivo dos cenários: `src/test/resources/features/web/blog_search.feature`

### API

1. Busca por termo conhecido retorna `200` e posts aderentes
2. Busca por termo inexistente retorna `200` com lista vazia

### Performance smoke

1. Mede tempo médio e P95 do endpoint de busca da aplicação pública
2. Gera artefato simples para acompanhamento técnico

## Zephyr Scale

O mapeamento sugerido de casos de teste para Zephyr Scale está descrito em `docs/test-analysis.md`, permitindo cadastro rápido dos cenários no gerenciamento de testes.

## Postman

Importe a collection em `postman/AgiBlogSearch.postman_collection.json` para execução manual da API de busca.

## Pipeline

- GitLab CI: `.gitlab-ci.yml`
- GitHub Actions: `.github/workflows/ci.yml`