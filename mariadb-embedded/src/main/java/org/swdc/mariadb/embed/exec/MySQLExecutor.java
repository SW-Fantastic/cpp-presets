package org.swdc.mariadb.embed.exec;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.embed.EmbeddedMariaDB;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * Mariadb对线程有一定要求，
 * 所有Mariadb相关的数据库操作都需要进行Mysql_thread_init操作，所以
 * 我把它们都集中在一个单独的线程处理。
 */
public class MySQLExecutor extends Thread {

    private CountDownLatch latch = new CountDownLatch(1);

    private Deque<FutureTask> tasks = new ArrayDeque<>();

    private EmbeddedMariaDB mariaDB;

    private boolean state = true;

    public MySQLExecutor(EmbeddedMariaDB mariaDB) {
        setName("MariaDB Executor");
        this.mariaDB = mariaDB;
        start();
    }

    public <R> R execute(DBCallable<R> callable) throws SQLException {
        if (!state) {
            throw new RuntimeException("executor has shutdown");
        }

        FutureTask task = new FutureTask(() -> callable.call(mariaDB));
        tasks.push(task);
        if (latch != null) {
            latch.countDown();
        }
        try {
            return (R) task.get();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public void shutdown() {
        this.state = false;
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public void run() {

        while (state) {

            if (mariaDB.initialize()) {
                mariaDB.initSystemData();
                MariaDB.mysql_thread_init();
            }

            while (!tasks.isEmpty()) {
                FutureTask task = tasks.removeFirst();
                try {
                    task.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                latch = new CountDownLatch(1);
                latch.await();
                latch = null;
            } catch (Exception e) {
                latch = null;
            }
        }

        MariaDB.mysql_thread_end();
        mariaDB.shutdown();
    }


}
