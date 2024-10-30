package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;
import org.swdc.mariadb.embed.jdbc.MyThreadHolder;

import java.io.File;
import java.io.InputStream;
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

    private volatile static boolean systemClosed = false;

    private List<CloseableSource> activeConnections = new ArrayList<>();

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
                            "--basedir=" + baseDir.toPath().normalize().toAbsolutePath(),
                            "--datadir=" + dataDir.toPath().normalize().toAbsolutePath(),
                            "--console",  // 输出到控制台而不是文件，否则system.err会被重定向
                            "--skip-grant-tables",
                            "--default-time-zone=" + getTimeZoneId(),
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
                    if (initialized) {
                        Runtime.getRuntime().addShutdownHook(
                                new Thread(EmbeddedMariaDB::shutdownEnvironment)
                        );
                    }
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

        if (!initialized || systemClosed) {
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

        MariaDB.mysql_set_character_set(mysql, "utf8");

        MySQLDBConnection connection = new MySQLDBConnection(mysql);
        activeConnections.add(connection);
        connection.setCloseListener(activeConnections::remove);
        return connection;
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

    public synchronized void initSystemData() {

        if (systemClosed) {
            return;
        }

        MySQLDBConnection exist = connect("mysql");
        if (exist != null) {
            return;
        }

        String initScript = "";
        String initTableScript = "";
        String initSysDataScript = "";

        try {

            InputStream initSystemTables = EmbeddedMariaDB.class.getModule().getResourceAsStream("mysystem/mariadb_system_tables.sql");
            initScript = new String(initSystemTables.readAllBytes());
            initSystemTables.close();

            InputStream initTables = EmbeddedMariaDB.class.getModule().getResourceAsStream("mysystem/mariadb_performance_tables.sql");
            initTableScript = new String(initTables.readAllBytes());
            initTables.close();

            InputStream initSystemData = EmbeddedMariaDB.class.getModule().getResourceAsStream("mysystem/mariadb_system_tables_data.sql");
            initSysDataScript = new String(initSystemData.readAllBytes());
            initSystemData.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MYSQL conn = getDefault();
        String initMySQL = "create database if not exists mysql;";
        int rst = MariaDB.mysql_real_query(conn,initMySQL,initMySQL.length());
        if (rst != 0) {
            freeDefault(conn);
            throw new RuntimeException("can not init mysql system database, errno : " + MariaDB.mysql_errno(conn));
        }

        rst = MariaDB.mysql_select_db(conn,"mysql");
        if (rst != 0) {
            freeDefault(conn);
            throw new RuntimeException("can not init mysql system database, errno : " + MariaDB.mysql_errno(conn));
        }

        initMySQL = "SET @auth_root_socket=NULL;";
        MariaDB.mysql_real_query(conn,initMySQL,initMySQL.length());

        boolean state = executeScript(conn,initScript);
        if (!state) {
            String msg = "can not init mysql system database, errno : " + MariaDB.mysql_errno(conn) + " " + MariaDB.mysql_error(conn).getString();
            freeDefault(conn);
            throw new RuntimeException(msg);
        }

        state = executeScript(conn,initTableScript);
        if (!state) {
            String msg = "can not init mysql system database, errno : " + MariaDB.mysql_errno(conn) + " " + MariaDB.mysql_error(conn).getString();
            freeDefault(conn);
            throw new RuntimeException(msg);
        }

        state = executeScript(conn,initSysDataScript);
        if (!state) {
            String msg = "can not init mysql system database, errno : " + MariaDB.mysql_errno(conn) + " " + MariaDB.mysql_error(conn).getString();
            freeDefault(conn);
            throw new RuntimeException(msg);
        }

        freeDefault(conn);
    }


    private static boolean executeScript(MYSQL conn, String script) {
        List<String> initSplit = splitSQLScript(script);
        for (String sc: initSplit) {
            int rst = MariaDB.mysql_real_query(conn,sc,sc.length());
            if (rst != 0) {
                return false;
            }
        }
        return true;
    }

    private static List<String> splitSQLScript(String sql) {
        String[] splited = sql.split("\n");
        StringBuilder sb = new StringBuilder();
        List<String> results = new ArrayList<>();
        for (String item : splited) {
            if (item.startsWith("--")) {
                continue;
            }
            if (item.endsWith(";")) {
                sb.append("\n").append(item);
                results.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append("\n").append(item);
            }
        }
        return results;
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

    public boolean isEnvironmentClosed() {
        return systemClosed;
    }


    public synchronized static void shutdownEnvironment() {
        systemClosed = true;
        MariaDB.mysql_thread_init();
        if (instance != null && instance.initialized) {
            for (CloseableSource connections : instance.activeConnections) {
                connections.closeBySource();
            }
            instance.activeConnections.clear();
            instance.shutdown();
        }
    }
}
