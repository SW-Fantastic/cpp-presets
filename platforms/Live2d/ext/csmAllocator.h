#ifndef CSM_ALLOCATOR_H
#define CSM_ALLOCATOR_H

#include "csmBasicType.h"
#include "../include/Live2DCubismCore.h"

void* csmAllocateAligned(const csmSizeType size, const csmUint32 alignment)
{
    size_t offset, shift, alignedAddress;
    void* allocation;
    void** preamble;

    offset = alignment - 1 + sizeof(void*);
    allocation = malloc(size + static_cast<csmUint32>(offset));
    alignedAddress = reinterpret_cast<size_t>(allocation) + sizeof(void*);

    shift = alignedAddress % alignment;

    if (shift)
    {
        alignedAddress += (alignment - shift);
    }

    preamble = reinterpret_cast<void**>(alignedAddress);
    preamble[-1] = allocation;

    return reinterpret_cast<void*>(alignedAddress);
}

void csmDeallocateAligned(void* alignedMemory)
{
    void** preamble;
    preamble = static_cast<void**>(alignedMemory);
    free(preamble[-1]);
}

csmMoc* csmAllocMoc(csmByte* mocBytes, csmSizeType size,csmBool shouldCheckMocConsistency) {

    void* alignedBuffer = csmAllocateAligned(size, csmAlignofMoc);
    memcpy(alignedBuffer, mocBytes, size);
    if (shouldCheckMocConsistency) {
        // .moc3の整合性を確認
        csmBool consistency = csmHasMocConsistency(alignedBuffer, size) != 0;
        if (!consistency) {
            csmDeallocateAligned(alignedBuffer);
            // 整合性が確認できなければ処理しない
            return NULL;
        }
    }
    return csmReviveMocInPlace(alignedBuffer, size);
}

csmModel* csmAllocModel(csmMoc* moc) {
    const csmUint32  modelSize = csmGetSizeofModel(moc);
    void*            modelMemory = csmAllocateAligned(modelSize, csmAlignofModel);
    return csmInitializeModelInPlace(moc, modelMemory, modelSize);
}



#endif