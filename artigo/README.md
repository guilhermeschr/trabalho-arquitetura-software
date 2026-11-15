# Artigo completo — COTB 2027

Artigo de seis páginas, em duas colunas, sobre o Now in Android e o News
Architecture Demo. O arquivo principal é `main.tex`; o PDF final está no
índice da entrega. O ZIP para Overleaf contém todos os fontes necessários,
incluindo classe, estilo bibliográfico, seções, imagens vetoriais e resultados.

## Usar no Overleaf

1. Crie um projeto por upload do arquivo `artigo-cotb2027-overleaf.zip`.
2. Selecione `main.tex` e o compilador pdfLaTeX.
3. Preencha somente os dados reais dos autores e da instituição.
4. Recompile. A revisão acadêmica do conteúdo deve ser feita pelos autores.

Para compilar com TeX Live:

```sh
pdflatex main.tex
bibtex main
pdflatex main.tex
pdflatex main.tex
```

O texto possui título em inglês e português, abstract e keywords em inglês,
corpo em português, cinco figuras, quatro tabelas, referências reais e
declaração de uso de IA. O template foi preservado, inclusive cabeçalho do
evento, que não indica submissão nem aceite. Classe e estilo bibliográfico são
idênticos aos arquivos enviados. A opção final `language=english` substitui a
chave aninhada `main=english` para compatibilidade com xkeyval antigo.

`resultados.tex` é gerado a partir das evidências por
`../scripts/consolidar_resultados.py`. Não se devem inventar valores nem editar
apenas o texto do artigo para aparentar novas medições. O relatório e o plano
GQM completos estão na documentação complementar.

A compilação local usou Tectonic 0.17.0 e BibTeX. O PDF foi renderizado para
revisão. A compilação dentro do serviço Overleaf não foi executada. Os arquivos
originais de exemplo permanecem em `template-original` para consulta, mas não
entram no ZIP de submissão.
