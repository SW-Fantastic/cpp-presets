package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EmbeddedMariaDB {

    /**
     * singleton instance for mariadb
     * Mariadb的单例对象，每一个应用只需要一个这样的对象。
     */
    private static EmbeddedMariaDB instance;

    /**
     * Mariadb是否成功初始化
     */
    private boolean initialized;

    /**
     * Mariadb的数据文件夹。
     */
    private File dataDir;

    /**
     * Mariadb的公共文件夹。
     */
    private File baseDir;

    private String timeZoneId = "+0:00";

    private EmbeddedMariaDB(File dataDir, File baseDir) {
        this.baseDir = baseDir;
        this.dataDir = dataDir;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getDataDir() {
        return dataDir;
    }

    public void setBaseDir(File baseDir) {
        if (initialized) {
            throw new IllegalStateException("can not set a base dir on mariadb has initialize.");
        }
        this.baseDir = baseDir;
    }

    public void setDataDir(File dataDir) {
        if (initialized) {
            throw new IllegalStateException("can not set a data dir on mariadb has initialize.");
        }
        this.dataDir = dataDir;
    }

    /**
     * 初始化Mariadb的环境。
     */
    public boolean initialize() {

        if (!initialized) {
            synchronized (EmbeddedMariaDB.class) {
                if (!initialized) {
                    String[] argv = {
                            "mysql",    // 该参数会被忽略
                            "--console",  // 输出到控制台而不是文件，否则system.err会被重定向
                            "--skip-grant-tables",
                            "--default-time-zone=" + getTimeZoneId(),
                            "--datadir=" + dataDir.toPath().toAbsolutePath(),
                            "--basedir=" + baseDir.toPath().toAbsolutePath()
                    };

                    PointerPointer argvPointer = argvPointer(argv);

                    int rst = MariaDB.mysql_server_init(
                            argv.length,
                            argvPointer,
                            null
                    );

                    for (int idx = 0; idx < argv.length; idx ++) {

                        CharPointer cp = new CharPointer(argvPointer.get(idx));
                        cp.close();

                    }

                    MyGlobal.ext_char_list_free(argvPointer);

                    initialized = rst == 0;
                }
            }
        }

        return initialized;

    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 关闭Mariadb的环境
     */
    public void shutdown() {
        synchronized (EmbeddedMariaDB.class) {
            if (initialized) {
                MariaDB.mysql_server_end();
                initialized = false;
            }
        }
    }

    /**
     * 执行任意Mariadb的操作，该操作无需返回值。
     * @param runnable
     */
    public void withMySQL(Runnable runnable) {

        if (!initialized) {
            throw new RuntimeException("please initialize mariadb first!");
        }

        MariaDB.mysql_thread_init();
        runnable.run();
        MariaDB.mysql_thread_end();

    }

    private MYSQL getDefault() {

        if (!initialized) {
            throw new RuntimeException("please initialize mariadb first");
        }

        MYSQL mysql = MariaDB.mysql_init(null);
        if (mysql == null || mysql.isNull()) {
            return null;
        }
        mysql = MariaDB.mysql_real_connect(
                mysql,(String) null,null,null,null,0,null,0
        );
        if (mysql == null || mysql.isNull()) {
            return null;
        }

        return mysql;
    }

    private void freeDefault(MYSQL mysql) {
        if (mysql == null || mysql.isNull()) {
            return;
        }
        MariaDB.mysql_close(mysql);
    }

    public boolean createDatabase(String name, String charset, String coll) {

        MySQLDBConnection exist = connect(name);
        if (exist != null) {
            return true;
        }

        MYSQL mysql = getDefault();
        if (mysql == null) {
            return false;
        }

        if (charset == null || charset.isBlank()) {
            charset = "utf8mb4";
        }

        if (coll == null || coll.isBlank()) {
            coll = "utf8mb4_general_ci";
        }

        String createScript = "CREATE DATABASE " + name + " CHARACTER SET " + charset + " COLLATE " + coll;
        int state = MariaDB.mysql_real_query(mysql,createScript,createScript.length());
        freeDefault(mysql);

        return state == 0;

    }

    public List<String> getDatabases() {

        MYSQL mysql = getDefault();
        if (mysql == null) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();

        MYSQL_RES res = MariaDB.mysql_list_dbs(mysql,(String) null);
        PointerPointer dbPointers = null;
        while ((dbPointers = MariaDB.mysql_fetch_row(res)) != null && !dbPointers.isNull()) {
            BytePointer cp = new BytePointer(dbPointers.get(0));
            String data = cp.getString();
            results.add(data);
        }

        MariaDB.mysql_free_result(res);

        freeDefault(mysql);

        return results;
    }

    public MySQLDBConnection connect(String name) {

        if (!initialized) {
            throw new RuntimeException("please initialize mariadb first");
        }

        MYSQL mysql = MariaDB.mysql_init(null);
        if (mysql == null) {
            return null;
        }
        mysql = MariaDB.mysql_real_connect(
                mysql,(String) null,null,null,null,0,null,0
        );
        if (mysql == null || mysql.isNull()) {
            return null;
        }
        boolean state = MariaDB.mysql_select_db(mysql,name) == 0;
        if (!state) {
            MariaDB.mysql_close(mysql);
            return null;
        }

        return new MySQLDBConnection(mysql);

    }



    /**
     * 执行任意的Mariadb操作，该操作需要返回值。
     * @param runnable Block
     */
    public <T> T withMySQL(Function<Void, T> runnable) {

        if (!initialized) {
            throw new RuntimeException("please initialize mariadb first!");
        }

        MariaDB.mysql_thread_init();
        T result = runnable.apply(null);
        MariaDB.mysql_thread_end();

        return result;
    }

    private PointerPointer argvPointer(String[] argv) {

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

    /**
     * 获取单例对象。
     * @param baseDir 公共文件夹
     * @param dataDir 数据文件夹
     * @return Mariadb环境对象。
     */
    public synchronized static EmbeddedMariaDB getMariaDB(File baseDir, File dataDir) {
        if (instance == null) {
            instance = new EmbeddedMariaDB(dataDir,baseDir);
        }
        return instance;
    }
}
