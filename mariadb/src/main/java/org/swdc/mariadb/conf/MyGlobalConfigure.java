package org.swdc.mariadb.conf;


import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Mariadb/include","platforms/Mariadb/ext" },
                include = {"my_global.h" , "mysql_time.h", "my_list.h", "my_alloc.h", "ext_mysql_data.h"},
                linkpath = "platforms/Mariadb/dll/windows/x86_64",
                link = "libmysqld"
        ),
},
        target = "org.swdc.mariadb.core.global",
        global = "org.swdc.mariadb.core.MyGlobal"
)
public class MyGlobalConfigure implements InfoMapper {

    /**
     * Windows构建的时候，请删除 my_global.h里面的下列内容，否则构建将会失败：
     *
     * Please delete following lines from my_global.h
     * condition on building on windows，or build will be failed
     *
     * #include <winsock2.h>
     * #include <ws2tcpip.h>
     *
     * @param infoMap
     */
    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("my_bool").cppTypes("char").translate());
        infoMap.put(new Info("uint").cppTypes("unsigned int").translate());
        infoMap.put(new Info("ushort").cppTypes("unsigned short").translate());
        infoMap.put(new Info("uchar").cppTypes("unsigned char").translate());
        infoMap.put(new Info("int8").cppTypes("signed char").translate());
        infoMap.put(new Info("uint8").cppTypes("unsigned char").translate());
        infoMap.put(new Info("int16").cppTypes("short").translate());
        infoMap.put(new Info("uint16").cppTypes("unsigned short").translate());
        infoMap.put(new Info("int32").cppTypes("int").translate());
        infoMap.put(new Info("uint32").cppTypes("unsigned int").translate());
        infoMap.put(new Info("ulong").cppTypes("unsigned long").translate());
        infoMap.put(new Info("int64").cppTypes("long").translate());
        infoMap.put(new Info("ulonglong","my_ulonglong").cppTypes("long").translate());
        infoMap.put(new Info("st_list").cppTypes("LIST").translate());
        infoMap.put(new Info("PSI_memory_key").cppTypes("unsigned int").translate());
        infoMap.put(new Info("st_mem_root").cppTypes("MEM_ROOT").translate());
        infoMap.put(new Info("st_used_mem").cppTypes("USED_MEM").translate());

        infoMap.put(new Info("STDCALL").cppText("#define STDCALL").translate());
        infoMap.put(new Info("char**").pointerTypes("PointerPointer").translate());


        infoMap.put(new Info("enum_mysql_timestamp_type").enumerate(true).translate());

        for (int i = 0; i <= 16; i++) {
            skip(infoMap, "reg" + i);
        }
        skip(
                infoMap,"MYSQL_PLUGIN_IMPORT",
                "C_MODE_START",
                "C_MODE_END",
                "RTLD_DEFAULT",
                "dlerror",
                "HAVE_DLOPEN",
                "HAVE_DLERROR",
                "bool",
                "__FUNCTION__",
                "__func__",
                "DECIMAL_NOT_SPECIFIED",
                "NOT_FIXED_DEC",
                "setrlimit",
                "MADV_DODUMP",
                "MADV_DONTDUMP",
                "DODUMP_STR",
                "DONTDUMP_STR",
                "DONT_ALLOW_USER_CHANGE",
                "DONT_USE_MYSQL_PWD",
                "SO_EXT",
                "TRUE",
                "FALSE",
                "sig_handler",
                "qsort_t",
                "SOCKOPT_OPTLEN_TYPE",
                "INVALID_SOCKET",
                "FN_LIBCHAR",
                "FN_LIBCHAR2",
                "FN_DIRSEP",
                "FN_EXEEXT",
                "FN_SOEXT",
                "FN_ROOTDIR",
                "FN_DEVCHAR",
                "MY_FILE_MIN",
                "MY_NFILE",
                "OS_FILE_LIMIT",
                "my_ulonglong2double",
                "ulonglong2double",
                "my_off_t2double",
                "my_double2ulonglong",
                "double2ulonglong",
                "LONGLONG_MIN",
                "LONGLONG_MAX",
                "LONGLONG_BUFFER_SIZE",
                "ULONGLONG_MAX",
                "NullS",
                "socket_errno",
                "SOCKET_EINTR",
                "SOCKET_ETIMEDOUT",
                "SOCKET_EWOULDBLOCK",
                "SOCKET_EADDRINUSE",
                "SOCKET_ECONNRESET",
                "SOCKET_ENFILE",
                "SOCKET_EMFILE",
                "SOCKET_CLOSED",
                "SOCKET_EAGAIN",
                "NOT_FIXED_DEC",
                "MY_ERRPTR",
                "MY_FILEPOS_ERROR",
                "ONCE_ALLOC_INIT",
                "RECORD_CACHE_SIZE",
                "KEY_CACHE_SIZE",
                "KEY_CACHE_BLOCK_SIZE",
                "Dl_info",
                "CPP_UNNAMED_NS_START",
                "CPP_UNNAMED_NS_END",
                "my_likely_ok",
                "my_likely_fail",
                "madvise",
                "my_socket",
                "ALIGN_MAX_UNIT",
                "USED_MEM",
                "st_used_mem",
                // following function is not provided
                "list_add",
                "list_reverse",
                "list_walk",
                "list_cons",
                "list_free",
                "list_length",
                "list_delete"
        );
    }

    public void skip(InfoMap map , String ... names) {

        map.put(new Info(names).skip());

    }

}
