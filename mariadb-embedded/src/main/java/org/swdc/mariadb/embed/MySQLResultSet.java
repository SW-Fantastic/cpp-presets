package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.global.MYSQL_TIME;
import org.swdc.mariadb.core.mysql.MYSQL_FIELD;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * MySQL的结果集，用于从MySQL读取返回的数据。
 */
public class MySQLResultSet implements Closeable {

    /**
     * MySQL的结果集的本地对象。
     */
    private MYSQL_RES res;


    /**
     * 当前行
     */
    private PointerPointer currentRow;

    /**
     * 当前行的各字段长度指针
     */
    private CLongPointer currentRowLength;

    /**
     * 当前行号
     */
    private long currentRowNum = -1;


    private MySQLResultMetadata metadata;


    public MySQLResultSet( MYSQL_RES res) {

        this.res = res;
        this.metadata = new MySQLResultMetadata(res);

    }

    public boolean next() {
        return seek(currentRowNum + 1);
    }

    public boolean previous() {
        if (currentRowNum <= 0) {
            return false;
        }
        return seek(currentRowNum - 1);
    }

    public boolean seek(long rowNum) {
        if (rowNum > MariaDB.mysql_num_rows(res) + 1) {
            // seek to after last
            currentRow = null;
            currentRowLength = null;
            currentRowNum =  MariaDB.mysql_num_rows(res) + 1;
            return true;

        } else if (rowNum <= -1) {
            // seek to before first
            currentRow = null;
            currentRowLength = null;
            currentRowNum = -1;
            return true;

        }
        // seek to normal position
        MariaDB.mysql_data_seek(res,rowNum);
        currentRowNum = rowNum;
        currentRow = MariaDB.mysql_fetch_row(res);
        currentRowLength = MariaDB.mysql_fetch_lengths(res);
        if (currentRow == null || currentRow.isNull()) {
            return false;
        }
        if (currentRowLength == null || currentRowLength.isNull()) {
            return false;
        }
        return true;
    }

    public boolean beforeFirst() {
        currentRowNum = -1;
        this.currentRow = null;
        this.currentRowLength = null;
        return true;
    }

    public boolean afterLast() {
        currentRowNum = MariaDB.mysql_num_rows(res) + 1;
        this.currentRow = null;
        this.currentRowLength = null;
        return true;
    }

    public boolean isBeforeFirst() {
        return currentRowNum == -1;
    }

    public boolean isAfterLast() {
        return currentRowNum > MariaDB.mysql_num_rows(res);
    }

    public void firstRow() {
        seek(0);
    }

    public void lastRow() {
        seek(MariaDB.mysql_num_rows(res) - 1);
    }

    public boolean isFirst() {
        return currentRowNum == 0;
    }

    public boolean isLast() {
        return currentRowNum == MariaDB.mysql_num_rows(res);
    }

    public int findColumn(String label) throws SQLException {
        // jdbc的column从1开始，这里额外加一。
        return metadata.findField(label) + 1;
    }

    public Date getDate(String column) throws SQLException {
        return getDate(metadata.findField(column));
    }

    public Date getDate(int column) throws SQLException {

        if (currentRow == null || currentRow.isNull()) {
            return null;
        }

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {
            MYSQL_TIME time = new MYSQL_TIME(currentRow.get(column));
            LocalDate localDate = LocalDate.of(time.year(),time.month(),time.day());
            return Date.valueOf(localDate);
        }

        return null;
    }

    public Time getTime(String column) throws SQLException {
        return getTime(metadata.findField(column));
    }

    public Time getTime(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            MYSQL_TIME time = new MYSQL_TIME(currentRow.get(column));
            LocalTime localTime = LocalTime.of(time.hour(),time.minute(),time.second());
            return Time.valueOf(localTime);
        }
        return null;
    }

    public Byte getByte(String column) throws SQLException {
        return getByte(metadata.findField(column));
    }

    public Byte getByte(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            return new BytePointer(currentRow.get(column)).get();
        }
        return null;
    }

    public Short getShort(String column) throws SQLException {
        return getShort(metadata.findField(column));
    }

    public Short getShort(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            return new ShortPointer(currentRow.get(column)).get();
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Byte val = getByte(column);
            return val != null ? Short.valueOf(val) : null;
        }
        return null;
    }

    public Long getLong(String column) throws SQLException {
        return getLong(metadata.findField(column));
    }

    public Long getLong(int column) throws SQLException {

        if (currentRow == null || currentRow.isNull()) {
            return null;
        }

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            return new LongPointer(currentRow.get(column)).get();
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {

            Integer val = getInt(column);
            return val != null ? Long.valueOf(val) : null;
        }

        return null;
    }

    public Integer getInt(String column) throws SQLException {
        return getInt(metadata.findField(column));
    }

    public Integer getInt(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG
        )) {
            return new IntPointer(currentRow.get(column)).get();
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            Short val = getShort(column);
            return val != null ? Integer.valueOf(val) : null;
        }
        return null;
    }

    public Float getFloat(String column) throws SQLException {
        return getFloat(metadata.findField(column));
    }

    public Float getFloat(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_FLOAT
        )) {
            return new FloatPointer(currentRow).get(column);
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Integer val = getInt(column);
            return val != null ? Float.valueOf(val) : null;
        }
        return null;
    }

    public Double getDouble(String column) throws SQLException {
        return getDouble(metadata.findField(column));
    }

    public Double getDouble(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            return new DoublePointer(currentRow.get(column)).get();
        } else if (accept(field.type(),MyCom.enum_field_types.MYSQL_TYPE_FLOAT)) {
            Float val = getFloat(column);
            return val != null ? Double.valueOf(val) : null;
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Integer val = getInt(column);
            return val != null ? Double.valueOf(val) : null;
        }
        return null;
    }

    public BigDecimal getDecimal(String column) throws SQLException {
        return getDecimal(metadata.findField(column));
    }

    public BigDecimal getDecimal(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL
        )) {
            BytePointer pData = new BytePointer(currentRow.get(column));
            if (pData.isNull()) {
                return null;
            }
            return new BigDecimal(pData.getString());
        }

        return null;
    }

    public String getString(String column) throws SQLException {
        return getString(metadata.findField(column));
    }

    public String getString(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(field.type(),MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING)) {
            byte[] data = new byte[(int)currentRowLength.get(column)];
            BytePointer pointer = new BytePointer(currentRow.get(column));
            pointer.get(data);
            return new String(data);
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
        )) {
            return new BytePointer(currentRow).getString();
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            byte[] data = getBlob(column);
            if (data != null) {
                return new String(data);
            }
        }
        return null;
    }

    public byte[] getBlob(String column) throws SQLException {
        return getBlob(metadata.findField(column));
    }

    public byte[] getBlob(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_BLOB)) {
            byte[] data = new byte[(int)currentRowLength.get(column)];
            BytePointer pointer = new BytePointer(currentRow.get(column));
            pointer.get(data);
            return data;
        }
        return null;
    }

    public Long getTimestamp(String column) throws SQLException {
        return getTimestamp(metadata.findField(column));
    }

    public Long getTimestamp(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
        )) {

            return new LongPointer(currentRow.get(column)).get();

        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {

            MYSQL_TIME time = new MYSQL_TIME(currentRow.get(column));
            LocalDateTime dateTime = LocalDateTime.of(
                    time.year(),time.month(),time.day(),time.hour(),time.minute(),time.second()
            );
            ZoneOffset offset = ZoneOffset.UTC;

            return dateTime.atOffset(offset).toEpochSecond();
        }
        return null;
    }

    public Boolean getBoolean(String column) throws SQLException {
        return getBoolean(metadata.findField(column));
    }

    public Boolean getBoolean(int column) throws SQLException {
        if (currentRow == null || currentRow.isNull()) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BIT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            return new BooleanPointer(
                    currentRow.get(column)
            ).get();
        }
        return null;
    }

    /**
     * 判断指定的field的类型是否在提供的field类型列表中。
     * @param type field类型
     * @param types filed类型列表
     * @return 在列表中时返回true
     */
    public static boolean accept(MyCom.enum_field_types type, MyCom.enum_field_types... types) {
        for (MyCom.enum_field_types item : types) {
            if (item.value == type.value) {
                return true;
            }
        }
        return false;
    }

    public MySQLResultMetadata getMetadata() {
        return metadata;
    }

    public int getCurrentRowNum() {
        return (int) currentRowNum;
    }

    @Override
    public void close() {

        MariaDB.mysql_free_result(res);
        currentRow = null;
        res = null;

    }
}
