#ifndef _H_LLAMA_STD_
#define _H_LLAMA_STD_

#include "../include/llama.h"


#define LLAMA_POOLING_TYPE_NONE 0;
#define LLAMA_POOLING_TYPE_MEAN 1;
#define LLAMA_POOLING_TYPE_CLS  2;
#define LLAMA_POOLING_TYPE_LAST 3;

int ext_llama_pooling_type(llama_context* context) {
    return static_cast<int>(llama_pooling_type(context));
}

#endif