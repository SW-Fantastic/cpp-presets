package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.MySQLResultSet;
import org.swdc.mariadb.embed.MySQLStatement;

import java.sql.*;

/**
 * Not implement yet
 */
public class MyStatement implements Statement {

    private MySQLStatement statement;

    private MyConnection connection;

    private MyResult curResultSet;

    private int lastAffectRows;


    private boolean doEscape;

    private int queryTimeout;

    private int maxRows;

    public MyStatement(MyConnection connection, MySQLStatement statement) {
        this.statement = statement;
        this.connection = connection;
    }

    /**
     * Build sql command to execute :
     *
     * SQL语句有时需要进行转义处理，通常是SQL的自定义函数，存储过程
     * 需要这样的转义，不同的数据库可能需要不同的转义，这里的转义仅针对于Mariadb。
     *
     * 另外，根据本Statement的配置，如果配置了超时和行数限制，那么也会添加到SQL语句中。
     *
     * <ul>
     *   <li>Execute escape substitution if needed / 如果需要，进行转义处理
     *   <li>add query timeout prefix if server permits it 如果需要，增加超时处理
     *   <li>add max row limit prefix if server permits it 如果需要，增加行数限制。
     * </ul>
     *
     * @param sql sql command / sql语句
     * @return sql command to execute / 处理完毕的sql语句
     * @throws SQLException if fails to escape sql
     */
    protected String escapeTimeout(String sql) throws SQLException {
        // 这个数据库的版本可以使用maxRows和timeOut.
        String escapedSql = doEscape ? NativeSQL.parse(sql) : sql;
        if (queryTimeout != 0) {
            if (maxRows > 0) {
                return "SET STATEMENT max_statement_time="
                        + queryTimeout
                        + ", SQL_SELECT_LIMIT="
                        + maxRows
                        + " FOR "
                        + escapedSql;
            }
            return "SET STATEMENT max_statement_time=" + queryTimeout + " FOR " + escapedSql;
        }
        if ( maxRows > 0) {
            return "SET STATEMENT SQL_SELECT_LIMIT=" + maxRows + " FOR " + escapedSql;
        }
        return escapedSql;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        MySQLResultSet rs = statement.executeQuery(
                escapeTimeout(sql)
        );
        if (rs == null) {
            return null;
        }
        curResultSet = new MyResult(rs);
        return curResultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        lastAffectRows = statement.executeUpdate(
                escapeTimeout(sql)
        );
        return lastAffectRows;
    }

    @Override
    public void close() throws SQLException {
    }

    /**
     * not support
     * 不支持，不进行实现。
     * @return
     * @throws SQLException
     */
    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    /**
     * not support
     * 不支持，不进行实现
     * @param max the new column size limit in bytes; zero means there is no limit
     * @throws SQLException
     */
    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.doEscape = enable;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        // 本地执行，不需要cancel
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new SQLException("Cursors are not supported");
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return statement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return curResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return lastAffectRows;
    }

    /**
     * 不做实现。
     * @param rows the number of rows to fetch
     * @throws SQLException
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
    }

    /**
     * 不做实现。
     * @return
     * @throws SQLException
     */
    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }


    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }


    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
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
