# Trabalho de Arquitetura de Software

Entrega integrada sobre o **Now in Android**, com artigo no template COTB 2027,
aplicativo Android independente, diagramas, apresentação e evidências.

**Autores:** Giancarlo Roveda de Jesus, Daniel Hoepers e Guilherme Schroder —
UDESC, Centro de Educação Superior do Alto Vale do Itajaí (CEAVI), Ibirama/SC.

## Arquivos principais

| Material | Local |
|---|---|
| Artigo final — 6 páginas | [PDF](output/pdf/artigo-arquitetura-software.pdf) |
| Projeto LaTeX para Overleaf | [ZIP](artigo-cotb2027-overleaf.zip) |
| Fontes do artigo | [main.tex](artigo/main.tex) |
| Aplicativo pronto para instalar | [APK debug](output/apk/news-architecture-demo-debug.apk) |
| Projeto Android — 4 módulos | [README de execução](projeto-base/README.md) |
| Slides — 12 páginas, editáveis | [PowerPoint](apresentacao/apresentacao.pptx) |
| Slides para apresentar sem PowerPoint | [PDF](apresentacao/apresentacao.pdf) |
| Roteiro — 15 minutos | [Roteiro](apresentacao/roteiro.md) |
| Perguntas prováveis e respostas | [Perguntas e respostas](apresentacao/perguntas-e-respostas.md) |
| Demonstração gravada | [Vídeo MP4](apresentacao/demonstracao.mp4) |
| Diagramas Mermaid, SVG e PDF | [Índice](diagramas/README.md) |
| Análise detalhada do original | [Relatório](docs/analise-nowinandroid.md) |
| Decisões da demonstração | [Justificativas](docs/decisoes-arquiteturais.md) |
| GQM completo | [Objetivos, perguntas e métricas](docs/gqm.md) |
| Resultados e limites | [Validação](docs/validacao.md) |

## O que foi implementado e verificado

O aplicativo lista 12 notícias fictícias, abre detalhes e permite salvar/remover
favoritos em Room. A atualização aparece nas telas observadoras e persiste após
encerrar o processo. Ele funciona sem internet e não replica sincronização,
notificações, backend ou analytics do original.

Foram aprovados **36 testes unitários e 6 instrumentados**. A compilação do APK,
o lint e a demonstração no emulador foram executados. As medidas técnicas são
exploratórias e pertencem ao aplicativo pequeno; não medem o desempenho do NiA.
Não houve estudo de usabilidade com participantes reais.

## Referência e rastreabilidade

A revisão estudada é `2982e63cb691ad49cd009aab43dc206fee6699c2`, de 20/10/2026,
consultada em 15/11/2026. O recorte preserva evidências de código, testes,
workflows e consulta de PRs. Os trechos do original mantêm os avisos e a licença
Apache 2.0. O aplicativo demonstrativo foi implementado separadamente.

A jornada arquitetural contém um exemplo antigo com
`GetUserNewsResourcesUseCase`; a revisão fixada usa o repositório composto.
Artigo e diagramas identificam essa divergência.

## Reproduzir verificações

1. Siga os requisitos do README Android e inicie um emulador compatível.
2. Compile e execute as suítes unitária, Room e Compose pelos comandos
   documentados. Há relatórios JUnit e logs da execução entregue nas evidências.
3. Execute `python3 scripts/verificar_arquitetura.py` para verificar o grafo.
4. Instale o APK e execute `python3 scripts/demonstrar.py --reset` para um
   roteiro determinístico. **`--reset` limpa somente os dados deste aplicativo.**
5. Execute `python3 scripts/medir_inicializacao.py` para coletar as dez amostras
   de abertura. Esses scripts usam `adb` no PATH ou o SDK em `ANDROID_HOME`.

O experimento de latência é um teste instrumentado do núcleo e registra o JSON
com a tag `NewsDemoMetrics` no logcat. A consolidação em
`scripts/consolidar_resultados.py` utiliza os relatórios locais e os arquivos
brutos já extraídos; ao repetir a coleta, atualize os JSONs de latência e da
amostra de acessibilidade a partir dos novos registros antes de consolidar.

As pastas temporárias, caches, `local.properties` e diretórios de build não
entram no pacote final. O enunciado orientou o conteúdo; o template orientou o
formato. Não foi realizada submissão ao evento, publicação ou envio ao professor.
