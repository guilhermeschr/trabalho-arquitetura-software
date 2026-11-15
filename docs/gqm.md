# Plano Goal/Question/Metric e interpretação

Base conceitual: Basili, Caldiera e Rombach (1994), *Goal Question Metric
Paradigm*, reproduzido da *Encyclopedia of Software Engineering*. O link da
fonte e os arquivos brutos de coleta acompanham a documentação. O método
parte de objetivos, deriva perguntas e seleciona medidas que respondem a elas;
medir um número isolado não estabelece qualidade nem causalidade.

## Dois objetos de avaliação

- **Now in Android:** análise documental de arquitetura, código e automação no
  commit fixado. Os indicadores abaixo são um plano de avaliação aplicável à
  referência; a compilação, os testes e o desempenho do original não foram
  executados neste trabalho.
- **News Architecture Demo:** execução de verificações estruturais, testes e
  experimento exploratório no emulador. Os resultados estão em
  `evidencias/resultados.json` e são interpretados apenas para esse artefato.

## G1 — Manutenibilidade

Analisar a organização do código para avaliar separação de responsabilidades,
do ponto de vista dos desenvolvedores e mantenedores, no contexto de evolução
de funcionalidades. São indicadores de estrutura, não uma medição completa
do esforço de manutenção.

| Pergunta | Métrica e unidade | Estratégia de coleta | Critério para a demonstração |
|---|---|---|---|
| Q1: o grafo de módulos contém ciclos? | Número de ciclos dirigidos | Extrair dependências `project(...)` dos quatro arquivos Gradle e percorrer o grafo | 0, pela regra adotada |
| Q2: uma funcionalidade depende diretamente de outra? | Número de arestas feature → feature | Filtrar o grafo de dependências | 0, pela regra adotada |
| Q3: a apresentação conhece tipos de persistência? | Referências indevidas, em ocorrências e arquivos | Buscar imports e referências a Room, banco, entidade e DAO nas features e no pacote UI compartilhado | 0; DAO/banco internos ao núcleo |

A busca textual detecta o recorte explicitado no script, sem demonstrar ausência
de toda forma possível de acoplamento. No original, considerar também os
módulos `api`/`impl` e suas regras antes de aplicar o mesmo critério.

## G2 — Testabilidade

Analisar a lógica e a apresentação para avaliar isolamento e verificabilidade,
do ponto de vista da qualidade, no contexto de mudanças pequenas e repetíveis.

| Pergunta | Métrica e unidade | Estratégia de coleta | Interpretação |
|---|---|---|---|
| Q1: quais classes relevantes são verificáveis com substitutos? | Quantidade e lista de classes avaliadas com fakes | Inspecionar os testes do caso de uso, repositório Room com DAO fake e três ViewModels | Cinco classes; não é percentual de cobertura de código |
| Q2: os casos automatizados previstos passam? | Aprovados / executados, em testes | Consolidar XML JUnit da suíte JVM e das instrumentações de Room/Compose | Todos os testes entregues devem passar; listar falhas se ocorrerem |
| Q3: qual o custo de execução da suíte unitária? | Tempo de parede, em segundos | Cronometrar uma chamada de `testDebugUnitTest`; registrar cache e tarefas executadas | Uma observação descritiva, sem extrapolar para outras máquinas |

O tempo da suíte pode incluir trabalho do Gradle e compilação incremental. O
relatório informa esse contexto; não equivale à soma de tempos dos métodos.
Não foi calculada uma taxa de cobertura de linhas nem feita análise de mutação.

## G3 — Desempenho

Caracterizar tempos e consumo do aplicativo de demonstração, do ponto de vista
de operação e usuário, em um único emulador ARM64 e APK debug. Não se pretende
comparar arquiteturas nem provar ganho causado por modularização.

| Pergunta | Métrica e unidade | Estratégia de coleta | Limite da medida |
|---|---|---|---|
| Q1: quanto leva iniciar o processo e exibir a Activity? | `TotalTime`, em ms; mediana, mínimo e máximo de 10 amostras | `am force-stop` seguido de `am start -W`; uma execução de aquecimento excluída | Não limpa cache de disco; não mede exatamente quando todo o feed terminou de desenhar |
| Q2: quanto leva uma gravação aparecer no fluxo combinado? | Tempo entre solicitação e recebimento da emissão correspondente, em ms; 10 operações alternadas | Instrumentação com Room em memória, coletor ativo e relógio monotônico | Não inclui o frame final de Compose, nem latência de um banco em disco |
| Q3: qual o uso de memória após o roteiro? | TOTAL PSS, em KiB e MiB | Uma captura `dumpsys meminfo` após percorrer telas, reiniciar processo e remover favorito | Amostra pontual; não é pico, média de uso nem memória em dispositivo físico |

As latências de Room em memória são um ensaio controlado de reatividade. A
persistência em disco é comprovada por outro teste; não se deve confundir os
dois experimentos. Sem uma meta externa ou baseline não se declara o aplicativo
"rápido" apenas pelos números encontrados.

## G4 — Usabilidade

Avaliar clareza das ações e execução de tarefas, do ponto de vista do leitor.
Apenas o recorte técnico de acessibilidade foi executado; os demais indicadores
são propostas, sem participantes nem respostas fabricadas.

| Pergunta | Métrica e unidade | Estratégia de coleta | Estado |
|---|---|---|---|
| Q1: os controles amostrados têm rótulos e área adequada? | Controles aprovados / 5; dimensão mínima em dp | Teste Compose de marcador na lista, duas abas, marcador nos detalhes e voltar; exigir 48 dp e ação/rotulagem | Executado; escopo restrito à amostra |
| Q2: leitores conseguem salvar, reencontrar e remover conteúdo? | Tarefas concluídas sem ajuda / tentativas, em % | Propor sessão com participantes reais e checklist de três tarefas; registrar ajuda e erros | Proposto; sem resultado |
| Q3: quanto demora realizar cada tarefa? | Tempo por tarefa, em segundos | Cronometrar as mesmas tarefas, registrando contexto e mediana por tarefa | Proposto; sem resultado |

Inspeção automatizada de rótulos e dimensões não substitui avaliação com pessoas,
leitor de tela, limitações motoras ou diferentes tamanhos de fonte.
