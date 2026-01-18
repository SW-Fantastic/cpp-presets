

#ifndef LLVM_CLANG_C_CXSTRING_H
#define LLVM_CLANG_C_CXSTRING_H

#include "ExternC.h"
#include "Platform.h"

LLVM_CLANG_C_EXTERN_C_BEGIN

/**
 * \defgroup CINDEX_STRING String manipulation routines
 * \ingroup CINDEX
 *
 * @{
 */

/**
 * A character string.
 *
 * The \c CXString type is used to return strings from the interface when
 * the ownership of that string might differ from one call to the next.
 * Use \c clang_getCString() to retrieve the string data and, once finished
 * with the string data, call \c clang_disposeString() to free the string.
 */
typedef struct {
  const void *data;
  unsigned private_flags;
} CXString;

typedef struct {
  CXString *Strings;
  unsigned Count;
} CXStringSet;

/**
 * Retrieve the character data associated with the given string.
 */
CINDEX_LINKAGE const char *clang_getCString(CXString string);

/**
 * Free the given string.
 */
CINDEX_LINKAGE void clang_disposeString(CXString string);

/**
 * Free the given string set.
 */
CINDEX_LINKAGE void clang_disposeStringSet(CXStringSet *set);

/**
 * @}
 */

LLVM_CLANG_C_EXTERN_C_END

#endif

