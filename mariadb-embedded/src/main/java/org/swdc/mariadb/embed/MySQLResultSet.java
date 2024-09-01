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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.swdc.mariadb.embed.IMySQLResultSet.accept;

/**
 * MySQL的结果集，用于从MySQL读取返回的数据。
 */
public class MySQLResultSet implements IMySQLResultSet,CloseableSource {

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

    private CloseableListener closeableListener;


    public MySQLResultSet( MYSQL_RES res) {

        this.res = res;
        this.metadata = new MySQLResultMetadata(res);

    }

    public void validate() throws SQLException {
        if (res == null || res.isNull()) {
            throw new SQLException("result set has closed.");
        }
    }

    @Override
    public boolean next() throws SQLException {
        validate();
        return seek(currentRowNum + 1);
    }

    @Override
    public boolean previous() throws SQLException {
        validate();
        if (currentRowNum <= 0) {
            return false;
        }
        return seek(currentRowNum - 1);
    }

    @Override
    public boolean seek(long rowNum) throws SQLException {
        validate();
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

    @Override
    public boolean beforeFirst() throws SQLException {
        validate();
        currentRowNum = -1;
        this.currentRow = null;
        this.currentRowLength = null;
        return true;
    }

    @Override
    public boolean afterLast() throws SQLException {
        validate();
        currentRowNum = MariaDB.mysql_num_rows(res) + 1;
        this.currentRow = null;
        this.currentRowLength = null;
        return true;
    }

    @Override
    public boolean isBeforeFirst() {
        return currentRowNum == -1;
    }

    @Override
    public boolean isAfterLast() {
        return currentRowNum > MariaDB.mysql_num_rows(res);
    }

    @Override
    public void firstRow() throws SQLException {
        seek(0);
    }

    @Override
    public void lastRow() throws SQLException {
        seek(MariaDB.mysql_num_rows(res) - 1);
    }

    @Override
    public boolean isFirst() throws SQLException {
        validate();
        return currentRowNum == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        validate();
        return currentRowNum == MariaDB.mysql_num_rows(res);
    }

    @Override
    public int findColumn(String label) throws SQLException {
        validate();
        // jdbc的column从1开始，这里额外加一。
        return metadata.findField(label) + 1;
    }


    @Override
    public Date getDate(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {

            BytePointer pointer = new BytePointer(currentRow.get(column));
            if (pointer.isNull()) {
                return null;
            }

            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);

            String patternDateTime = "yyyy-MM-dd HH:mm:ss";
            String patternDate = "yyyy-MM-dd";
            String dateStr = new String(data);
            DateTimeFormatter formatter = null;
            if (dateStr.length() == patternDate.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDate);

            } else if (dateStr.length() == patternDateTime.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDateTime);
            } else {
                throw new SQLException("unsupport data time : "  + dateStr);
            }

            LocalDate localDate = LocalDate.parse(dateStr,formatter);
            return Date.valueOf(localDate);
        }

        return null;
    }

    @Override
    public Time getTime(int column) throws SQLException {
        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {

            BytePointer pointer = new BytePointer(currentRow.get(column));
            if (pointer.isNull()) {
                return null;
            }

            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);

            String patternDateTime = "yyyy-MM-dd HH:mm:ss";
            String patternDate = "yyyy-MM-dd";
            String dateStr = new String(data);
            DateTimeFormatter formatter = null;
            if (dateStr.length() == patternDate.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDate);

            } else if (dateStr.length() == patternDateTime.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDateTime);
            } else {
                throw new SQLException("unsupport data time : "  + dateStr);
            }

            LocalDateTime localDateTime = LocalDateTime.parse(dateStr,formatter);
            return Time.valueOf(localDateTime.toLocalTime());
        }
        return null;
    }

    @Override
    public Byte getByte(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB
        )) {
            return new BytePointer(currentRow.get(column)).get();
        }
        return null;
    }


    @Override
    public Short getShort(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            BytePointer pointer = new BytePointer(currentRow.get(column));
            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);
            return Short.parseShort(new String(data));
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Byte val = getByte(column);
            return val != null ? Short.valueOf(val) : 0;
        }
        return 0;
    }



    @Override
    public Long getLong(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            BytePointer pointer = new BytePointer(currentRow.get(column));
            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);
            return Long.parseLong(new String(data));
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {

            Integer val = getInt(column);
            return val != null ? Long.valueOf(val) : 0l;
        }

        return 0l;
    }



    @Override
    public Integer getInt(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            if (currentRow.get(column) == null) {
                return 0;
            }
            BytePointer pointer = new BytePointer(currentRow.get(column));
            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);
            return Integer.parseInt(new String(data));
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            Short val = getShort(column);
            return val != null ? Integer.valueOf(val) : 0;
        }
        return 0;
    }


    @Override
    public Float getFloat(int column) throws SQLException {


        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_FLOAT
        )) {
            BytePointer pointer = new BytePointer(currentRow.get(column));
            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);
            return Float.parseFloat(new String(data));
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Integer val = getInt(column);
            return val != null ? Float.valueOf(val) : 0f;
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            Double val = getDouble(column);
            if (val != null) {
                return val.floatValue();
            }
        }
        return 0f;
    }


    @Override
    public Double getDouble(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            BytePointer pointer = new BytePointer(currentRow.get(column));
            byte[] data = new byte[(int) currentRowLength.getPointer(column).get()];
            pointer.get(data);
            return Double.parseDouble(new String(data));
        } else if (accept(field.type(),MyCom.enum_field_types.MYSQL_TYPE_FLOAT)) {
            Float val = getFloat(column);
            return val != null ? Double.valueOf(val) : 0d;
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            Integer val = getInt(column);
            return val != null ? Double.valueOf(val) : 0d;
        }
        return 0d;
    }


    @Override
    public BigDecimal getDecimal(int column) throws SQLException {

        validate();

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

        return new BigDecimal(0);
    }

    @Override
    public String getString(int column) throws SQLException {

        validate();

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
                MyCom.enum_field_types.MYSQL_TYPE_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB
        )) {
            byte[] data = getBlob(column);
            if (data != null) {
                return new String(data);
            }
        }
        return "";
    }


    @Override
    public byte[] getBlob(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB
        )) {
            byte[] data = new byte[(int)currentRowLength.get(column)];
            BytePointer pointer = new BytePointer(currentRow.get(column));
            pointer.get(data);
            return data;
        }
        return new byte[0];
    }


    @Override
    public Long getTimestamp(int column) throws SQLException {

        validate();

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

            byte[] data = new byte[(int)currentRowLength.get(column)];
            BytePointer pointer = new BytePointer(currentRow.get(column));
            pointer.get(data);

            String patternDateTime = "yyyy-MM-dd HH:mm:ss";
            String patternDate = "yyyy-MM-dd";
            String dateStr = new String(data);
            DateTimeFormatter formatter = null;
            if (dateStr.length() == patternDate.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDate);

            } else if (dateStr.length() == patternDateTime.length()) {
                formatter = DateTimeFormatter.ofPattern(patternDateTime);
            } else {
                throw new SQLException("unsupport data time : "  + dateStr);
            }

            LocalDateTime time = LocalDateTime.parse(dateStr,formatter);
            ZoneOffset offset = ZoneOffset.UTC;

            return time.atOffset(offset).toEpochSecond();
        }
        return null;
    }


    @Override
    public Boolean getBoolean(int column) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BIT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            if (currentRow.get(column).isNull()) {
                return false;
            }
            return new BooleanPointer(
                    currentRow.get(column)
            ).get();
        }
        return false;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {

        validate();

        MYSQL_FIELD field = metadata.getField(columnIndex);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            return getTime(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDATE
        )) {
            return getDate(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
        )) {
            return getTimestamp(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BIT
        )) {
            return getBoolean(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {
            return getShort(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG
        )) {
            return getInt(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_STRING
        )) {
            return getString(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB
        )) {
            if(field.charsetnr() != 63) {
                return getString(columnIndex);
            }
            return getBlob(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            return getLong(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_FLOAT
        )) {
            return getFloat(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            return getDouble(columnIndex);
        }

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL
        )) {
            return getDecimal(columnIndex);
        }

        return null;
    }

    @Override
    public MySQLResultMetadata getMetadata() throws SQLException {
        validate();
        return metadata;
    }

    @Override
    public int getCurrentRowNum() {
        return (int) currentRowNum;
    }

    @Override
    public synchronized void close() {

       if(closeBySource()) {
           if (closeableListener != null) {
               closeableListener.closed(this);
           }
       }

    }

    @Override
    public void setCloseListener(CloseableListener listener) {
        this.closeableListener = listener;
    }

    @Override
    public synchronized boolean closeBySource() {
        if (res == null || res.isNull()) {
            return false;
        }

        MariaDB.mysql_free_result(res);
        currentRow = null;
        res = null;
        return true;
    }
}
