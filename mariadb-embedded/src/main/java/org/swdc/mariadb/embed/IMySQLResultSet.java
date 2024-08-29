package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MyCom;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

public interface IMySQLResultSet extends Closeable {
    /**
     * 判断指定的field的类型是否在提供的field类型列表中。
     *
     * @param type  field类型
     * @param types filed类型列表
     * @return 在列表中时返回true
     */
    static boolean accept(MyCom.enum_field_types type, MyCom.enum_field_types... types) {
        for (MyCom.enum_field_types item : types) {
            if (item.value == type.value) {
                return true;
            }
        }
        return false;
    }

    boolean next() throws SQLException;

    boolean previous() throws SQLException;

    boolean seek(long rowNum) throws SQLException;

    boolean beforeFirst() throws SQLException;

    boolean afterLast() throws SQLException;

    boolean isBeforeFirst();

    boolean isAfterLast();

    void firstRow() throws SQLException;

    void lastRow() throws SQLException;

    boolean isFirst() throws SQLException;

    boolean isLast() throws SQLException;

    int findColumn(String label) throws SQLException;

    default Date getDate(String column) throws SQLException {
        return getDate(findColumn(column));
    }

    Date getDate(int column) throws SQLException;

    default Time getTime(String column) throws SQLException {
        return getTime(findColumn(column));
    }

    Time getTime(int column) throws SQLException;

    default Byte getByte(String column) throws SQLException {
        return getByte(findColumn(column));
    }

    Byte getByte(int column) throws SQLException;

    default Short getShort(String column) throws SQLException {
        return getShort(findColumn(column));
    }

    Short getShort(int column) throws SQLException;

    default Long getLong(String column) throws SQLException {
        return getLong(findColumn(column));
    }

    Long getLong(int column) throws SQLException;

    default Integer getInt(String column) throws SQLException {
        return getInt(findColumn(column));
    }

    Integer getInt(int column) throws SQLException;

    default Float getFloat(String column) throws SQLException {
        return getFloat(findColumn(column));
    }

    Float getFloat(int column) throws SQLException;

    default Double getDouble(String column) throws SQLException {
        return getDouble(findColumn(column));
    }

    Double getDouble(int column) throws SQLException;

    default BigDecimal getDecimal(String column) throws SQLException {
        return getDecimal(findColumn(column));
    }

    BigDecimal getDecimal(int column) throws SQLException;

    default String getString(String column) throws SQLException {
        return getString(findColumn(column));
    }

    String getString(int column) throws SQLException;

    default byte[] getBlob(String column) throws SQLException {
        return getBlob(findColumn(column));
    }

    byte[] getBlob(int column) throws SQLException;

    default Long getTimestamp(String column) throws SQLException {
        return getTimestamp(findColumn(column));
    }

    Long getTimestamp(int column) throws SQLException;

    default Boolean getBoolean(String column) throws SQLException {
        return getBoolean(findColumn(column));
    }

    Boolean getBoolean(int column) throws SQLException;

    default Object getObject(String column) throws SQLException {
        return getObject(findColumn(column));
    }

    Object getObject(int column) throws SQLException;

    MySQLResultMetadata getMetadata() throws SQLException;

    int getCurrentRowNum();

    @Override
    void close();
}
