#!/usr/bin/env python3
"""Dez reinícios do processo; não limpa dados nem cache de disco."""
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import os, shutil, subprocess, re, json, time, statistics
root=Path(__file__).resolve().parents[1]
sdk=Path(os.environ.get('ANDROID_HOME',Path.home()/'Library/Android/sdk'))
adb=shutil.which('adb') or str(sdk/'platform-tools/adb')
component='br.edu.arquitetura.newsdemo/br.edu.arquitetura.newsdemo.app.MainActivity'
package='br.edu.arquitetura.newsdemo'
def run(*args):return subprocess.check_output([adb,*args],text=True)
raw=[];times=[]
for i in range(11):
    run('shell','am','force-stop',package)
    time.sleep(.4)
    text=run('shell','am','start','-W','-n',component)
    if 'Status: ok' not in text:raise RuntimeError(text)
    total=int(re.search(r'TotalTime:\s*(\d+)',text).group(1))
    raw.append({'iteration':i,'warmup':i==0,'output':text})
    if i:times.append(total)
    print(('aquecimento' if i==0 else str(i)),total,'ms',flush=True)
result={'date':datetime.now(ZoneInfo('America/Sao_Paulo')).isoformat(),'apk':'debug','measurement':'am start -W TotalTime; processo encerrado antes de cada amostra; cache de disco preservado',
        'warmup_excluded':1,'samples_ms':times,'median_ms':statistics.median(times),'min_ms':min(times),'max_ms':max(times),
        'device':run('shell','getprop','ro.product.model').strip(),'android':run('shell','getprop','ro.build.version.release').strip(),
        'api':run('shell','getprop','ro.build.version.sdk').strip(),'abi':run('shell','getprop','ro.product.cpu.abi').strip(),'raw':raw}
(root/'docs/evidencias/inicializacao.json').write_text(json.dumps(result,indent=2,ensure_ascii=False))
