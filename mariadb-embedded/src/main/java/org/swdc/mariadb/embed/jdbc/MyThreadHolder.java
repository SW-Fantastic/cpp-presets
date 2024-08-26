package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.core.MariaDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyThreadHolder {

    private static Map<Thread,List<MyConnection>> mysqlThreads = new ConcurrentHashMap<>();


    public static void threadVerify(MyConnection connection) {
        List<MyConnection> connections = mysqlThreads.computeIfAbsent(
                Thread.currentThread(),  v -> new ArrayList<>()
        );
        if (connections.isEmpty()) {
            connections.add(connection);
            MariaDB.mysql_thread_init();
        }
    }

    public static void threadRelease(MyConnection connection) {
        List<MyConnection> connections = mysqlThreads.computeIfAbsent(
                Thread.currentThread(),  v -> new ArrayList<>()
        );
        if (connections.isEmpty()) {
            connections.add(connection);
            MariaDB.mysql_thread_end();
        }
    }

}
