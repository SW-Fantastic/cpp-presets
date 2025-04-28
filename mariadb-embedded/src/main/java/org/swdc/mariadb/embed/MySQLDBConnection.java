package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MySQLDBConnection implements CloseableSource {

    private MYSQL mariaDB;

    private List<CloseableSource> activeStatements = new ArrayList<>();

    private CloseableListener closeableListener;

    private String timeZoneId;

    protected MySQLDBConnection(MYSQL db, String timeZoneId) {
        this.mariaDB = db;
        this.timeZoneId = timeZoneId;
    }

    public void valid() {

        if (mariaDB == null || mariaDB.isNull()) {
            throw new RuntimeException("this object has closed.");
        }

    }

    public synchronized List<String> getTables(String wild) {

        valid();

        List<String> results = new ArrayList<>();

        MYSQL_RES res = MariaDB.mysql_list_tables(mariaDB, wild);
        PointerPointer dbPointers = null;
        while ((dbPointers = MariaDB.mysql_fetch_row(res)) != null && !dbPointers.isNull()) {
            BytePointer cp = new BytePointer(dbPointers.get(0));
            String data = cp.getString();
            results.add(data);
            dbPointers.close();
        }
        MariaDB.mysql_free_result(res);

        return results;
    }

    public synchronized List<TableField> listFields(String tableName) {

        valid();

        String query = "SHOW COLUMNS FROM " + tableName;
        int rst = MariaDB.mysql_real_query(mariaDB,query,query.length());
        if (rst != 0) {
            return Collections.emptyList();
        }

        MYSQL_RES res = MariaDB.mysql_use_result(mariaDB);
        if (res == null || res.isNull()) {
            return Collections.emptyList();
        }

        try {
            List<TableField> fields = FieldMapper.mapFromResult(res,TableField.class);
            MariaDB.mysql_free_result(res);
            return fields;
        } catch (Exception e) {
            MariaDB.mysql_free_result(res);
            throw new RuntimeException(e);
        }

    }

    public long getMajorVersion() {
        valid();
        long ver = MariaDB.mysql_get_server_version(mariaDB);
        return ver / 10000;
    }

    public long getMinerVersion() {
        valid();
        long ver = MariaDB.mysql_get_server_version(mariaDB);
        return (ver % 10000) / 100;
    }

    public long getSubVersion() {
        valid();
        long ver = MariaDB.mysql_get_server_version(mariaDB);
        return ver % 100;
    }

    public int getServerStatus() {
        valid();
        return mariaDB.server_status();
    }

    public String getConnectedDB() {
        valid();
        return mariaDB.db().getString();
    }

    public boolean isAutoCommit() throws SQLException {
        valid();

        MySQLStatement statement = new MySQLStatement(mariaDB,getTimeZoneId());
        MySQLResultSet rs = statement.executeQuery("SELECT @@autocommit");
        if (rs != null && rs.next()) {
            boolean result = rs.getLong(0) == '1';
            rs.close();
            return result;
        }
        throw new SQLException("failed to get autocommit status");
    }

    public boolean setAutoCommit(boolean autoCommit) {
        valid();
        boolean rs = MariaDB.mysql_autocommit(
                mariaDB,autoCommit ? (byte) 1 : (byte) 0
        ) == 0;
        if (rs) {
            MariaDB.mysql_commit(mariaDB);
        }
        return rs;
    }

    public void commit() {
        valid();
        MariaDB.mysql_commit(mariaDB);
    }

    public MySQLStatement createStatement() {

        valid();
        MySQLStatement statement = new MySQLStatement(mariaDB,getTimeZoneId());
        activeStatements.add(statement);
        statement.setCloseListener(activeStatements::remove);
        return statement;

    }


    public int getJDBCTransactionIsolation() throws SQLException {

        valid();

        MySQLStatement statement = new MySQLStatement(mariaDB,getTimeZoneId());
        MySQLResultSet rs = statement.executeQuery("SELECT @@tx_isolation");
        if (rs != null && rs.next()) {
            String txType = rs.getString(0);
            rs.close();
            if (txType == null) {
                throw new SQLException("Can not read tx isolation type");
            } else if (txType.equals("REPEATABLE-READ")) {
                return Connection.TRANSACTION_REPEATABLE_READ;
            } else if (txType.equals("READ-UNCOMMITTED")) {
                return Connection.TRANSACTION_READ_UNCOMMITTED;
            } else if (txType.equals("READ-COMMITTED")) {
                return Connection.TRANSACTION_READ_COMMITTED;
            } else if (txType.equals("SERIALIZABLE")) {
                return Connection.TRANSACTION_SERIALIZABLE;
            } else {
                throw new SQLException("Could not get transaction isolation level: Invalid value " + txType);
            }
        }
        throw new SQLException("failed to get tx isolation");

    }

    public void setJDBCTransactionIsolation(int level) throws SQLException {
        String query = "SET SESSION TRANSACTION ISOLATION LEVEL";
        switch (level) {
            case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED:
                query += " READ UNCOMMITTED";
                break;
            case java.sql.Connection.TRANSACTION_READ_COMMITTED:
                query += " READ COMMITTED";
                break;
            case java.sql.Connection.TRANSACTION_REPEATABLE_READ:
                query += " REPEATABLE READ";
                break;
            case java.sql.Connection.TRANSACTION_SERIALIZABLE:
                query += " SERIALIZABLE";
                break;
            default:
                throw new SQLException("Unsupported transaction isolation level");
        }
        MySQLStatement statement = new MySQLStatement(mariaDB,getTimeZoneId());
        if(!statement.execute(query)) {
            throw new SQLException("Failed to change transaction isolation level.");
        }
    }

    public MySQLPreparedStatement preparedStatement(String sql) {
        valid();
        return new MySQLPreparedStatement(mariaDB,sql,getTimeZoneId());
    }

    public boolean rollback() {
        valid();
        return MariaDB.mysql_rollback(mariaDB) == 0;
    }

    public boolean selectDB(String db) {
        valid();
        return MariaDB.mysql_select_db(mariaDB,db) == 0;
    }


    @Override
    public void close() {
        if (closeBySource() && this.closeableListener != null) {
            this.closeableListener.closed(this);
        }
    }

    @Override
    public void setCloseListener(CloseableListener listener) {
        this.closeableListener = listener;
    }

    @Override
    public boolean closeBySource() {

        if (mariaDB != null && !mariaDB.isNull()) {

            for (CloseableSource source : activeStatements) {
                source.closeBySource();
            }
            activeStatements.clear();

            MariaDB.mysql_close(mariaDB);
            mariaDB = null;
            return true;
        }

       return false;

    }

    public String getTimeZoneId() {
        return timeZoneId;
    }
}
