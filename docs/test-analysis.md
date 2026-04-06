# Análise de testes - Busca de artigos do Blog do Agi

## Escopo analisado

Funcionalidade de pesquisa de artigos iniciada pela lupa no canto superior direito do blog.

## Abordagem de QA aplicada

A seleção dos cenários priorizou impacto em negócio, frequência de uso e risco funcional:

1. **Caminho feliz da busca**
   - risco: usuário não localizar conteúdo relevante
   - impacto: frustração do usuário, queda em engajamento e SEO interno

2. **Busca sem resultados**
   - risco: página exibir resultados indevidos ou experiência inconsistente
   - impacto: perda de confiança e baixa previsibilidade

## Decisão de automação

Os cenários Web foram implementados em **BDD/Cucumber** para que o comportamento esperado da busca fique legível no formato de negócio e aderente ao pedido de demonstrar cenários relevantes do desafio.

## Técnicas utilizadas

- Particionamento por equivalência
  - termo válido
  - termo inválido/inexistente
- Análise de risco
  - busca é uma entrada de navegação crítica para descoberta de conteúdo
- Cobertura multicamada
  - UI (Selenium)
  - API (REST Assured)
  - performance smoke

## Casos sugeridos para Zephyr Scale

| ID sugerido | Tipo | Cenário | Prioridade |
|---|---|---|---|
| AGI-SEARCH-WEB-001 | Web | Pesquisar artigo com termo válido via header | Alta |
| AGI-SEARCH-WEB-002 | Web | Pesquisar artigo com termo inexistente e validar estado vazio | Alta |
| AGI-SEARCH-API-001 | API | Buscar posts por termo válido via `wp-json/wp/v2/posts` | Alta |
| AGI-SEARCH-API-002 | API | Buscar posts por termo inexistente e validar coleção vazia | Média |
| AGI-SEARCH-PERF-001 | Performance | Medir tempo médio e P95 da busca pública | Média |

## Cenários exploratórios recomendados

- termos com acentuação: `empréstimo`
- termos com caixa mista: `EmPrEsTiMo`
- busca com espaços em branco no início/fim
- busca com caracteres especiais
- uso da busca em mobile/responsive
- comportamento com múltiplas pesquisas consecutivas
- consistência entre resultados Web e API

## Riscos técnicos observados

- O site é público e sujeito a variação de conteúdo.
- Pode ocorrer rate limit (`429`) em chamadas HTTP se a execução for agressiva.
- O DOM é baseado em tema WordPress/Astra; mudanças visuais podem impactar seletores.

## Mitigações aplicadas

- Seletores baseados em atributos e classes semânticas do componente de busca
- Retry simples para `429` nos testes não funcionais/API
- Tags por suíte (`web`, `api`, `performance`) para execução segmentada
