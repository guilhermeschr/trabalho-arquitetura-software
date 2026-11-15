#!/usr/bin/env python3
"""Executa e grava o roteiro no emulador. --reset limpa somente os dados deste demo."""
from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import argparse, subprocess, os, shutil, time, re, json, xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
sdk=Path(os.environ.get('ANDROID_HOME',Path.home()/'Library/Android/sdk'))
adb=shutil.which('adb') or str(sdk/'platform-tools/adb')
package='br.edu.arquitetura.newsdemo'; component=package+'/br.edu.arquitetura.newsdemo.app.MainActivity'
title='Arquitetura que facilita mudanças'
parser=argparse.ArgumentParser();parser.add_argument('--reset',action='store_true');args=parser.parse_args()
captures=root/'docs/evidencias/capturas';captures.mkdir(parents=True,exist_ok=True)
def run(*parts):return subprocess.check_output([adb,*parts],text=True)
def screen(name):
    (captures/(name+'.png')).write_bytes(subprocess.check_output([adb,'exec-out','screencap','-p']))
def tree():
    dump = subprocess.run([adb, 'shell', 'uiautomator', 'dump', '/sdcard/newsdemo-window.xml'], capture_output=True, text=True)
    if dump.returncode or 'dumped' not in dump.stdout:
        return ET.Element('empty')
    data = run('exec-out','cat','/sdcard/newsdemo-window.xml')
    try: return ET.fromstring(data)
    except ET.ParseError: return ET.Element('empty')
def find(predicate, timeout=15):
    deadline=time.monotonic()+timeout
    while time.monotonic()<deadline:
        for node in tree().iter('node'):
            if predicate(node): return node
        time.sleep(.3)
    raise AssertionError('Elemento esperado não encontrado na interface')
def text(value):return lambda n:n.get('text')==value
def description(value):return lambda n:n.get('content-desc')==value
def tap(predicate):
    node=find(predicate)
    x1,y1,x2,y2=map(int,re.findall(r'\d+',node.attrib['bounds']))
    run('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2));time.sleep(.7)
run('shell','input','keyevent','224')
run('shell','wm','dismiss-keyguard')
if args.reset:run('shell','pm','clear',package)
run('shell','am','force-stop',package);run('shell','am','start','-n',component)
find(text(title));time.sleep(1)
# O PID é coletado imediatamente após iniciar a gravação; não encerra gravações alheias.
prior=subprocess.run([adb,'shell','pidof','screenrecord'],capture_output=True,text=True).stdout.strip()
if prior:raise RuntimeError('Já existe uma gravação no dispositivo; finalize-a antes deste roteiro.')
record=subprocess.Popen([adb,'shell','screenrecord','--size','720x1600','--bit-rate','2000000','--time-limit','120','/sdcard/newsdemo-demonstracao.mp4'],stdout=subprocess.DEVNULL,stderr=subprocess.PIPE)
time.sleep(1)
record_pid=run('shell','pidof','screenrecord').strip()
if not record_pid.isdigit():raise RuntimeError('Não foi possível identificar a gravação iniciada.')
steps=[]
try:
    screen('noticias');steps.append('lista exibida')
    tap(text(title));find(text('Leitura'));screen('detalhes')
    action='Salvar nos favoritos: '+title
    if any(description('Remover dos favoritos: '+title)(n) for n in tree().iter('node')):
        tap(description('Remover dos favoritos: '+title))
    tap(description(action));find(description('Remover dos favoritos: '+title));screen('detalhes-salvo');steps.append('favorito salvo nos detalhes')
    tap(description('Voltar'));tap(text('Favoritos'));find(text(title));screen('favoritos');steps.append('favorito exibido em outra tela')
    run('shell','am','force-stop',package);time.sleep(.5)
    run('shell','am','start','-n',component);find(text(title))
    tap(text('Favoritos'));find(text(title));screen('favoritos-apos-reabrir');steps.append('favorito preservado após force-stop e reabertura')
    tap(description('Remover dos favoritos: '+title));find(text('Seus favoritos aparecem aqui'));screen('favoritos-vazio');steps.append('remoção atualiza lista vazia')
    tap(text('Notícias'));find(description(action));steps.append('lista principal reflete remoção')
    memory=run('shell','dumpsys','meminfo',package)
    (root/'docs/evidencias/memoria-dumpsys.txt').write_text(memory)
    match=re.search(r'TOTAL PSS:\s*(\d+)',memory)
    if not match:match=re.search(r'^\s*TOTAL\s+(\d+)',memory,re.M)
    if not match:raise RuntimeError('TOTAL PSS não localizado no dumpsys')
    kib=int(match.group(1))
    (root/'docs/evidencias/demonstracao.json').write_text(json.dumps({'date':datetime.now(ZoneInfo('America/Sao_Paulo')).isoformat(),'steps':steps,'passed':True,'persistence_after_process_restart':True,'memory_pss_kib':kib,'memory_pss_mib':kib/1024,'memory_context':'após navegar por lista/detalhes/favoritos, reabrir processo e remover favorito; build debug; uma amostra'},ensure_ascii=False,indent=2))
    print('Roteiro concluído:',steps,flush=True)
    time.sleep(2)
finally:
    subprocess.run([adb,'shell','kill','-2',record_pid],capture_output=True)
    record.wait(timeout=15)
    subprocess.run([adb,'pull','/sdcard/newsdemo-demonstracao.mp4',str(root/'apresentacao/demonstracao.mp4')],check=True)
