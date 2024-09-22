#ifndef _H_IM_DATA_
#define _H_IM_DATA_

#include "cimgui.h"
#include "stdlib.h"

ImWchar* Ext_CreateFontRange(int begin, int end) {

    ImWchar* buf = (ImWchar*)malloc(sizeof(ImWchar) * 3);
    memset((void*)buf, 0, sizeof(ImWchar) * 3);
    buf[0] = (ImWchar)begin;
    buf[1] = (ImWchar)end;
    buf[2] = 0;

    return buf;
}

#endif