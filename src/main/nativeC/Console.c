/*
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
*/

#include <sys/ioctl.h>
#include <termios.h>
#include <stdio.h>
#include "Console.h"

//prints one line the split the repositories. Only implemented for unix systems.
JNIEXPORT void JNICALL Java_org_RepositorySearch_Console_PrintDelimiter(JNIEnv *env, jobject this){
  
  struct winsize w;
  int fd = fileno(stdout);
  ioctl(fd, TIOCGWINSZ, &w);
  for(int i=0; i<w.ws_col;i++){
    printf("-");
  }
  printf("\n");

}
