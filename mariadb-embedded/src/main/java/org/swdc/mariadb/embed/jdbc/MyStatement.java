package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.MySQLResultSet;
import org.swdc.mariadb.embed.MySQLStatement;
import org.swdc.mariadb.embed.jdbc.results.MyCompleteResult;
import org.swdc.mariadb.embed.jdbc.results.MyQueryResult;
import org.swdc.mariadb.embed.jdbc.results.MyQueryUpdatableResult;
import org.swdc.mariadb.embed.jdbc.results.MyResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MyStatement implements Statement {

    protected MySQLStatement statement;

    protected MyConnection connection;

    private MyResult curResultSet;

    private long lastAffectRows;

    private boolean doEscape;

    private int queryTimeout;

    private int maxRows;

    private int resultConcurrency;

    private int resultType;

    private List<String> batchSql = new ArrayList<>();

    private List<Long> generateKeys = new ArrayList<>();

    public MyStatement(MyConnection connection, MySQLStatement statement,int resultType, int resultConcurrency) {
        this.statement = statement;
        this.connection = connection;
        this.resultType = resultType;
        this.resultConcurrency = resultConcurrency;
    }

    protected <T extends MySQLStatement> T getStmt() throws SQLException {
        return (T) statement;
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
        MySQLResultSet rs = getStmt().executeQuery(
                escapeTimeout(sql)
        );
        if (rs == null) {
            return null;
        }
        if (resultConcurrency == ResultSet.CONCUR_UPDATABLE) {
            curResultSet = new MyQueryUpdatableResult(this,rs,resultType);
        } else {
            curResultSet = new MyQueryResult(this,rs,resultType);
        }
        return curResultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql,Statement.NO_GENERATED_KEYS);
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return executeLargeUpdate(sql,Statement.NO_GENERATED_KEYS);
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
        return getStmt().execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return curResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return (int)lastAffectRows;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        this.generateKeys.clear();
        return executeUpdateInternal(sql,autoGeneratedKeys,this.generateKeys);
    }

    public int executeUpdateInternal(String sql, int autoGeneratedKeys, List<Long> insertedIds) throws SQLException {

        lastAffectRows = getStmt().executeUpdate(
                escapeTimeout(sql)
        );
        if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
            MySQLResultSet rs = getStmt().getLastGeneratedId();
            if (rs != null && rs.next()) {
                insertedIds.add(rs.getLong(0));
            }
        }
        return (int)lastAffectRows;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeLargeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeLargeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        this.generateKeys.clear();
        return executeUpdateInternal(sql,autoGeneratedKeys,generateKeys);
    }

    private long executeLargeUpdateInternal(String sql, int autoGeneratedKeys,List<Long> generateKeys) throws SQLException {
        lastAffectRows = getStmt().executeUpdate(
                escapeTimeout(sql)
        );
        if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
            MySQLResultSet rs = getStmt().getLastGeneratedId();
            if (rs != null && rs.next()) {
                generateKeys.add(rs.getLong(0));
            }
        }
        return lastAffectRows;
    }


    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        this.generateKeys.clear();
        return executeInternal(sql,autoGeneratedKeys,this.generateKeys);
    }

    private boolean executeInternal(String sql, int autoGeneratedKeys,List<Long> generateKeys) throws SQLException {
        sql = escapeTimeout(sql);
        boolean result = getStmt().execute(sql);
        if (result && autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
            MySQLResultSet rs = getStmt().getLastGeneratedId();
            if (rs != null && rs.next()) {
                generateKeys.add(rs.getLong(0));
            }
            return true;
        }
        return result;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql,Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql,Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        MyCompleteResult result = new MyCompleteResult(this)
                .field(0,"LAST_INSERT_ID", Long.class);
        for (Long generateId : generateKeys) {
            result.pushData(new Object[] { generateId });
        }
        return result;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        batchSql.add(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        batchSql.clear();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        this.generateKeys.clear();
        int[] results = new int[batchSql.size()];
        for (int idx = 0; idx < batchSql.size(); idx ++) {
            results[idx] = executeUpdateInternal(
                    batchSql.get(idx),
                    Statement.RETURN_GENERATED_KEYS,
                    this.generateKeys
            );
        }
        return results;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        long[] results = new long[batchSql.size()];
        for (int idx = 0; idx < batchSql.size(); idx ++) {
            results[idx] = executeLargeUpdateInternal(
                    batchSql.get(idx),
                    Statement.RETURN_GENERATED_KEYS,
                    this.generateKeys
            );
        }
        return results;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
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
    public boolean getMoreResults(int current) throws SQLException {
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
