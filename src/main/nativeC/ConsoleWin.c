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

#include <windows.h>
#include <stdio.h>
#include <jni.h>
#include "Console.h"
#include "Bash.h"

//prints one line the split the repositories. Only implemented for windows systems.
JNIEXPORT void JNICALL Java_org_RepositorySearch_Console_PrintDelimiter(JNIEnv *env, jobject this){
  
  int size = GetSize();
  for(int i=0;i<size;i++){
    printf("-");
  }
  printf("\n");

}

JNIEXPORT jint JNICALL Java_org_RepositorySearch_Console_getSize
(JNIEnv *env, jobject this){
  
  return (jint)GetSize();

}

int GetSize(){
  
  int size=-1;
  int fd=fileno(stdout);
  DWORD nhandle;
  HANDLE handle;
  CONSOLE_SCREEN_BUFFER_INFO csbi;
  switch (fd) {
  case 0: nhandle = STD_INPUT_HANDLE;
    break;
  case 1: nhandle = STD_OUTPUT_HANDLE;
    break;
  case 2: nhandle = STD_ERROR_HANDLE;
    break;
  default:
    size = -1;
  }

  if(size != -1){
    //standard windows cmd is used, no bash shell
    handle = GetStdHandle(nhandle);  
  
    if(GetConsoleScreenBufferInfo(handle, &csbi)){
      isBash = JNI_FALSE;
      size = csbi.srWindow.Right-csbi.srWindow.Left;
    }else{
      isBash = JNI_TRUE;
    }

  }else{
    //the git bash is used
    isBash = JNI_TRUE;
  }
  return size;
}

JNIEXPORT jboolean JNICALL Java_org_RepositorySearch_Console_ShellColor
(JNIEnv *env, jobject this){
  
  return isBash;

}
