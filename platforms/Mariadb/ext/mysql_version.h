#ifndef _H_MYVER_EXT
#define _H_MYVER_EXT

#ifdef __APPLE__
    #include "mac/mysql_version.h"
#elif _WIN32
    #include "windows/mysql_version.h"
#elif __linux__
    #include "linux/mysql_version.h"
#endif

#endif
