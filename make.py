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
import os
from os.path import join
import sys

def execute(cmd_parts):
    os.system(' '.join(cmd_parts))

javah = join(os.environ['JAVA_HOME'], 'bin', 'javah')
classpath=join('target', 'classes')

HeaderFile=join('src', 'main', 'nativeC', 'Console.h')
ImplFile=join('src', 'main', 'nativeC', 'Console.c')

print(HeaderFile)
print(ImplFile)
execute([javah, '-classpath', classpath, '-o', HeaderFile])

execute(["gcc -std=c99 -fPIC -I\"$JAVA_HOME/include\" -I\"$JAVA_HOME/include/linux\" -shared -o libConsoleWidth.so",
         ImplFile
])



