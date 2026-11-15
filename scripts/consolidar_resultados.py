#!/usr/bin/env python3
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import json, statistics, shutil, xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1];evidence=root/'docs/evidencias'
def read(name):return json.loads((evidence/name).read_text())
unit=[];instrumented=[]
for p in (root/'projeto-base').rglob('TEST-*.xml'):
    if '/test-results/testDebugUnitTest/' in str(p):target=unit
    elif '/outputs/androidTest-results/connected/debug/' in str(p):target=instrumented
    else:continue
    tree=ET.parse(p).getroot()
    target.append({'suite':tree.attrib.get('name'), 'tests':int(tree.attrib.get('tests',0)),
                   'failures':int(tree.attrib.get('failures',0)), 'errors':int(tree.attrib.get('errors',0)),
                   'skipped':int(tree.attrib.get('skipped',0)),'time_seconds':float(tree.attrib.get('time',0))})
    folder=evidence/'junit';folder.mkdir(exist_ok=True)
    shutil.copy2(p,folder/(p.parents[5].name+'-'+p.name) if target is instrumented else folder/p.name)
assert sum(t['tests'] for t in unit)==36,unit
assert sum(t['tests'] for t in instrumented)==6,instrumented
assert not sum(t['failures']+t['errors']+t['skipped'] for t in unit+instrumented)
latencies=read('latencias-favoritos.json')['favorite_latency_ms'];assert len(latencies)==10
result={'date':datetime.now(ZoneInfo('America/Sao_Paulo')).isoformat(),'reference_commit':'2982e63cb691ad49cd009aab43dc206fee6699c2',
        'unit':{'tests':sum(t['tests'] for t in unit),'suites':unit,'execution':read('testes-unitarios-execucao.json')},
        'instrumented':{'tests':sum(t['tests'] for t in instrumented),'suites':instrumented},
        'fake_tested_classes':['ObserveNewsWithFavoritesUseCase','RoomFavoritesRepository','NewsViewModel','NewsDetailViewModel','FavoritesViewModel'],
        'architecture':read('arquitetura-metricas.json'),'startup':read('inicializacao.json'),
        'favorite_latency':{'samples_ms':latencies,'median_ms':statistics.median(latencies),'min_ms':min(latencies),'max_ms':max(latencies),
                            'context':'Room em memória; coletor ativo; tempo entre gravação e emissão combinada; última execução instrumentada'},
        'demo':read('demonstracao.json'),'accessibility':read('acessibilidade.json'),
        'limitations':['Sem execução do Now in Android original','APK debug em um emulador ARM64; sem comparação causal','Sem participantes reais para avaliação de usabilidade','PSS pontual; latência Room em memória não inclui renderização']}
assert result['demo']['passed'] and result['accessibility']['passed']==5
(evidence/'resultados.json').write_text(json.dumps(result,indent=2,ensure_ascii=False))
def fmt(v,n=2):return f'{v:.{n}f}'.replace('.',',')
macros={'UnitTests':36,'InstrumentedTests':6,'UnitSeconds':fmt(result['unit']['execution']['wall_seconds']),
        'StartupMedian':int(result['startup']['median_ms']),'StartupMin':result['startup']['min_ms'],'StartupMax':result['startup']['max_ms'],
        'FavoriteMedian':fmt(result['favorite_latency']['median_ms']),'MemoryPss':fmt(result['demo']['memory_pss_mib'])}
(root/'artigo/resultados.tex').write_text('% Gerado das evidências de execução; não editar valores manualmente.\n'+'\n'.join('\\newcommand{\\'+k+'}{'+str(v)+'}' for k,v in macros.items())+'\n')
for name in ['testes-unitarios','testes-room','testes-interface','primeira-compilacao','instrumentacao-compilacao','demonstracao','inicializacao']:
    source=root/'tmp/build'/f'{name}.log'
    if source.exists():shutil.copy2(source,evidence/(name+'.log'))
print(json.dumps(macros,ensure_ascii=False))
