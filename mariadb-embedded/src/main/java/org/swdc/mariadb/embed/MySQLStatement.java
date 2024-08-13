package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.sql.SQLException;

public class MySQLStatement {

    private MYSQL connection;

    private MySQLDBConnection conn;

    protected MySQLStatement(MySQLDBConnection conn, MYSQL mysqlConnection) {
        this.connection = mysqlConnection;
        this.conn = conn;
    }

    public MySQLResultSet executeQuery(String sql) throws SQLException {
        if (conn.getSteamingResult() != null) {
            throw new SQLException("can not execute query while a result set is reading.");
        }
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException("can not execute query, errno : " + MariaDB.mysql_errno(connection));
        }
        MYSQL_RES res = MariaDB.mysql_store_result(connection);;
        if (res == null || res.isNull()) {
            return null;
        }
        return  new MySQLResultSet(conn,res);
    }

    public int executeUpdate(String sql) throws SQLException {
        int state = MariaDB.mysql_real_query(connection,sql,sql.length());
        if (state != 0) {
            throw new SQLException("can not execute query, errno : " + MariaDB.mysql_errno(connection));
        }
        return (int)MariaDB.mysql_affected_rows(connection);
    }

    public boolean execute(String sql) throws SQLException {
        return MariaDB.mysql_real_query(connection,sql,sql.length()) == 0;
    }
    
}
