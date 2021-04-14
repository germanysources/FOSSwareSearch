''' compile the native c parts
Eclipse Public License - v 2.0
Copyright (c) 2018 Johannes Gerbershagen <johannes.gerbershagen@kabelmail.de>

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html

NO WARRANTY:
EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, AND TO THE EXTENT
PERMITTED BY APPLICABLE LAW, THE PROGRAM IS PROVIDED ON AN "AS IS"
BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF
TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR
PURPOSE. Each Recipient is solely responsible for determining the
appropriateness of using and distributing the Program and assumes all
risks associated with its exercise of rights under this Agreement,
including but not limited to the risks and costs of program errors,
compliance with applicable laws, damage to or loss of data, programs
or equipment, and unavailability or interruption of operations.

'''
import os, platform
from os.path import join
import sys

if len(sys.argv) == 2 and sys.argv[1] == '-h':
    print('Usage: {0} <gcc options>'.format(sys.argv[0]))
    exit(0)

def execute(cmd_parts):
    print(' '.join(cmd_parts))
    os.system(' '.join(cmd_parts))

is_windows = 'windows' in platform.system().lower()
javah = '"' + join(os.environ['JAVA_HOME'], 'bin', 'javah') + '"'
classpath=join('target', 'classes')

HeaderFile=join('src', 'main', 'nativeC', 'Console.h')
if is_windows:
    ImplFile=join('src', 'main', 'nativeC', 'ConsoleWin.c')
else:
    ImplFile=join('src', 'main', 'nativeC', 'Console.c')

include = []
#collect library headers
for root, dirs, files in os.walk(join(os.environ['JAVA_HOME'], 'include')):
    for dirname in dirs:
        include.append('-I\"'+join(root, dirname)+'\"')
include.append('-I\"'+join(os.environ['JAVA_HOME'], 'include')+'\"')

execute([javah, '-classpath', classpath, '-o', HeaderFile, 'org.RepositorySearch.Console'])

#make directory for compiled-library
if not os.access(platform.machine(), os.F_OK):
    os.mkdir(platform.machine())


output = ''
gcc_command = ''
options = ''
for i in range(1, len(sys.argv)):
    options += sys.argv[i]

if is_windows:
    output = join(platform.machine(), 'ConsoleWidth.dll')
    gcc_command = 'gcc {0} -std=c99 -Wl,--add-stdcall-alias'.format(options)
else:
    output = join(platform.machine(), 'libConsoleWidth.so')
    gcc_command = 'gcc {0} -std=c99 -fPIC'.format(options)

execute([gcc_command,  
         ' '.join(include),
         "-shared -o",
         output,
         ImplFile
])



