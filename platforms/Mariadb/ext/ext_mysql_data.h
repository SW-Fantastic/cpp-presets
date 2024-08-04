#ifndef _H_EXT_MYDATA
#define _H_EXT_MYDATA

#include<stdio.h>
#include<stdlib.h>

/**
 Allocate a array with type char* (as type char**)
*/
char** ext_alloc_char_list(int length) {

    if(length <= 0) {
        return NULL;
    }
    return (char**)malloc(sizeof(char*) * length);

}

/**
 Add char* element into char** array
*/
int ext_char_list_insert(char** buf,char* str, int idx) {

    if(str == NULL || buf == NULL) {
        return 0;
    }

    buf[idx] = str;
    return 1;

}

/**
 Get char* element from char** array
*/
char* ext_char_list_get(char** buf, int idx) {

    if(idx < 0 || buf == NULL) {
        return NULL;
    }
    int size = sizeof(buf) / sizeof(char*);
    if(idx >= size) {
        return NULL;
    }

    return buf[idx];
}

void ext_char_list_free(char** buf) {

    if(buf == NULL) {
        return ;
    }
    free(buf);

}

#endif
