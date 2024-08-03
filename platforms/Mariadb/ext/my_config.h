#ifndef _H_MYCONF_EXT
#define _H_MYCONF_EXT

#ifdef __APPLE__
    #include "mac/my_config.h"
#elif _WIN32
    #include "windows/my_config.h"
#elif __linux__
    #include "linux/my_config.h"
#endif

#endif
