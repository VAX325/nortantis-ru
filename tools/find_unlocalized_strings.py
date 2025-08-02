import os,re
root='src/nortantis'
# patterns to find UI component constructors or text setters
patterns=[
    re.compile(r'new\s+J(Label|Button|CheckBox|RadioButton|MenuItem|Menu|Dialog|Frame)\s*\(\s*"'),
    re.compile(r'\.set(Text|Title)\s*\(\s*"'),
    re.compile(r'JOptionPane\.(show|create)\w*\s*\(\s*"'),
    re.compile(r'putValue\s*\(\s*Action\.NAME\s*,\s*"')
]
ignore_substrings=['Localization.get']
results=[]
for dirpath,_,filenames in os.walk(root):
    for fn in filenames:
        if fn.endswith('.java'):
            path=os.path.join(dirpath,fn)
            with open(path,encoding='utf-8') as f:
                for i,line in enumerate(f,1):
                    if any(p.search(line) for p in patterns):
                        if any(sub in line for sub in ignore_substrings):
                            continue
                        results.append(f"{path}:{i}: {line.strip()}")
if __name__=='__main__':
    for r in results:
        print(r)
