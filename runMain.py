'''
Alle Test mit Einbindung libraries laufen lassen
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
    #'-Djava.util.logging.config.file=log.properties',
    javaclass,
    searchTerm,
])

