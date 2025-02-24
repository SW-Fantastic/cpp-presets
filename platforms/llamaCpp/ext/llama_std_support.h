#ifndef _H_LLAMA_STD_
#define _H_LLAMA_STD_

#include <vector>
#include "../include/llama.h"

void* vector_alloc(size_t n) {
    return new std::vector<llama_chat_message>();
}

void vector_free(void* ptr) {
    delete static_cast<std::vector<llama_chat_message>*>(ptr);
}

void vector_push_back(void* ptr, llama_chat_message val) {
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    vec->push_back(val);
}

llama_chat_message vector_get(void* ptr, size_t index) {
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    return vec->at(index);
}

void vector_remove(void* ptr, size_t index) {
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    if(index >= vec->size() || index < 0) {
        return;
    }
    vec->erase(vec->begin() + index);
}

size_t vector_size(void* ptr) {
    if(ptr == nullptr) {
        return 0;
    }
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    return vec->size();
}

size_t vector_capacity(void* ptr) {
    if(ptr == nullptr) {
        return 0;
    }
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    return vec->capacity();
}

llama_chat_message* vector_data(void* ptr) {
    if(ptr == nullptr) {
        return nullptr;
    }
    std::vector<llama_chat_message>* vec = static_cast<std::vector<llama_chat_message>*>(ptr);
    return vec->data();
}

#endif