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
