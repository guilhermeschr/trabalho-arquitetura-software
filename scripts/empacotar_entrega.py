#!/usr/bin/env python3
"""Empacota fontes e resultados, sem caches, dados locais de SDK ou builds."""
from pathlib import Path
import zipfile,hashlib,json
root=Path(__file__).resolve().parents[1]
article=root/'artigo'
overleaf=root/'artigo-cotb2027-overleaf.zip'
with zipfile.ZipFile(overleaf,'w',zipfile.ZIP_DEFLATED) as z:
    for p in sorted(article.rglob('*')):
        rel=p.relative_to(article)
        if p.is_file() and 'template-original' not in rel.parts and not p.name.startswith('.'):
            z.write(p,rel.as_posix())
    z.writestr('imagens/','')
roots=['artigo','projeto-base','diagramas','apresentacao','docs','scripts','output']
selected=[root/'README.md',overleaf]
selected += [root/name for name in ['.gitignore','.gitattributes'] if (root/name).is_file()]
for dirname in roots:
    for p in (root/dirname).rglob('*'):
        if not p.is_file():continue
        rel=p.relative_to(root)
        if any(x in rel.parts for x in ['build','.gradle','.kotlin','.idea','__pycache__','template-original']):continue
        if p.name in ['local.properties','.DS_Store'] or p.name.endswith('.iml'):continue
        if rel.as_posix()=='output/pdf/artigo-cotb2027-base.pdf':continue
        if p.suffix in ['.aux','.blg','.bbl','.out','.toc']:continue
        selected.append(p)
selected=sorted(set(selected))
manifest={str(p.relative_to(root)):hashlib.sha256(p.read_bytes()).hexdigest() for p in selected if p.name!='manifesto-entrega-sha256.json'}
checksum=root/'docs/manifesto-entrega-sha256.json';checksum.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
if checksum not in selected:selected.append(checksum)
archive=root/'entrega-arquitetura-software.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
    for p in selected:z.write(p,Path('trabalho-arquitetura-software')/p.relative_to(root))
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    forbidden=[n for n in z.namelist() if any(x in Path(n).parts for x in ['.gradle','.kotlin','build']) or n.endswith('local.properties')]
    assert not forbidden,forbidden
with zipfile.ZipFile(overleaf) as z:
    assert z.testzip() is None
    for p in ['main.tex','preambulo-cotb.tex','resultados.tex','referencias.bib','acmart.cls','ACM-Reference-Format.bst']:
        assert p in z.namelist(),p
print(json.dumps({'archive':str(archive),'files':len(selected),'bytes':archive.stat().st_size,'overleaf_bytes':overleaf.stat().st_size},ensure_ascii=False))
