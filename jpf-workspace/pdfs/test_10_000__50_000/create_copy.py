#!/usr/bin/env python3

import argparse
import shutil
from pathlib import Path
import sys
import os

parser = argparse.ArgumentParser(description="Copy a file")
parser.add_argument('source', help='File to copy')
parser.add_argument('num', type=int, help='Number of Copies')

args = parser.parse_args()


src = Path(args.source)
copys = args.num



if not src.is_file():
    sys.exit(f"Error: {src} is not a file")

dir = os.path.dirname(src)

if copys < 1:
    sys.exit(f"Error: {copys} is not a valid number")

for i in range(copys):
    dst = dir + src.stem + "cp" + str(i) + src.suffix
    try:
        shutil.copy2(src, dst)
    except (PermissionError, OSError) as e:
        print("error in copying file: " + dst)



print(src.stem)

print(copys + 5)
