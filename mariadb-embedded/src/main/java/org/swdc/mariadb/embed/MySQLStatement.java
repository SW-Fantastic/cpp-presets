package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLStatement implements CloseableSource {

    private MYSQL connection;

    protected List<CloseableSource> activeResults = new ArrayList<>();

    protected CloseableListener closeableListener;


    protected MySQLStatement( MYSQL mysqlConnection) {
        this.connection = mysqlConnection;
    }

    public MySQLResultSet executeQuery(String sql) throws SQLException {
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException(
                    "can not execute query, errno : " + MariaDB.mysql_errno(connection) +
                            "\n caused by " + MariaDB.mysql_error(connection).getString()
            );
        }
        MYSQL_RES res = MariaDB.mysql_store_result(connection);;
        if (res == null || res.isNull()) {
            return null;
        }

        MySQLResultSet resultSet =  new MySQLResultSet(res);
        activeResults.add(resultSet);
        resultSet.setCloseListener(activeResults::remove);

        return resultSet;
    }

    public long executeUpdate(String sql) throws SQLException {
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException("can not execute query, errno : " + MariaDB.mysql_errno(connection) +
                    "\n caused by " + MariaDB.mysql_error(connection).getString()
            );
        }
        return MariaDB.mysql_affected_rows(connection);
    }

    public MySQLResultSet getLastGeneratedId() throws SQLException {
        String queryLastId = "SELECT LAST_INSERT_ID()";
        int state = MariaDB.mysql_real_query(connection, queryLastId,queryLastId.length());
        if (state != 0) {
            throw new SQLException("can not get generated id.");
        }
        MYSQL_RES res = MariaDB.mysql_store_result(connection);
        if (res == null || res.isNull()) {
            return null;
        }
        MySQLResultSet resultSet = new MySQLResultSet(res);
        activeResults.add(resultSet);
        resultSet.setCloseListener(activeResults::remove);
        return resultSet;
    }

    public boolean execute(String sql) throws SQLException {
        boolean res = MariaDB.mysql_real_query(connection,sql,sql.length()) == 0;
        if (!res) {
            throw new SQLException(
                    "failed to execute query , errno : " + MariaDB.mysql_errno(connection) +
                    "\n caused by : " + MariaDB.mysql_error(connection).getString()
            );
        }
        return true;
    }

    @Override
    public synchronized void close() {

        if(closeBySource()) {
            if (closeableListener != null) {
                closeableListener.closed(this);
            }
        }
    }

    @Override
    public void setCloseListener(CloseableListener listener) {
        this.closeableListener = listener;
    }

    @Override
    public synchronized boolean closeBySource() {
        if (!activeResults.isEmpty()) {
            for (CloseableSource closeable : activeResults) {
                closeable.closeBySource();
            }
            activeResults.clear();
            return true;
        }
        return false;
    }
}
