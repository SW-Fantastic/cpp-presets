package org.swdc.mariadb.embed.jdbc.results;

import org.swdc.mariadb.embed.IMySQLResultSet;
import org.swdc.mariadb.embed.MySQLResultSet;
import org.swdc.mariadb.embed.exec.MySQLExecutor;
import org.swdc.mariadb.embed.jdbc.MyStatement;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MyQueryResult extends MyResult {

    protected IMySQLResultSet resultSet;

    protected MyStatement statement;

    protected int type;

    protected MySQLExecutor executor;

    public MyQueryResult(MySQLExecutor executor, MyStatement connection, IMySQLResultSet rs) {
        this(executor,connection,rs,TYPE_FORWARD_ONLY);
    }

    public MyQueryResult(MySQLExecutor executor, MyStatement statement,IMySQLResultSet rs, int type) {
        this.resultSet = rs;
        this.type = type;
        this.statement = statement;
        this.executor = executor;
    }

    @Override
    public boolean next() throws SQLException {
        return executor.execute(db -> {
            return resultSet.next();
        });
    }

    @Override
    public void close() throws SQLException {
        resultSet.close();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return resultSet == null;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex - 1);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return resultSet.getBoolean(columnIndex - 1);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return resultSet.getByte(columnIndex - 1);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return resultSet.getShort(columnIndex - 1);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex - 1);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex - 1);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return resultSet.getFloat(columnIndex - 1);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return resultSet.getDouble(columnIndex - 1);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return resultSet.getBlob(columnIndex - 1);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return resultSet.getDate(columnIndex - 1);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return resultSet.getTime(columnIndex - 1);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        Long val = resultSet.getTimestamp(columnIndex - 1);
        if (val == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(
                val,0, ZoneOffset.UTC
        );
        return Timestamp.valueOf(dateTime);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return resultSet.getDecimal(columnIndex - 1);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex - 1);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return resultSet.findColumn(columnLabel);
    }

    @Override
    public boolean isFirst() throws SQLException {
        return resultSet.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return resultSet.isLast();
    }

    @Override
    public boolean first() throws SQLException {
        resultSet.firstRow();
        return true;
    }

    @Override
    public void beforeFirst() throws SQLException {
        resultSet.beforeFirst();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return resultSet.isBeforeFirst();
    }

    @Override
    public boolean last() throws SQLException {
        resultSet.lastRow();
        return true;
    }

    @Override
    public void afterLast() throws SQLException {
        resultSet.afterLast();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return resultSet.isAfterLast();
    }

    @Override
    public boolean previous() throws SQLException {
        return resultSet.previous();
    }

    @Override
    public int getRow() throws SQLException {
        return resultSet.getCurrentRowNum();
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return resultSet.seek(row);
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return resultSet.seek(getRow() + rows);
    }

    @Override
    public int getConcurrency() throws SQLException {
        return CONCUR_READ_ONLY;
    }

    @Override
    public int getType() throws SQLException {
        return type;
    }

    @Override
    public MyStatement getStatement() {
        return statement;
    }

    public void setStatement(MyStatement statement) {
        this.statement = statement;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MyResultMetadata(resultSet.getMetadata());
    }

}
