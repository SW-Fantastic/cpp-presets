package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.global.MYSQL_TIME;
import org.swdc.mariadb.core.mysql.MYSQL_BIND;
import org.swdc.mariadb.core.mysql.MYSQL_FIELD;
import org.swdc.mariadb.core.mysql.MYSQL_RES;
import org.swdc.mariadb.core.mysql.MYSQL_STMT;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static org.swdc.mariadb.embed.IMySQLResultSet.accept;

public class MySQLPreparedResult  implements IMySQLResultSet {

    private MYSQL_RES res;

    private MYSQL_STMT stmt;

    private MYSQL_BIND bindAndBuffer;

    /**
     * 当前行号
     */
    private long currentRowNum = -1;

    private MySQLResultMetadata metadata;

    private CLongPointer lengths;

    public MySQLPreparedResult(MYSQL_RES res, MYSQL_STMT stmt, MYSQL_BIND bindAndBuffer, CLongPointer lengths) {
        this.res = res;
        this.bindAndBuffer = bindAndBuffer;
        this.stmt = stmt;
        this.metadata = new MySQLResultMetadata(res);
    }

    @Override
    public boolean next() {
        return seek(currentRowNum + 1);
    }

    @Override
    public boolean previous() {
        if (currentRowNum <= 0) {
            return false;
        }
        return seek(currentRowNum - 1);
    }

    @Override
    public boolean seek(long rowNum) {
        if (rowNum > MariaDB.mysql_stmt_num_rows(stmt) + 1) {
            // seek to after last
            currentRowNum =  MariaDB.mysql_stmt_num_rows(stmt) + 1;
            return true;

        } else if (rowNum <= -1) {
            // seek to before first
            currentRowNum = -1;
            return true;

        }
        // seek to normal position
        MariaDB.mysql_stmt_data_seek(stmt,rowNum);
        currentRowNum = rowNum;

        for (int i = 0; i < res.field_count(); i++) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(i);
            if (bind.buffer() != null && !bind.buffer().isNull()) {
                bind.buffer().close();
            }
            bind.buffer(new Pointer());
            bind.buffer_length(0);
        }

        if(MariaDB.mysql_stmt_fetch(stmt) != 0) {
            return false;
        }

        for (int i = 0; i < res.field_count(); i++) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(i);
            if (bind.buffer() != null && !bind.buffer().isNull()) {
                bind.buffer().close();
            }
            if (lengths.get(i) > 0) {
                bind.buffer(Pointer.malloc(lengths.get(i)));
                bind.buffer_length(lengths.get(i));
                MariaDB.mysql_stmt_fetch_column(stmt,bind.getPointer(i),i,0);
            } else {
                bind.buffer(new Pointer());
                bind.buffer_length(0);
            }

        }

        return true;
    }

    @Override
    public boolean beforeFirst() {
        currentRowNum = -1;
        return true;
    }

    @Override
    public boolean afterLast() {
        currentRowNum = MariaDB.mysql_num_rows(res) + 1;
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
    public void firstRow() {
        seek(0);
    }

    @Override
    public void lastRow() {
        seek(MariaDB.mysql_num_rows(res) - 1);
    }

    @Override
    public boolean isFirst() {
        return currentRowNum == 0;
    }

    @Override
    public boolean isLast() {
        return currentRowNum == MariaDB.mysql_num_rows(res);
    }

    @Override
    public int findColumn(String label) throws SQLException {
        return metadata.findField(label);
    }

    @Override
    public Date getDate(int column) throws SQLException {

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {
            MYSQL_TIME time = new MYSQL_TIME(bindAndBuffer.getPointer(column).buffer());
            LocalDate localDate = LocalDate.of(time.year(),time.month(),time.day());
            return Date.valueOf(localDate);
        }

        return null;
    }

    @Override
    public Time getTime(int column) throws SQLException {

        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            MYSQL_TIME time = new MYSQL_TIME(bind.buffer());
            LocalTime localTime = LocalTime.of(time.hour(),time.minute(),time.second());
            return Time.valueOf(localTime);
        }
        return null;
    }

    @Override
    public Byte getByte(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new BytePointer(bind.buffer()).get();
        }
        return null;
    }

    @Override
    public Short getShort(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new ShortPointer(bind.buffer()).get();
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

    @Override
    public Long getLong(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new LongPointer(bind.buffer()).get();
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

    @Override
    public Integer getInt(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new IntPointer(bind.buffer()).get();
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

    @Override
    public Float getFloat(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_FLOAT
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new FloatPointer(bind.buffer()).get(column);
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

    @Override
    public Double getDouble(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }

            return new DoublePointer(bind.buffer()).get();
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

    @Override
    public BigDecimal getDecimal(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            BytePointer pData = new BytePointer(bind.buffer());
            if (pData.isNull()) {
                return null;
            }
            return new BigDecimal(pData.getString());
        }

        return null;
    }

    @Override
    public String getString(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(field.type(),MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING)) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }

            byte[] data = new byte[(int)lengths.get(column)];
            BytePointer pointer = new BytePointer(bind.buffer());
            pointer.get(data);
            return new String(data);
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
        )) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new BytePointer(bind.buffer()).getString();
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

    @Override
    public byte[] getBlob(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_BLOB)) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            byte[] data = new byte[(int)lengths.get(column)];
            BytePointer pointer = new BytePointer(bind.buffer());
            pointer.get(data);
            return data;
        }
        return null;
    }

    @Override
    public Long getTimestamp(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
        )) {

            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            return new LongPointer(bind.buffer()).get();

        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {

            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }
            MYSQL_TIME time = new MYSQL_TIME(bind.buffer());
            LocalDateTime dateTime = LocalDateTime.of(
                    time.year(),time.month(),time.day(),time.hour(),time.minute(),time.second()
            );
            ZoneOffset offset = ZoneOffset.UTC;

            return dateTime.atOffset(offset).toEpochSecond();
        }
        return null;
    }

    @Override
    public Boolean getBoolean(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);

        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BIT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {

            MYSQL_BIND bind = bindAndBuffer.getPointer(column);
            if (bind.buffer_length() <= 0) {
                return null;
            }

            return new BooleanPointer(
                    bind.buffer()
            ).get();
        }
        return null;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {

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
    public MySQLResultMetadata getMetadata() {
        return metadata;
    }

    @Override
    public int getCurrentRowNum() {
        return (int)currentRowNum;
    }

    @Override
    public void close() {

        for (int i = 0; i < res.field_count(); i++) {
            MYSQL_BIND bind = bindAndBuffer.getPointer(i);
            if (bind.buffer() != null && !bind.buffer().isNull()) {
                bind.buffer().close();
            }
        }

        Pointer.free(bindAndBuffer);
        Pointer.free(lengths);
        bindAndBuffer = null;
        lengths = null;

        MariaDB.mysql_free_result(res);
        res = null;

        MariaDB.mysql_stmt_free_result(stmt);
        stmt = null;

    }
}