package org.swdc.mariadb.embed.jdbc.results;

import org.swdc.mariadb.embed.jdbc.MyStatement;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCompleteResult extends MyResult {


    private List<Object[]> data = new ArrayList<>();

    private int curRow = -1;

    private MyStatement statement;

    private MyCompleteResultMetadata metadata = new MyCompleteResultMetadata();

    public MyCompleteResult(MyStatement statement) {
        this.statement = statement;
    }

    public MyCompleteResult field(int index, String field, Class type) {
        metadata.putColumn(index,field,type);
        return this;
    }

    public MyCompleteResult pushData(Object[] row) {
        data.add(row);
        return this;
    }

    @Override
    public boolean next() throws SQLException {
        if (curRow + 1 == data.size()) {
            return false;
        }
        curRow ++;
        return true;
    }

    @Override
    public boolean previous() throws SQLException {
        if (curRow - 1 < 0) {
            return false;
        }
        curRow = curRow - 1;
        return true;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean wasNull() throws SQLException {
        return data.isEmpty();
    }


    private <T> T getAs(int columnIndex, Class... type) throws SQLException {
        // columnIndex从1开始，所以这里需要额外减一。
        if (isAfterLast() || isBeforeFirst()) {
            throw new SQLException("call methods move cursor to a valid row first.");
        }
        Object[] data = this.data.get(curRow);
        if (data == null || data.length <= columnIndex - 1) {
            throw new SQLException("no such data");
        }
        for (Class item: type) {
            if (item == metadata.getJavaType(columnIndex - 1)) {
                return (T)data[columnIndex - 1];
            }
        }
        throw new SQLException("can not converter value");
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getAs(columnIndex,String.class);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return getAs(columnIndex,Boolean.class, boolean.class);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return getAs(columnIndex,Byte.class, byte.class);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return getAs(columnIndex,Short.class, short.class);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getAs(columnIndex,Integer.class, int.class);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return getAs(columnIndex,Long.class,long.class);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return getAs(columnIndex,float.class, Float.class);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return getAs(columnIndex,double.class, Double.class);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return getAs(columnIndex, byte[].class);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return getAs(columnIndex,Date.class);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return getAs(columnIndex,Time.class);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getAs(columnIndex, Timestamp.class);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return metadata.findColumn(columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getAs(columnIndex,BigDecimal.class);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return curRow == -1;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return curRow >= data.size();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return curRow == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        return curRow == data.size() - 1;
    }

    @Override
    public void beforeFirst() throws SQLException {
        curRow = -1;
    }

    @Override
    public void afterLast() throws SQLException {
        curRow = data.size();
    }

    @Override
    public boolean first() throws SQLException {
        curRow = 0;
        return true;
    }

    @Override
    public boolean last() throws SQLException {
        curRow = data.size() - 1;
        return true;
    }

    @Override
    public int getRow() throws SQLException {
        return curRow;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return absolute(curRow + rows);
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        if (row >= -1 && row < data.size() + 1) {
            curRow = row;
            return true;
        }
        return false;
    }

    @Override
    public int getType() throws SQLException {
        return TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return CONCUR_READ_ONLY;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return metadata;
    }
}
