package org.swdc.mariadb.test;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

public class MariaDBEmbedTest {

    public static void main(String[] args) {

        Loader.load(MariaDB.class);
        String[] argv = new String[] {
                "",
                "--console",
                "--skip-grant-tables",
                "--basedir=D:/JavaProjects/cpp-presets/mysqlData/base",
                "--datadir=D:/JavaProjects/cpp-presets/mysqlData/data",
        };


        MariaDB.mysql_server_init(argv.length,argvPointer(argv),null);
        MYSQL mysql = MariaDB.mysql_init(null);
        if (mysql == null) {
            System.out.println("Can not init default sql");
            return;
        }
        mysql = MariaDB.mysql_real_connect(mysql,(String) null,null,null,null,0,null,0);
        System.err.println("load databases");
        MYSQL_RES res = MariaDB.mysql_list_dbs(mysql,(String) null);

        boolean exist = false;
        String myCustomDB = "test_db";

        PointerPointer dbPointers = null;
        while ((dbPointers = MariaDB.mysql_fetch_row(res)) != null && !dbPointers.isNull()) {
            BytePointer cp = new BytePointer(dbPointers.get(0));
            String dbName = cp.getString();
            System.err.println("DBName : " + dbName);
            if (dbName.equals(myCustomDB)) {
                exist = true;
            }
        }

        if (!exist) {
            String createDB = "CREATE DATABASE " + myCustomDB;
            MariaDB.mysql_real_query(mysql,createDB,createDB.length());
        }

        MariaDB.mysql_free_result(res);
        MariaDB.mysql_close(mysql);

        MariaDB.mysql_server_end();
    }

    private static PointerPointer argvPointer(String[] argv) {

        PointerPointer argvPointer = MyGlobal.ext_alloc_char_list(argv.length);
        for (int idx = 0; idx < argv.length; idx ++) {
            String str = argv[idx];
            if (str == null) {
                continue;
            }
            BytePointer cp = new BytePointer(
                    Pointer.malloc( str.length() * Pointer.sizeof(BytePointer.class))
            );
            cp.putString(str);
            MyGlobal.ext_char_list_insert(argvPointer,cp,idx);
        }

        return argvPointer;

    }

}
