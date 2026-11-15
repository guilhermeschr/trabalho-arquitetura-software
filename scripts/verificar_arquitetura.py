#!/usr/bin/env python3
"""Verifica o grafo Gradle e a ausência de referências à persistência na UI."""
from pathlib import Path
import json, re
root = Path(__file__).resolve().parents[1]
project = root / 'projeto-base'
modules = {':app':'app', ':core':'core', ':feature:news':'feature/news', ':feature:favorites':'feature/favorites'}
edges = {name: re.findall(r'project\("([^"]+)"\)', (project / folder / 'build.gradle.kts').read_text()) for name, folder in modules.items()}
cycles = set()
def visit(node, path):
    if node in path:
        ring = path[path.index(node):]
        rotations = [tuple(ring[i:]+ring[:i]) for i in range(len(ring))]
        cycles.add(min(rotations)); return
    for target in edges.get(node, []): visit(target, path + [node])
for module in modules: visit(module, [])
feature_edges = [(a,b) for a, targets in edges.items() for b in targets if a.startswith(':feature:') and b.startswith(':feature:')]
violations = []
folders = [project/'feature/news/src/main', project/'feature/favorites/src/main', project/'core/src/main/kotlin/br/edu/arquitetura/newsdemo/core/ui']
for folder in folders:
    for file in folder.rglob('*.kt'):
        for line_no, line in enumerate(file.read_text().splitlines(), 1):
            if re.search(r'core\.database|androidx\.room|\bFavoriteDao\b|\bFavoritesDatabase\b|\bFavoriteEntity\b', line):
                violations.append({'file':str(file.relative_to(root)), 'line':line_no, 'text':line.strip()})
result = {'modules':edges, 'cycles':[list(c) for c in cycles], 'cycle_count':len(cycles), 'feature_edges':feature_edges,
          'feature_edge_count':len(feature_edges), 'forbidden_persistence_references':violations,
          'forbidden_reference_count':len(violations), 'scope':'dependências project(...) e referências textuais aos tipos de persistência; não é uma prova geral de manutenibilidade'}
(root/'docs/evidencias/arquitetura-metricas.json').write_text(json.dumps(result,ensure_ascii=False,indent=2))
print(json.dumps(result,ensure_ascii=False,indent=2))
raise SystemExit(bool(cycles or feature_edges or violations))
