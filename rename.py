import os
import re

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    def replacer(match):
        inner = match.group(1)
        inner = inner.replace('Echo Music', 'Akai')
        inner = inner.replace('Echo music', 'Akai')
        inner = inner.replace('echo-music', 'akai')
        inner = inner.replace('echomusic', 'akai')
        inner = re.sub(r'\bEcho\b', 'Akai', inner)
        inner = re.sub(r'\becho\b', 'akai', inner)
        return f'>{inner}</string>'
        
    new_content = re.sub(r'>([^<]*)</string>', replacer, content)
    
    # Check if app_name exists and replace it explicitly if it missed
    new_content = new_content.replace('<string name="app_name">Echo Music</string>', '<string name="app_name">Akai</string>')
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)

for root, dirs, files in os.walk('c:\\Users\\mayan\\Desktop\\musicapp\\Echo\\Echo-Music\\app\\src\\main\\res'):
    for file in files:
        if file.endswith('.xml'):
            replace_in_file(os.path.join(root, file))

print('Strings Replaced')
