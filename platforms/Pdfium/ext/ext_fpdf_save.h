#ifndef _H_PDFIUM_SAVE_EXT_
#define _H_PDFIUM_SAVE_EXT_

#include "fpdfview.h"
#include "fpdf_save.h"
#include <stdio.h>

typedef struct FWriter_: FPDF_FILEWRITE {

    FILE* fp;

} PDFWriter;

int priv_fpdf_write_data(FPDF_FILEWRITE* self, const void* data, unsigned long size) {

    PDFWriter* writer = reinterpret_cast<PDFWriter*>(self);
    char* writeBuffer = static_cast<char *>(const_cast<void *>(data));
    fwrite(data, 1, size, writer->fp);
    return 1;

}

bool FPDF_EXT_SaveAsCopy(FPDF_DOCUMENT doc, const char* filePath) {

    FILE* file = fopen(filePath, "wb");
    if(file == NULL) {
        return false;
    }

    PDFWriter write;
    write.fp = file;
    write.WriteBlock = &priv_fpdf_write_data;

    int result = FPDF_SaveAsCopy(doc,&write,FPDF_NO_INCREMENTAL);
    fclose(file);

    return result;
}

#endif