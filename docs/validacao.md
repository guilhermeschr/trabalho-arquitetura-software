# Validação e limites da entrega

Validação local realizada em 15/11/2026. Referência NiA:
`2982e63cb691ad49cd009aab43dc206fee6699c2`. O original foi inspecionado estaticamente; os
comandos de compilação e teste abaixo pertencem ao News Architecture Demo.

## Ambiente

- macOS ARM64; JDK 17.0.14; Gradle 8.13; AGP 8.11.1.
- Emulador `Medium_Phone_API_36.1`; modelo `sdk_gphone64_arm64`;
  Android 16, API 36, ABI arm64-v8a.
- APK debug. Não foram usados aparelho físico, backend ou autenticação.

## Comandos e resultados

| Comando/procedimento | Resultado registrado |
|---|---|
| `./gradlew :app:assembleDebug` | APK gerado; código de saída 0 |
| `./gradlew testDebugUnitTest --console=plain` | 36 aprovados; 0 falhas; 0 ignorados |
| `./gradlew :core:connectedDebugAndroidTest` | 4 aprovados; Room real, idempotência, arquivo reaberto e reatividade |
| `./gradlew :app:connectedDebugAndroidTest` | 2 aprovados; percurso Compose e amostra de controles |
| `./gradlew lintDebug` | 0 erros nos quatro módulos; avisos de atualização e recomendação de configuração de backup para APIs antigas |
| `python3 scripts/verificar_arquitetura.py` | 0 ciclos; 0 arestas entre features; 0 referências proibidas no recorte da UI |
| `python3 scripts/demonstrar.py --reset` | Percurso concluído; favorito sobreviveu a force-stop e reabertura; remoção refletida entre telas |
| `python3 scripts/medir_inicializacao.py` | 10 amostras coletadas após 1 aquecimento excluído |
| pdfLaTeX + BibTeX (TeX Live 2022) | Artigo compilado em 6 páginas, renderizado e revisado; sem referências indefinidas ou caixas ultrapassando colunas |
| Exportação e validação do PowerPoint | 12 slides, 4 tabelas nativas, diagramas editáveis, notas e 2 capturas incorporadas; arquivos revisados visualmente |

Os logs finais e os relatórios JUnit estão em `evidencias`. A suíte unitária
levou **5,36 s de parede**, com daemon ativo e dependências
resolvidas; os logs indicam as tarefas executadas. Esse valor inclui o trabalho
do Gradle na chamada e não equivale apenas à soma dos métodos JUnit.

## Medições exploratórias

| Indicador | Resultado | Objeto e procedimento |
|---|---|---|
| Início da Activity | Mediana 3095 ms; mínimo 2886 ms; máximo 3714 ms | `TotalTime` de 10 reinícios de processo; cache de disco preservado |
| Gravação → emissão combinada | Mediana 10,35 ms; mínimo 3,43 ms; máximo 57,98 ms | Dez operações alternadas, Room em memória, coletor ativo; sem medir frame final de Compose |
| Memória PSS | 126658 KiB = 123,69 MiB | Uma captura após o percurso e reabertura; não é pico de memória |
| Acessibilidade amostrada | 5/5 controles aprovados, mínimo 48 dp | Marcador na lista, duas abas, marcador nos detalhes e voltar |
| Testabilidade com fakes | 5 classes | Caso de uso, repositório com DAO fake e três ViewModels |

Os arquivos `inicializacao.json`, `latencias-favoritos.json`,
`memoria-dumpsys.txt`, `acessibilidade-logcat.txt` e `resultados.json` permitem
conferir os números. Os tempos variam com carga do host, cache, JIT e condições
do emulador. Não foi feita comparação controlada entre arquiteturas.

## Problemas encontrados e corrigidos

- Um método instrumentado inferia retorno Int; a assinatura foi corrigida para
  Unit, conforme exigência do JUnit.
- O Espresso transitivo antigo acessava InputManager por reflexão e falhava no
  Android 16. Foram adotados AndroidX Test 1.7.0 e Espresso 3.7.0 estáveis.
- A verificação de controles identificou área de voltar menor que o critério
  adotado. O componente recebeu tamanho explícito de 48 dp, e o teste passou.
- A exportação integrada dos slides teve limitações de fontes/prévias. O
  PowerPoint foi preservado, e o PDF final foi exportado pelo LibreOffice
  Impress com fontes incorporadas. Não se afirma revisão dentro do aplicativo PowerPoint.

## Limites e o que permanece proposto

- Não foram executados build, testes nem benchmarks do Now in Android original.
- Não houve participantes reais; conclusão e tempo de tarefas com pessoas são
  propostas de avaliação, sem resultados atribuídos a usuários.
- Não foi calculada cobertura de linhas, feita análise de mutação, comparação
  causal entre arquiteturas ou coleta em aparelhos físicos.
- A demonstração usa dados locais. Não implementa sincronização remota,
  notificações nem os conflitos de uma arquitetura offline-first completa.
- A compilação no serviço Overleaf não foi executada; os fontes foram compilados
  localmente com pdfLaTeX e empacotados com todas as dependências específicas do template.
