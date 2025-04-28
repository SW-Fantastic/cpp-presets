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
import java.nio.charset.StandardCharsets;
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

    private CLongPointer[] lengths;

    private BytePointer[] errors;

    private BytePointer[] isNulls;

    private Pointer[] buf;

    private String zoneId;

    public MySQLPreparedResult(MYSQL_RES res, MYSQL_STMT stmt,String timeZoneId) {
        this.res = res;
        this.stmt = stmt;
        this.metadata = new MySQLResultMetadata(res);
        this.zoneId = timeZoneId;

        this.bindAndBuffer = new MYSQL_BIND(Pointer.malloc(
                (long) Pointer.sizeof(MYSQL_BIND.class) * res.field_count()
        ));

        lengths = new CLongPointer[res.field_count()];
        errors = new BytePointer[res.field_count()];
        isNulls = new BytePointer[res.field_count()];
        buf = new Pointer[res.field_count()];

        for (int i = 0; i < res.field_count(); i++) {

            MYSQL_FIELD field = MariaDB.mysql_fetch_field_direct(res,i);

            lengths[i] = new CLongPointer(Pointer.malloc(
                    Pointer.sizeof(CLongPointer.class)
            ));
            Pointer.memset(lengths[i],0,Pointer.sizeof(CLongPointer.class));

            errors[i] = new BytePointer(Pointer.malloc(
                    Pointer.sizeof(BytePointer.class)
            ));
            Pointer.memset(errors[i],0,Pointer.sizeof(BytePointer.class));

            isNulls[i] = new BytePointer(Pointer.malloc(
                    Pointer.sizeof(BytePointer.class)
            ));
            Pointer.memset(isNulls[i],0,Pointer.sizeof(BytePointer.class));

            int length = 4;
            if (accept(field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_INT24,
                    MyCom.enum_field_types.MYSQL_TYPE_LONG,
                    MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                    MyCom.enum_field_types.MYSQL_TYPE_TINY
            )) {
                length = Pointer.sizeof(IntPointer.class);
            } else if (accept(field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_LONGLONG,
                    MyCom.enum_field_types.MYSQL_TYPE_LONG
            )) {
                length = Pointer.sizeof(LongPointer.class);
            } else if (accept(
                    field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_DATE,
                    MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                    MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                    MyCom.enum_field_types.MYSQL_TYPE_NEWDATE,
                    MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                    MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
            )) {
                length = Pointer.sizeof(MYSQL_TIME.class);
            } else if (accept(
                    field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                    MyCom.enum_field_types.MYSQL_TYPE_STRING,
                    MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                    MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                    MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                    MyCom.enum_field_types.MYSQL_TYPE_BLOB
            )) {
                length = Pointer.sizeof(BytePointer.class);
            }

            buf[i] = Pointer.malloc(length);

            MYSQL_BIND bind = bindAndBuffer.getPointer(i);
            bind.buffer_type(field.type());
            bind.buffer_length(length);
            bind.buffer(buf[i]);
            bind.error(errors[i]);
            bind.length(lengths[i]);
            bind.is_null(isNulls[i]);

        }

        int state = MariaDB.mysql_stmt_bind_result(stmt,bindAndBuffer);
        if (state != 0) {
            for (int i = 0; i < metadata.getFieldCount(); i++) {
                MYSQL_BIND bind = bindAndBuffer.getPointer(i);
                bind.buffer().close();
                bind.close();
                errors[i].close();
                lengths[i].close();
                isNulls[i].close();
            }
            MariaDB.mysql_free_result(res);
            throw new RuntimeException("failed to bind result , errno : " + MariaDB.mysql_stmt_errno(stmt));
        }

        state = MariaDB.mysql_stmt_store_result(stmt);
        if (state != 0) {
            for (int i = 0; i < metadata.getFieldCount(); i++) {
                MYSQL_BIND bind = bindAndBuffer.getPointer(i);
                bind.buffer().close();
                bind.close();
                errors[i].close();
                lengths[i].close();
                isNulls[i].close();
            }
            MariaDB.mysql_free_result(res);
            throw new RuntimeException("failed to store a result set , errno : " + MariaDB.mysql_stmt_errno(stmt));
        }


    }

    @Override
    public boolean next() throws SQLException {
        return seek(currentRowNum + 1);
    }

    @Override
    public boolean previous() throws SQLException {
        if (currentRowNum <= 0) {
            return false;
        }
        return seek(currentRowNum - 1);
    }

    @Override
    public boolean seek(long rowNum) throws SQLException {
        if (rowNum > MariaDB.mysql_stmt_num_rows(stmt) + 1) {
            // seek to after last
            currentRowNum =  MariaDB.mysql_stmt_num_rows(stmt) + 1;
            return false;
        } else if (rowNum <= -1) {
            // seek to before first
            currentRowNum = -1;
            return false;
        }
        // seek to normal position
        MariaDB.mysql_stmt_data_seek(stmt,rowNum);
        currentRowNum = rowNum;

        int rst = MariaDB.mysql_stmt_fetch(stmt);
        if (rst != 0 && (rst != MariaDB.MYSQL_DATA_TRUNCATED)) {
            // MYSQL_DATA_TRUNCATE直接无视，通过Mysql传入Length的长度申请buffer
            // 并且提取数据。
            return false;
        }
        //System.err.println("Fetch called.");

        for (int i = 0; i < res.field_count(); i++) {

            // 绑定的缓冲区
            MYSQL_BIND bind = bindAndBuffer.getPointer(i);
            // 需要的buffer长度
            long bufLength = lengths[i].get();

            if (bufLength > 0) {
                // 不为空，且长度不为0，可以读取数据
                // 准备buffer
                if (buf[i] != null && !buf[i].isNull()) {
                    buf[i].close();
                }
                // 申请内存。
                this.buf[i] = Pointer.malloc(bufLength);
                Pointer.memset(buf[i],0,bufLength);
                bind.buffer(buf[i]);
                bind.buffer_length(bufLength);
                // 初始化Buffer
                // 读取该字段数据。
                MariaDB.mysql_stmt_fetch_column(stmt,bind,i,0);
            } else {
                if (buf[i] != null && !buf[i].isNull()) {
                    buf[i].close();
                }
                buf[i] = null;
                bind.buffer(new Pointer());
                bind.buffer_length(0);
            }

        }

        return true;
    }

    @Override
    public boolean isNull(int col) {
        return isNulls[col].get() == 1;
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
    public void firstRow() throws SQLException {
        seek(0);
    }

    @Override
    public void lastRow() throws SQLException {
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
        if (isNull(column)) {
            return null;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {

            if (lengths[column].get() <= 0) {
                return null;
            }

            MYSQL_TIME time = new MYSQL_TIME(buf[column]);
            LocalDate localDate = LocalDate.of(time.year(),time.month(),time.day());
            return Date.valueOf(localDate);
        }

        return null;
    }

    @Override
    public Time getTime(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return null;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATE,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2,
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            if (lengths[column].get() <= 0) {
                return null;
            }
            MYSQL_TIME time = new MYSQL_TIME(buf[column]);
            LocalTime localTime = LocalTime.of(time.hour(),time.minute(),time.second());
            return Time.valueOf(localTime);
        }
        return null;
    }

    @Override
    public Byte getByte(int column) throws SQLException {
        if (isNull(column)) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            if (lengths[column].get() <= 0) {
                return 0;
            }
            return new BytePointer(buf[column]).get();
        }
        return 0;
    }

    @Override
    public Short getShort(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return 0;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_SHORT
        )) {
            if (lengths[column].get() <= 0) {
                return 0;
            }
            return new ShortPointer(buf[column]).get();
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
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return 0L;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONGLONG
        )) {
            if (lengths[column].get() <= 0) {
                return 0L;
            }
            return new LongPointer(buf[column]).get();
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG,
                MyCom.enum_field_types.MYSQL_TYPE_SHORT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {

            Integer val = getInt(column);
            return val != null ? Long.valueOf(val) : 0;
        }

        return 0L;
    }

    @Override
    public Integer getInt(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return 0;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24,
                MyCom.enum_field_types.MYSQL_TYPE_LONG
        )) {
            if (lengths[column].get() <= 0) {
                return 0;
            }
            return new IntPointer(buf[column]).get();
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
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return 0F;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_FLOAT
        )) {
            if (lengths[column].get() <= 0) {
                return 0f;
            }
            return new FloatPointer(buf[column]).get();
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
            return val == null ? null : val.floatValue();
        }
        return 0f;
    }

    @Override
    public Double getDouble(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return 0d;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DOUBLE
        )) {
            if (lengths[column].get() <= 0) {
                return 0d;
            }

            return new DoublePointer(buf[column]).get();
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
        if (isNull(column)) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL
        )) {
            if (lengths[column].get() <= 0) {
                return null;
            }
            BytePointer pData = new BytePointer(buf[column]);
            if (pData.isNull()) {
                return null;
            }
            return new BigDecimal(pData.getString());
        }

        return null;
    }

    @Override
    public String getString(int column) throws SQLException {
        if (isNull(column)) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
        )) {
            if (lengths[column].get() <= 0) {
                return null;
            }
            byte[] data = new byte[(int)lengths[column].get()];
            BytePointer pointer = new BytePointer(buf[column]);
            pointer.get(data);
            return new String(data,StandardCharsets.UTF_8);
        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            byte[] data = getBlob(column);
            if (data != null) {
                return new String(data,StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    @Override
    public byte[] getBlob(int column) throws SQLException {
        if (isNull(column)) {
            return null;
        }
        MYSQL_FIELD field = metadata.getField(column);
        if (accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_BLOB)) {
            if (lengths[column].get() <= 0) {
                return null;
            }
            byte[] data = new byte[(int)lengths[column].get()];
            BytePointer pointer = new BytePointer(buf[column]);
            pointer.get(data);
            return data;
        }
        return null;
    }

    @Override
    public Long getTimestamp(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return null;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
        )) {

            if (lengths[column].get() <= 0) {
                return null;
            }
            return new LongPointer(buf[column]).get();

        } else if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {

            if (lengths[column].get() <= 0) {
                return null;
            }
            MYSQL_TIME time = new MYSQL_TIME(buf[column]);
            LocalDateTime dateTime = LocalDateTime.of(
                    time.year(),time.month() ,time.day(),time.hour(),time.minute(),time.second()
            );
            return dateTime.atOffset(ZoneOffset.UTC).toEpochSecond();
        }
        return null;
    }

    @Override
    public Boolean getBoolean(int column) throws SQLException {
        MYSQL_FIELD field = metadata.getField(column);
        if (isNull(column)) {
            return false;
        }
        if (accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_BIT,
                MyCom.enum_field_types.MYSQL_TYPE_TINY
        )) {

            if (lengths[column].get() <= 0) {
                return false;
            }

            return new BooleanPointer(
                    buf[column]
            ).get();
        }
        return false;
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
        bindAndBuffer = null;

        MariaDB.mysql_free_result(res);
        res = null;

        MariaDB.mysql_stmt_free_result(stmt);
        stmt = null;

    }
}
