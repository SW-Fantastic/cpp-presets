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
                include = {"mysql.h" },
                linkpath = "platforms/Mariadb/dll/windows/x86_64",
                link = "libmysqld"
        ),
},
        target = "org.swdc.mariadb.core.mysql",
        global = "org.swdc.mariadb.core.MariaDB",
        inherit = { MyGlobalConfigure.class, MyComConfigure.class }
)
public class MariaDBConfigure implements InfoMapper {


    /**
     *
     * remove following rows in mysql.h or build will be fail on Windows。
     * 从mysql.h移除下面的内容，否则在Windows下的构建将会失败。
     *
     * #include "mariadb_capi_rename.h"
     *
     */
    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info(
                "mysql_option",
                "mysql_status",
                "mysql_protocol_type"
        ).enumerate(true).translate());


        infoMap.put(new Info("embedded_query_result").cppTypes("EMBEDDED_QUERY_RESULT").translate());
        infoMap.put(new Info("MYSQL_ROW").cppText("#define MYSQL_ROW char**"));
        infoMap.put(new Info("st_mysql_rows").cppTypes("MYSQL_ROWS").translate());
        infoMap.put(new Info("st_mysql_bind").cppTypes("MYSQL_BIND").translate());
        infoMap.put(new Info("st_mysql_stmt").cppTypes("MYSQL_STMT").translate());

        infoMap.put(new Info("charset_info_st").cppTypes("CHARSET_INFO").translate());

        skip(
                infoMap, "MYSQL_COUNT_ERROR",
                "ER_WARN_DATA_TRUNCATED",
                "WARN_PLUGIN_DELETE_BUILTIN",
                "ER_FK_DUP_NAME",
                "ER_VIRTUAL_COLUMN_FUNCTION_IS_NOT_ALLOWED",
                "ER_PRIMARY_KEY_BASED_ON_VIRTUAL_COLUMN",
                "ER_WRONG_FK_OPTION_FOR_VIRTUAL_COLUMN",
                "ER_UNSUPPORTED_ACTION_ON_VIRTUAL_COLUMN",
                "ER_UNSUPPORTED_ENGINE_FOR_VIRTUAL_COLUMNS",
                "ER_KEY_COLUMN_DOES_NOT_EXITS",
                "ER_DROP_PARTITION_NON_EXISTENT",
                "max_allowed_packet",
                "net_buffer_length",
                "mysql_library_init",
                "mysql_library_end",
                "st_dynamic_array",
                "mysql_create_db",
                "mysql_drop_db",
                "mysql_connect",
                // following function is not provided
                "server_mysql_send_query",
                "mysql_stat_cont",
                "server_mysql_num_fields",
                "mysql_list_dbs_start",
                "server_mysql_get_server_version",
                "mysql_real_connect_start",
                "mysql_read_query_result_cont",
                "mysql_fetch_row_cont",
                "mysql_rollback_cont",
                "mysql_stmt_send_long_data_start",
                "mysql_autocommit_cont",
                "mysql_stmt_store_result_cont",
                "mysql_select_db_cont",
                "mysql_refresh_cont",
                "server_mysql_options",
                "mysql_list_tables_start",
                "mysql_kill_start",
                "mysql_set_server_option_cont",
                "server_mysql_set_character_set",
                "mysql_ping_cont",
                "mysql_set_server_option_start",
                "mysql_dump_debug_info_cont",
                "mysql_select_db_start",
                "mysql_stmt_close_start",
                "mysql_close_cont",
                "mysql_store_result_start",
                "mysql_change_user_cont",
                "mysql_real_query_cont",
                "mysql_query_cont",
                "mysql_kill_cont",
                "mysql_get_timeout_value",
                "server_mysql_real_escape_string",
                "mysql_stmt_free_result_cont",
                "mysql_stmt_execute_cont",
                "mysql_list_fields_cont",
                "mysql_list_processes_start",
                "mysql_autocommit_start",
                "mysql_next_result_cont",
                "mysql_fetch_row_start",
                "mysql_store_result_cont",
                "mysql_list_dbs_cont",
                "mysql_get_timeout_value_ms",
                "server_mysql_real_query",
                "mysql_ping_start",
                "mysql_stat_start",
                "mysql_free_result_cont",
                "mysql_stmt_reset_cont",
                "server_mysql_options4",
                "mysql_shutdown_cont",
                "mysql_stmt_store_result_start",
                "mysql_stmt_prepare_start",
                "mysql_shutdown_start",
                "mysql_send_query_cont",
                "mysql_list_processes_cont",
                "mysql_real_connect_cont",
                "mysql_next_result_start",
                "mysql_close_start",
                "server_mysql_ssl_set",
                "mysql_free_result_start",
                "mysql_stmt_next_result_cont",
                "mysql_real_query_start",
                "mysql_stmt_close_cont",
                "mysql_set_character_set_start",
                "server_mysql_errno",
                "server_mysql_select_db",
                "mysql_unix_port",
                "server_mysql_free_result",
                "mysql_stmt_next_result_start",
                "mysql_refresh_start",
                "mysql_send_query_start",
                "mysql_stmt_fetch_start",
                "mysql_stmt_send_long_data_cont",
                "mysql_read_query_result_start",
                "mysql_stmt_free_result_start",
                "mysql_stmt_reset_start",
                "mysql_commit_cont",
                "mysql_change_user_start",
                "mysql_commit_start",
                "mysql_stmt_execute_start",
                "mysql_list_fields_start",
                "server_mysql_close",
                "mysql_query_start",
                "mysql_stmt_prepare_cont",
                "mysql_port",
                "mysql_stmt_fetch_cont",
                "mysql_close_slow_part",
                "mysql_list_tables_cont",
                "mysql_rollback_start",
                "mysql_set_character_set_cont",
                "mysql_dump_debug_info_start",
                "mariadb_connection",
                "server_mysql_fetch_row",
                "server_mysql_affected_rows",
                "server_mysql_num_rows",
                "mysql_get_server_name"
        );

    }

    private void skip(InfoMap map, String... names) {
        map.put(new Info(names).skip());
    }

}
