# Análise arquitetural do Now in Android

## Recorte e método

Estudo documental e inspeção estática, consultados em 15/11/2026, na revisão
`2982e63cb691ad49cd009aab43dc206fee6699c2` (20/10/2026). O recorte privilegia a
funcionalidade For You, seus repositórios e as fontes locais. Não foi executada
uma compilação ou uma suíte de testes do aplicativo original. A demonstração
acadêmica é outro software; seus números não medem o Now in Android.

As evidências reproduzíveis estão em `evidencias/manifesto.json`, `commit.json`,
`pull-requests.json`, `atividade-teste.json` e no recorte do código em
`evidencias/nowinandroid/`. A contagem da busca GitHub `repo:android/nowinandroid
is:pr`, com todos os estados, foi 1.407. O número 103 da aba do repositório
corresponde a pull requests abertas, não ao total histórico. O último commit
consultado do teste `ForYouViewModelTest.kt` é de 16/10/2026. Esses registros
sustentam os critérios de pelo menos 200 PRs, atividade recente e testes de
2025 em diante. A busca tem data de coleta, pois o total pode mudar.

## Caracterização

O projeto do Google é uma referência de desenvolvimento Android construída
com Kotlin e Jetpack Compose. Como produto, apresenta conteúdos da série Now
in Android, permite acompanhar tópicos e salvar recursos. Como referência,
expõe práticas de arquitetura, testes e modularização. O público contempla
leitores interessados no ecossistema Android e desenvolvedores que estudam o
código. Há variantes `demo`, com dados locais, e `prod`, com acesso remoto.
A licença do repositório é Apache 2.0.

## Stakeholders e conflitos

Os papéis a seguir são categorias analíticas, não um organograma confirmado.
A existência das funções é apoiada nos artefatos; as prioridades e tensões
são inferências do estudo, sem entrevistas.

| Parte interessada | Evidência do papel | Interesse e necessidade inferidos |
|---|---|---|
| Usuários | Funcionalidades no README e telas | Encontrar conteúdo pertinente, salvar itens e acessar dados disponíveis sem rede |
| Desenvolvedores/contribuidores | Código, módulos e PRs | Entender limites e alterar funcionalidades com testes e builds viáveis |
| Mantenedores | PRs integradas, workflows, documentação | Preservar coerência, revisar contribuições e controlar complexidade |
| Produtores/curadores de conteúdo | Série Now in Android identificada no README | Oferecer conteúdo correto e atualizado; não foi confirmada uma integração editorial direta |
| Qualidade | Testes JVM, instrumentados e de screenshots | Reproduzir falhas e validar estados, persistência e navegação |
| Responsáveis pela plataforma Android | Dependências Android/Jetpack e objetivo de demonstrar boas práticas | Evoluir APIs e divulgar recomendações, mantendo uma referência compreensível |

| Tensão possível | Partes | Encaminhamento proposto |
|---|---|---|
| Conteúdo atualizado versus rede, bateria e disponibilidade | Leitor, curadoria e manutenção | Leitura local e sincronização agendada; explicitar que o dado local pode estar desatualizado |
| Adoção de APIs novas versus estabilidade e compreensão | Plataforma, contribuidores e qualidade | Fixar versões, revisar mudanças e automatizar testes; documentar migrações |
| Isolamento de funcionalidades versus complexidade do build | Desenvolvedores e mantenedores | Modularização proporcional ao tamanho e regras de dependência verificáveis |

## Pontos de vista

**Usuário:** telas Compose, seleção de tópicos e dados salvos dão forma à
personalização. A leitura a partir de armazenamento local permite exibir
conteúdo já persistido quando a rede está ausente; isso não garante atualização
permanente nem disponibilidade de links externos.

**Desenvolvimento:** `app` reúne inicialização e navegação; módulos `feature`
separam funcionalidades; `core` contém componentes compartilhados. Na revisão
estudada, funcionalidades como For You possuem divisão `api`/`impl`. A
demonstração deliberadamente não replica essa divisão.

**Operação/manutenção:** `SyncWorker` coordena sincronização e pode retornar
`Result.retry()`. Os repositórios persistem conteúdo local. O workflow
`.github/workflows/Build.yaml` declara testes locais, screenshots, build e lint.
A existência dessas etapas não comprova que todas as execuções estejam verdes.
Não foi auditado o histórico completo de falhas da integração contínua.

## Padrões e evidências

Todos os caminhos são relativos ao commit fixado. Prefixos Kotlin longos podem
ser localizados na árvore completa preservada em `evidencias/arvore.json`.

| Padrão/decisão | Onde se observa | Benefício esperado e custo/limitação |
|---|---|---|
| Camadas UI, Domain e Data | `feature/foryou/impl`, `core/domain`, `core/data` | Separa apresentação e acesso a dados; exige decisões claras sobre onde colocar regras |
| Apresentação com ViewModel, compatível com uma leitura MVVM | `ForYouViewModel.feedState`, telas Compose | Estado sobrevive a recriações da tela e pode ser testado; ViewModels podem crescer se responsabilidades se acumularem |
| Fluxo unidirecional e reatividade | `stateIn`, `StateFlow`, `combine` no ViewModel/repositório composto | Propaga alterações a observadores; traz semântica de assinaturas, concorrência e cancelamento |
| Repository | `NewsRepository`, `UserDataRepository`, `UserNewsResourceRepository` | Oculta fontes e permite substituição em testes; interfaces também precisam evoluir de forma coerente |
| Composição de dados | `CompositeUserNewsResourceRepository` | Combina notícias e preferências sem repetir a associação na UI; filtros e assinaturas adicionam complexidade |
| Modularização | `settings.gradle.kts`, módulos `feature/*/api` e `impl`, módulos `core` | Explicita dependências e responsabilidades; aumenta configuração e superfície de manutenção |
| Injeção de dependências | `core/data/.../di/DataModule.kt`, `@HiltViewModel`, `@AssistedInject` | Vincula implementações e facilita substitutos; depende de geração de código e configuração |
| Leitura local com sincronização | `OfflineFirstNewsRepository`, `NewsResourceDao`, `SyncWorker` | Conteúdo local disponível durante indisponibilidade de rede; requer sincronização e pode exibir dados antigos |

O termo MVVM descreve aqui a relação entre UI, ViewModel e dados, não toda a
arquitetura do sistema. UDF descreve a direção de eventos e atualizações; não
é sinônimo de MVVM. A arquitetura recomendada pelo Android admite ViewModels
acessando repositórios diretamente e uma camada de domínio opcional. Isso não
é evidência de aplicação integral das regras de dependência de Clean
Architecture ou de portas e adaptadores de Arquitetura Hexagonal.

## Fluxo real de For You

1. O ViewModel recebe `UserNewsResourceRepository`, `UserDataRepository` e
   `GetFollowableTopicsUseCase` por injeção. O feed começa com `Loading`.
2. `observeAllForFollowedTopics()` observa os tópicos seguidos no fluxo do
   usuário e usa `flatMapLatest` para obter notícias pertinentes. Sem tópicos,
   o repositório composto retorna uma lista vazia.
3. O composto associa recursos e dados do usuário com `combine` e
   `mapToUserNewsResources`. `OfflineFirstNewsRepository` expõe consultas Room;
   `OfflineFirstUserDataRepository` observa `NiaPreferencesDataSource`.
4. `feedState` converte a lista para `NewsFeedUiState.Success` e a expõe com
   `stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`.
5. Ao salvar, a UI chama `updateNewsResourceSaved(id, selected)`. O ViewModel
   chama `UserDataRepository.setNewsResourceBookmarked` em uma coroutine.
6. A implementação persiste a mudança no Proto DataStore. A nova emissão é
   combinada às notícias, e o estado da interface é atualizado.

A jornada arquitetural ainda menciona `GetUserNewsResourcesUseCase`, ausente
na árvore do commit. A análise prioriza o código e registra a divergência.
`GetFollowableTopicsUseCase` continua presente no domínio e combina tópicos
com preferências para o onboarding. Não se deve desenhar o caso de uso antigo
como participante da sequência atual do feed.

A sincronização é um fluxo complementar: o WorkManager executa `SyncWorker`,
que coordena os repositórios; a implementação de notícias consulta a rede e
atualiza Room. O diagrama de sequência entregue concentra-se na leitura local
e no favorito, sem afirmar que o ViewModel dispara uma requisição de rede.

## Testes e integração contínua observados

- `core/domain/src/test/.../GetFollowableTopicsUseCaseTest.kt`.
- `core/data/src/test/.../OfflineFirstNewsRepositoryTest.kt`.
- `core/database/src/androidTest/.../NewsResourceDaoTest.kt`.
- `feature/foryou/impl/src/test/.../ForYouViewModelTest.kt`.
- `feature/foryou/impl/src/androidTest/.../ForYouScreenTest.kt`.
- `.github/workflows/Build.yaml`, com `testDemoDebug`, `verifyRoborazziDemoDebug`,
  montagem de APKs e tarefas lint, entre outras etapas.

Essas são evidências da existência de testes e automação, não resultados de
execução desta pesquisa no projeto original.

## Referências principais

- [Repositório e revisão estudada](https://github.com/android/nowinandroid/tree/2982e63cb691ad49cd009aab43dc206fee6699c2).
- [Jornada de arquitetura](https://github.com/android/nowinandroid/blob/2982e63cb691ad49cd009aab43dc206fee6699c2/docs/ArchitectureLearningJourney.md).
- [Jornada de modularização](https://github.com/android/nowinandroid/blob/2982e63cb691ad49cd009aab43dc206fee6699c2/docs/ModularizationLearningJourney.md).
- [Arquitetura Android](https://developer.android.com/topic/architecture).
- [Camada de domínio](https://developer.android.com/topic/architecture/domain-layer).
