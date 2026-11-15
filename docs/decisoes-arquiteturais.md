# Decisões da demonstração

1. **Quatro módulos.** Isolar inicialização e duas funcionalidades sem reproduzir
   dezenas de módulos da referência. As camadas do núcleo ficam em pacotes;
   isso reduz configuração, mas oferece menos isolamento de compilação entre
   domínio e dados do que módulos independentes.
2. **Domínio com responsabilidade real.** Um caso de uso combina dois fluxos e
   ordena notícias, reutilizado em três ViewModels. Não há casos de uso que
   apenas repassam todas as chamadas dos repositórios.
3. **Favoritos no Room.** Identificadores são chaves primárias, inserções usam
   IGNORE e remoções são idempotentes. O catálogo é estático e independente.
   IDs antigos sem notícia correspondente não criam conteúdo fantasma.
4. **Dados observáveis.** A UI não conserva uma cópia própria dos favoritos.
   Alterações são lidas do fluxo persistente e combinadas no domínio.
5. **Apresentação compartilhada por composição.** `FeedStateHolder` recebe o
   escopo do ViewModel e concentra estados e gravações pendentes. O seletor de
   cada ViewModel determina lista completa, favoritos ou ID específico.
6. **UI preservada em falhas de gravação.** Nenhum favorito é alterado apenas
   visualmente antes da confirmação. Uma gravação malsucedida mantém os dados
   anteriores e disponibiliza nova tentativa. Operações pendentes do mesmo ID
   são bloqueadas para evitar duplicação por toque repetido.
7. **Navegação no aplicativo principal.** Features expõem rotas Compose e
   callbacks; não importam outras funcionalidades. O detalhe recebe `newsId`
   pelo SavedStateHandle e exibe NotFound se não existir.
8. **Versões estáveis fixadas.** O conjunto foi escolhido pela compatibilidade,
   não por ser o mais recente. O lint pode apontar atualizações disponíveis.
   AndroidX Test/Espresso foram atualizados para versões estáveis que corrigem
   acesso ao InputManager em Android 16.
9. **Sem rede.** A demonstração não solicita permissão INTERNET. Mostra leitura
   local e persistência; não deve ser apresentada como implementação da
   sincronização offline-first completa do Now in Android.
10. **Esquema inicial.** Room exporta o esquema v1. Não se implementou uma
    migração fictícia. Alterações futuras do esquema exigem migração explícita.
