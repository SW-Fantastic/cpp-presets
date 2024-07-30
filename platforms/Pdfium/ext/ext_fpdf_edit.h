#ifndef _H_PDFIUM_EDIT_EXT_
#define _H_PDFIUM_EDIT_EXT_

#include "fpdfview.h"
#include "fpdf_edit.h"
#include <stdio.h>
#include <string.h>

int priv_fpdf_readJpegBytes(void* param, unsigned long position, unsigned char* pBuf, unsigned long size) {

    char* data = static_cast<char *>(const_cast<void *>(param));
    memcpy(pBuf, data + position, size);
    return size;

}

bool FPDFObj_EXT_LoadJpegData(void* data, int length, FPDF_PAGE page,FPDF_PAGEOBJECT obj) {

    FPDF_FILEACCESS access;
    access.m_Param = data;
    access.m_FileLen = length;
    access.m_GetBlock = &priv_fpdf_readJpegBytes;
    return FPDFImageObj_LoadJpegFile(
        page == NULL ? NULL : &page,
        page == NULL ? 0 : 1,
        obj,
        &access
    );

}

#endif