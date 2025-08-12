package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.core.MariaDB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyThreads {

    private static Map<MyConnection, Thread> threads = new ConcurrentHashMap<>();

    /**
     * 验证当前线程是否初始化为MariaDB线程。
     * 在进行SQL操作前，如果当前线程尚未初始化为Mariadb线程，则初始化它。
     * @param connection MariaDB连接对象
     */
    public static void threadVerify(MyConnection connection) {

        if (threads.containsKey(connection) && threads.get(connection) == Thread.currentThread()) {
            return;
        }
        threads.put(connection, Thread.currentThread());
        MariaDB.mysql_thread_init();

    }

    /**
     * 释放当前线程的MariaDB资源。
     * 在执行完SQL操作后，释放当前线程的资源。
     * @param connection MariaDB连接对象
     */
    public static void threadRelease(MyConnection connection) {

        if (threads.containsKey(connection) && threads.get(connection) == Thread.currentThread()) {
            threads.remove(connection);
            MariaDB.mysql_thread_end();
        }

    }

    public static void cleanCurrentThread() {
        if (threads.containsKey(null) && threads.get(null) == Thread.currentThread()) {
            threads.remove(null);
            MariaDB.mysql_thread_end();
        }
    }

}
