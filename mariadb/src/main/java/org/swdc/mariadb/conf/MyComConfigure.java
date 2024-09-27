package org.swdc.mariadb.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Mariadb/include" },
                include = "mysql_com.h",
                linkpath = "platforms/Mariadb/dll/windows/x86_64",
                link = "libmysqld"
        ),
        @Platform(
                value = "linux-x86_64",
                includepath = { "platforms/Mariadb/include" },
                include = "mysql_com.h",
                linkpath = "platforms/Mariadb/dll/linux/x86_64",
                link = "mariadbd"
        ),
},
        inherit = MyGlobalConfigure.class,
        target = "org.swdc.mariadb.core.com",
        global = "org.swdc.mariadb.core.MyCom"
)
public class MyComConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

        skip(infoMap,
                "HOSTNAME_LENGTH",
                "HOSTNAME_LENGTH_STR",
                "SYSTEM_CHARSET_MBMAXLEN",
                "NAME_CHAR_LEN",
                "USERNAME_CHAR_LENGTH",
                "USERNAME_CHAR_LENGTH_STR",
                "NAME_LEN",
                "USERNAME_LENGTH",
                "DEFINER_CHAR_LENGTH",
                "DEFINER_LENGTH",
                "MYSQL_AUTODETECT_CHARSET_NAME",
                "MYSQL50_TABLE_NAME_PREFIX",
                "MYSQL50_TABLE_NAME_PREFIX_LENGTH",
                "SAFE_NAME_LEN",
                "SERVER_VERSION_LENGTH",
                "SQLSTATE_LENGTH",
                "LIST_PROCESS_HOST_LEN",
                "USER_HOST_BUFF_SIZE",
                "MARIADB_FIELD_ATTR_LAST",
                "FIELD_TYPE_DECIMAL",
                "FIELD_TYPE_NEWDECIMAL",
                "FIELD_TYPE_TINY",
                "FIELD_TYPE_SHORT",
                "FIELD_TYPE_LONG",
                "FIELD_TYPE_FLOAT",
                "FIELD_TYPE_DOUBLE",
                "FIELD_TYPE_NULL",
                "FIELD_TYPE_TIMESTAMP",
                "FIELD_TYPE_LONGLONG",
                "FIELD_TYPE_INT24",
                "FIELD_TYPE_DATE",
                "FIELD_TYPE_TIME",
                "FIELD_TYPE_DATETIME",
                "FIELD_TYPE_YEAR",
                "FIELD_TYPE_NEWDATE",
                "FIELD_TYPE_ENUM",
                "FIELD_TYPE_SET",
                "FIELD_TYPE_TINY_BLOB",
                "FIELD_TYPE_MEDIUM_BLOB",
                "FIELD_TYPE_LONG_BLOB",
                "FIELD_TYPE_BLOB",
                "FIELD_TYPE_VAR_STRING",
                "FIELD_TYPE_STRING",
                "FIELD_TYPE_CHAR",
                "FIELD_TYPE_INTERVAL",
                "FIELD_TYPE_GEOMETRY",
                "FIELD_TYPE_BIT",
                "SESSION_TRACK_BEGIN",

                // these function is not provides in mysql embedded version.
                "my_net_init",
                "net_flush",
                "mysql_errno_to_sqlstate",
                "check_scramble_323",
                "net_end",
                "get_tty_password_buff",
                "net_write_command",
                "get_salt_from_password_323",
                "get_salt_from_password",
                "my_net_write",
                "net_realloc",
                "hash_password",
                "net_real_write",
                "my_net_local_init",
                "scramble",
                "make_scrambled_password_323",
                "my_thread_init",
                "net_clear",
                "my_thread_end",
                "net_clear",
                "scramble_323",
                "my_net_read_packet",
                "create_random_string",
                "check_scramble",
                "my_net_read_packet_reallen",
                "make_scrambled_password",
                "octet2hex"
        );

        infoMap.put(new Info(
                "enum_server_command",
                "enum_indicator_type",
                "mariadb_field_attr_t",
                "enum_field_types",
                "mysql_enum_shutdown_level",
                "enum_cursor_type",
                "enum_mysql_set_option",
                "enum_session_state_type"
        ).enumerate(true).translate());

    }

    private void skip(InfoMap map, String... names) {
        map.put(new Info(names).skip());
    }

}
