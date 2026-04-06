# Plano de teste de performance - Busca do Blog do Agi

## Objetivo

Medir um baseline técnico do tempo de resposta da busca pública do Blog do Agi, usando um smoke de performance de baixa agressividade e seguro para CI.

## Escopo

- endpoint/página alvo: `/?s=<termo>`
- termo padrão: `emprestimo`
- protocolo: HTTPS

## Estratégia

A execução atual é um **smoke de performance** e não um teste de carga massiva. O foco é detectar degradações grosseiras durante a avaliação técnica.

## Métricas acompanhadas

- tempo médio de resposta
- P95
- sucesso HTTP 200
- presença de conteúdo funcional na resposta

## Workload padrão

- 2 requisições de aquecimento
- 8 requisições medidas
- pausa de 250 ms entre execuções

## Critérios iniciais de aceitação

- média <= 2500 ms
- P95 <= 4000 ms
- erro HTTP = 0%

## Evoluções sugeridas

- teste concorrente com ferramenta dedicada
- cenários com múltiplos termos de busca
- execução por janelas horárias diferentes
- correlação com observabilidade/APM
