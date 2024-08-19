package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.sql.SQLException;

public class MySQLStatement {

    private MYSQL connection;


    protected MySQLStatement( MYSQL mysqlConnection) {
        this.connection = mysqlConnection;
    }

    public MySQLResultSet executeQuery(String sql) throws SQLException {
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException("can not execute query, errno : " + MariaDB.mysql_errno(connection));
        }
        MYSQL_RES res = MariaDB.mysql_store_result(connection);;
        if (res == null || res.isNull()) {
            return null;
        }
        return  new MySQLResultSet(res);
    }

    public long executeUpdate(String sql) throws SQLException {
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException("can not execute query, errno : " + MariaDB.mysql_errno(connection));
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
        return new MySQLResultSet(res);
    }

    public boolean execute(String sql) throws SQLException {
        return MariaDB.mysql_real_query(connection,sql,sql.length()) == 0;
    }
    
}
