# Perguntas prováveis

**Qual a diferença entre MVC e MVVM?**
Em MVC, o controller recebe interações e coordena mudanças envolvendo modelo e
visão, conforme a implementação do padrão. Em MVVM, o ViewModel expõe estado e
ações consumidos pela visão. Aqui Compose observa StateFlow e envia eventos.
MVVM não exige ligação bidirecional e não descreve toda a arquitetura do app.

**Por que usar camadas?**
Para separar apresentação, composição de regras e acesso aos dados. Isso torna
responsabilidades identificáveis e permite testar partes com substitutos. O
custo é a existência de contratos e pontos adicionais de coordenação. A
separação não garante qualidade automaticamente.

**Por que quatro módulos e não o mesmo número do Now in Android?**
O escopo é uma aplicação de três telas. Separar app, núcleo e duas features
permite demonstrar dependências em uma direção sem reproduzir dezenas de
configurações. O custo dessa simplificação é que as camadas do núcleo têm
limites de pacotes, menos fortes que módulos de compilação independentes.

**O que o ViewModel faz? Ele salva dados depois que o processo morre?**
Coordena estado e eventos da tela e possui escopo para coroutines. Pode
sobreviver a recriações de configuração, mas não mantém sozinho os dados após
morte do processo. Na demonstração, Room fornece a persistência dos favoritos;
o SavedStateHandle recebe o identificador de navegação do detalhe.

**Qual a função do repositório?**
Oferecer um contrato de dados e concentrar o acesso às fontes. A UI não conhece
Room nem o DAO. Interfaces possibilitam usar implementações reais ou fakes.
O domínio combina notícias e identificadores favoritos quando essa regra é
reutilizada.

**Por que existe uma camada de domínio se ela é opcional no Android?**
O caso de uso combina duas fontes observáveis e ordena o resultado, regra
compartilhada pelos três ViewModels. Isso evita duplicação. Não foram criados
casos de uso para repassar toda chamada simples ao banco.

**Por que o favorito fica no Room, se o original usa DataStore?**
É uma simplificação acadêmica deliberada para demonstrar persistência
estruturada com DAO, chave primária e testes em arquivo. No original, conteúdo
está em Room e dados do usuário em Proto DataStore. O artigo diferencia as duas
implementações.

**O app demonstrativo é offline-first como o original?**
Ele funciona inteiramente com dados locais. Não implementa sincronização de
fontes remotas, filas, conflitos nem novas tentativas de rede. Portanto,
demonstra disponibilidade e persistência locais, sem reproduzir a complexidade
da sincronização offline-first do original.

**Por que o diagrama não mostra GetUserNewsResourcesUseCase?**
A documentação ainda cita esse nome, mas ele não existe na revisão fixada.
O código usa UserNewsResourceRepository e sua implementação composta.
GetFollowableTopicsUseCase continua no domínio para tópicos/onboarding. O
artigo prioriza o código e registra a divergência.

**É Clean Architecture?**
Não foi feita essa classificação. A referência segue a arquitetura oficial
Android, que permite UI usando repositórios e domínio dependendo de dados.
Adotar camadas, MVVM e repositórios não prova aplicação de todas as regras de
Clean Architecture ou de portas e adaptadores de Arquitetura Hexagonal.

**Como a arquitetura influencia qualidade?**
Ela cria condições e custos: interfaces ajudam a substituir dependências;
módulos explicitam limites; fluxos observáveis propagam mudanças. Mas esses
mecanismos podem aumentar configuração e complexidade. Para afirmar melhoria,
seria necessário estabelecer objetivos, medir e comparar em contexto adequado.

**O que o GQM acrescenta?**
Relaciona o motivo da avaliação às perguntas e às métricas. Por exemplo, para
avaliar isolamento de funcionalidades, perguntamos se há dependência direta
entre features e contamos essas arestas no grafo Gradle. O número responde uma
pergunta específica, sem resumir toda a manutenibilidade.

**Os 42 testes comprovam que o aplicativo não tem defeitos?**
Não. Eles cobrem os comportamentos definidos e passaram no ambiente registrado.
Não foi medida cobertura de linhas nem aplicada análise de mutação. Outros
ambientes e interações podem revelar problemas.

**Os números de tempo mostram que essa arquitetura é mais rápida?**
Não há comparação controlada. Os tempos vêm de APK debug em um emulador; o
cache de disco foi mantido. A latência de favorito usa Room em memória e termina
na emissão do fluxo, não no frame final de Compose. PSS é uma amostra pontual,
não o pico de memória.

**Vocês avaliaram usabilidade com usuários?**
Não. Foram verificados rótulos e áreas de toque em uma amostra de cinco
controles. Taxa de conclusão e tempo de tarefas com pessoas são propostas,
sem participantes nem resultados inventados.

**Qual seria a próxima evolução?**
Primeiro avaliar tarefas com usuários e medir em aparelhos físicos. Se houver
necessidade de conteúdo remoto, acrescentar fonte de rede e política de
sincronização. Uma mudança de esquema exigirá migração explícita do banco.
