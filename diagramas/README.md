# Diagramas arquiteturais

Cada diagrama é fornecido em Mermaid (`.mmd`), SVG e PDF vetorial. O artigo
inclui os cinco PDFs, com legendas e explicações. Os rótulos curtos nas figuras
remetem às classes e módulos descritos no artigo e no relatório complementar.

1. **Contexto:** aplicativo NiA, leitor, API, navegador e plataforma Android.
2. **Componentes:** recorte For You, domínio, repositórios e fontes. Não é o
   inventário completo dos módulos do repositório.
3. **Camadas:** setas contínuas indicam dependências; a tracejada representa
   dados observáveis. Domain é opcional nas recomendações oficiais Android.
4. **Sequência:** leitura local e salvamento de favorito na revisão fixada. Não
   representa o ViewModel iniciando diretamente uma requisição de rede.
5. **Demonstração:** organização implementada, com duas features e núcleo comum.
   As setas das features aos repositórios representam chamadas; as emissões
   observáveis percorrem o caminho de volta até a apresentação.

Os diagramas 1 a 4 referem-se ao commit
`2982e63cb691ad49cd009aab43dc206fee6699c2`. O quinto representa o código entregue.
As fontes foram renderizadas com Mermaid 11.12.0. Depois de editar, exporte
novamente os formatos vetoriais e substitua as cópias na pasta de imagens do
artigo. Preserve a diferença entre dependências de código, chamadas e emissões.
