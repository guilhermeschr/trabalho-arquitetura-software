# Roteiro de apresentação — 15 minutos

Estudo de caso do Now in Android e demonstração local. Reservar mais cinco minutos para perguntas. Preencher nome(s) e instituição no primeiro slide.

## Slide 1 — Objetivo do trabalho (00:00–01:00)

O objetivo é compreender uma arquitetura real e aplicar suas decisões em uma demonstração menor. A análise toma uma revisão fixa do Now in Android, para que caminhos e diagramas possam ser conferidos. A entrega relaciona stakeholders, pontos de vista, GQM e implementação. A aplicação acadêmica é independente do original. Ao longo da apresentação, vou separar o que observamos no código, o que medimos na demonstração e o que continua sendo uma proposta de avaliação.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2

## Slide 2 — Caracterização e evidências (01:00–02:00)

O aplicativo apresenta conteúdos da série Now in Android e funciona também como referência de boas práticas. A consulta à API retornou 1.407 pull requests considerando todos os estados; o contador da aba mostra apenas as abertas. A revisão escolhida é de 20 de outubro de 2026. O histórico consultado de ForYouViewModelTest registra alteração em 16 de outubro de 2026. Há testes locais, instrumentados e de screenshots, além de integração contínua. Esses artefatos comprovam elegibilidade, mas não significam que executamos a suíte original.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2; docs/evidencias/pull-requests.json; docs/evidencias/atividade-teste.json

## Slide 3 — Partes interessadas (02:00–03:00)

Stakeholder é quem tem interesse ou pode ser afetado pelo sistema. Organizamos seis papéis, sem afirmar que existem seis equipes independentes no projeto. O usuário quer conteúdo relevante; o desenvolvedor precisa compreender e modificar o código; os mantenedores precisam de coerência. Curadoria, qualidade e plataforma trazem interesses complementares. As funcionalidades, contribuições e testes apoiam os papéis, enquanto as necessidades listadas são inferências analíticas. Não foram realizadas entrevistas.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2

## Slide 4 — Conflitos possíveis (03:00–04:00)

Esses conflitos são possíveis tensões entre interesses, e não relatos de incidentes comprovados. Atualizar sempre pode aumentar uso de rede e bateria; a leitura local com sincronização separa disponibilidade e atualização. Adotar novas APIs demonstra evolução, mas pode aumentar o custo de manutenção e dos testes. Modularizar ajuda a estabelecer limites, ao custo de mais configuração. A decisão precisa considerar o tamanho e o objetivo do sistema. Isso explica por que nossa demonstração usa quatro módulos em vez de reproduzir toda a referência.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2

## Slide 5 — Três perspectivas (04:00–06:00)

A mesma arquitetura pode ser examinada de perspectivas diferentes. Para o usuário, importam a navegação, a personalização por tópicos e o acesso a recursos salvos. Ler do armazenamento local mantém conteúdos que já existem no aparelho quando a rede cai, mas não garante que um link externo abra. Para o desenvolvimento, observamos a distribuição das responsabilidades e o grafo de dependências. O app reúne inicialização e navegação, as features organizam funcionalidades, e core reúne componentes compartilhados. No código atual, algumas features são divididas em api e impl. Para operação, observamos sincronização, armazenamento e automação. SyncWorker pode solicitar nova tentativa. O workflow declara testes, APKs e lint. Declarar essas etapas não comprova que todas as execuções históricas deram certo. As vistas ajudam a selecionar evidências adequadas a cada preocupação.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2

## Slide 6 — Plano GQM (06:00–07:30)

O GQM começa no objetivo, deriva perguntas e escolhe medidas que possam respondê-las. Para manutenibilidade, verificamos ciclos, dependências entre funcionalidades e referências indevidas à persistência. Para testabilidade, examinamos uso de fakes, aprovação dos testes e custo de execução. Para desempenho, coletamos tempos de abertura, emissão após gravação e uma amostra de memória. Para usabilidade, há um recorte técnico de controles e propostas de tarefas com pessoas. A tabela mostra um exemplo por atributo; o artigo apresenta três. No Now in Android, a avaliação é documental e o GQM é um plano. Os valores executados no próximo slide pertencem à demonstração.

Fontes e apoio: Basili, Caldiera e Rombach (1994): https://www.cs.umd.edu/~basili/publications/technical/T89.pdf; docs/gqm.md

## Slide 7 — Resultados e limites (07:30–09:00)

A suíte aprovou 36 testes unitários e seis instrumentados. Os primeiros cobrem caso de uso, repositórios e três ViewModels; os demais cobrem Room, navegação e uma amostra de controles. Os cinco controles examinados atenderam a rótulo e tamanho mínimo de 48 dp. O grafo não apresentou ciclos nem dependências entre features. Em dez reinícios do processo, a mediana de TotalTime foi 3,10 segundos. Houve uma execução de aquecimento excluída e o cache de disco foi preservado. A mediana até a emissão combinada após gravar foi 10,35 milissegundos, com Room em memória; isso não inclui o frame final de Compose. PSS foi uma captura pontual de 123,7 MiB após o roteiro. Sem baseline e aparelho físico, os números não provam superioridade da arquitetura.

Fontes e apoio: docs/evidencias/resultados.json; relatórios JUnit e logs de execução

## Slide 8 — Camadas e persistência (09:00–10:00)

A documentação apresenta UI, Domain e Data. O domínio é opcional e serve a regras complexas ou reutilizadas; ViewModels podem usar repositórios diretamente. Na apresentação, Compose observa estado e encaminha ações ao ViewModel, compatível com uma interpretação MVVM. Isso não resume toda a arquitetura e não equivale automaticamente a Clean Architecture. UDF caracteriza a direção dos eventos e atualizações. No original, Room armazena conteúdo, Proto DataStore guarda dados do usuário e WorkManager coordena sincronização. Flow transporta alterações até os observadores.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2; https://developer.android.com/topic/architecture/domain-layer

## Slide 9 — Sequência atual de favoritos (10:00–11:00)

A jornada ainda cita GetUserNewsResourcesUseCase, mas essa classe não aparece na revisão estudada. O feed atual usa UserNewsResourceRepository, implementado pelo repositório composto. A sequência apresentada é a de salvar um recurso: a tela envia o evento, o ViewModel chama o repositório de usuário e a preferência é persistida no DataStore. O composto observa os dados do usuário e combina o novo estado com as notícias. A emissão passa ao feedState, e Compose atualiza a tela. GetFollowableTopicsUseCase permanece no domínio, relacionado ao onboarding. A divergência entre documentação e código foi registrada em vez de reproduzir o exemplo antigo.

Fontes e apoio: https://github.com/android/nowinandroid/blob/2982e63cb691ad49cd009aab43dc206fee6699c2/feature/foryou/impl/src/main/kotlin/com/google/samples/apps/nowinandroid/feature/foryou/impl/ForYouViewModel.kt

## Slide 10 — Arquitetura simplificada (11:00–12:00)

A demonstração mantém a ideia de funcionalidades independentes e componentes compartilhados, mas usa quatro módulos. Dentro do núcleo, camadas são pacotes, uma simplificação que reduz configuração e oferece limites menos fortes que módulos distintos. O caso de uso combina catálogo e favoritos e ordena os itens. Ele é usado pelos três ViewModels. Room guarda somente os identificadores salvos. Não há rede nem WorkManager. FeedStateHolder compartilha comportamento de apresentação por composição; não é uma classe de domínio. As features não acessam DAO e o app coordena navegação por callbacks. A diferença de armazenamento em relação ao original é deliberada.

Fontes e apoio: projeto-base/README.md; docs/decisoes-arquiteturais.md

## Slide 11 — Roteiro prático (12:00–14:00)

No emulador, abrir a primeira notícia e tocar no marcador. Voltar à lista e entrar em Favoritos, mostrando que o item já está presente sem recarregamento manual. Encerrar o processo com o comando de apoio e abrir novamente; entrar em Favoritos para mostrar a persistência. Remover o item e observar a lista vazia. Voltar a Notícias e conferir o marcador desmarcado. Durante as ações, apontar o caminho: evento da UI, ViewModel, repositório, Room e emissão por Flow. Se o emulador não estiver disponível, reproduzir demonstracao.mp4, que acompanha os slides. As capturas mostram os estados do roteiro realmente executado.

Fontes e apoio: docs/evidencias/demonstracao.json; apresentacao/demonstracao.mp4

## Slide 12 — Conclusão (14:00–15:00)

A principal contribuição é conectar decisões observadas em um projeto real a uma implementação menor e verificável. O confronto entre documentação e código evitou representar um fluxo antigo como atual. A demonstração concretizou navegação, persistência e atualização reativa, com testes e registros. Stakeholders e GQM ajudaram a formular perguntas e limitar as conclusões. Não provamos que essa arquitetura é superior a outras, nem medimos o original. Como continuidade, propomos avaliação com pessoas e medições em aparelhos físicos. A fonte remota e as políticas de sincronização só seriam acrescentadas se o escopo exigisse. A seguir, podemos discutir os compromissos das escolhas.

Fontes e apoio: https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2

## Operação da demonstração

1. Antes da aula, iniciar o emulador, instalar o APK e conferir abertura.
2. Mostrar a lista, abrir a primeira notícia e salvar no marcador.
3. Voltar e entrar em Favoritos; apontar a atualização sem recarregar.
4. Encerrar o processo e abrir novamente. O favorito deve continuar em Favoritos.
5. Remover o item e mostrar a lista vazia; retornar à lista principal.
6. Se o emulador falhar, abrir `demonstracao.mp4`, uma gravação do percurso executado.

O encerramento pode ser feito pelo terminal de apoio:

```sh
adb shell am force-stop br.edu.arquitetura.newsdemo
adb shell am start -n br.edu.arquitetura.newsdemo/br.edu.arquitetura.newsdemo.app.MainActivity
```

Não usar “limpar dados” nessa etapa, pois isso apaga o banco e não demonstra persistência após encerrar o processo.
