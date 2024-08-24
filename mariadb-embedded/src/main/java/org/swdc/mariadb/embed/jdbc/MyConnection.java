package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.embed.MySQLDBConnection;
import org.swdc.mariadb.embed.MySQLStatement;

import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Not implement yet
 */
public class MyConnection implements Connection {

    public MySQLDBConnection connection;

    private int lowercaseTableNames;

    private final AtomicInteger savepointId = new AtomicInteger();


    public MyConnection(MySQLDBConnection conn) {
        this.connection = conn;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        MySQLStatement statement = connection.createStatement();
        if (statement != null) {
            return new MyStatement(
                    this,
                    connection.createStatement(),
                    resultSetType,
                    resultSetConcurrency
            );
        }
        throw new SQLException("failed to create statement.");
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return NativeSQL.parse(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return (connection.getServerStatus() & MyCom.SERVER_STATUS_AUTOCOMMIT) > 0;
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        if (!isClosed()) {
            connection.close();
        } else {
            throw new SQLException("connection has closed.");
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            connection.valid();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MyDBMetadata(this);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    /**
     * 切换数据库。
     * Switch the database
     * @param catalog the name of a catalog (subspace in this
     *        {@code Connection} object's database) in which to work
     * @throws SQLException
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (catalog == null || catalog.isBlank()) {
            return;
        }
        if(!connection.selectDB(catalog)) {
            throw new RuntimeException("failed to change the database");
        }
    }

    /**
     * 获取当前的数据库
     * get current connected database
     * @return database name / 数据库名
     * @throws SQLException
     */
    @Override
    public String getCatalog() throws SQLException {
        return connection.getConnectedDB();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setJDBCTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getJDBCTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new MyPreparedStatement(this,sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return Collections.emptyMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("set type map is not supported.");
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException("set holdability is not supported.");
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        MySavePoint savePoint = new MySavePoint(savepointId.incrementAndGet());
        MySQLStatement statement = connection.createStatement();
        if(statement.execute("SAVEPOINT `" + savePoint.rawValue() + "`")) {
            statement.close();
            return savePoint;
        }
        throw new SQLException("failed to create savepoint .");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        MySQLStatement statement = connection.createStatement();
        if (statement != null) {
            return new MyStatement(
                    this,
                    connection.createStatement(),
                    resultSetType,
                    resultSetConcurrency
            );
        }
        throw new SQLException("failed to create statement.");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    /**
     * Are table case-sensitive or not . Default Value: 0 (Unix), 1 (Windows), 2 (Mac OS X). If set to
     * 0 (the default on Unix-based systems), table names and aliases and database names are compared
     * in a case-sensitive manner. If set to 1 (the default on Windows), names are stored in lowercase
     * and not compared in a case-sensitive manner. If set to 2 (the default on Mac OS X), names are
     * stored as declared, but compared in lowercase.
     *
     * @return int value.
     * @throws SQLException if a connection error occur
     */
    public int getLowercaseTableNames() throws SQLException {
        if (lowercaseTableNames == -1) {
            try (java.sql.Statement st = createStatement()) {
                try (ResultSet rs = st.executeQuery("select @@lower_case_table_names")) {
                    rs.next();
                    lowercaseTableNames = rs.getInt(1);
                }
            }
        }
        return lowercaseTableNames;
    }

    @Override
    public Clob createClob() throws SQLException {
        return new MyClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return new MyBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return new MyClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("SQLXML type is not supported");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("Array type is not supported");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Struct type is not supported");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        // Mysql不支持这个。
        // We support only catalog, and JDBC indicate "If the driver does not support schemas, it will
        // silently ignore this request."
    }

    @Override
    public String getSchema() throws SQLException {
        // Mysql不支持这个
        // We support only catalog
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

}
