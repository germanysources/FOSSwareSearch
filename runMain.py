'''
run the compiled classes

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
import os, shutil, platform 
from os.path import exists, join, isfile, isdir
import sys

is_windows = 'windows' in platform.system().lower()
classpath_sep = ';' if is_windows else ':'
javaclass = "org.RepositorySearch.queryConsole"

def execute(cmd_parts):
    os.system(' '.join(cmd_parts))

# Recursively collect library jars
jars = []
jars.append('target/classes')
for root, dirs, files in os.walk('target/dependency'):
    for filename in files:
        jars.append(join(root, filename))

searchTerm = "";
for i in range(len(sys.argv)):
    if i > 0:
        searchTerm = searchTerm + " " + sys.argv[i]

execute([
    'java -cp \"%s\"' % classpath_sep.join(jars),
    '-Djava.library.path=.',
    '-Djava.util.logging.config.file=log.properties',
    javaclass,
    searchTerm,
])

