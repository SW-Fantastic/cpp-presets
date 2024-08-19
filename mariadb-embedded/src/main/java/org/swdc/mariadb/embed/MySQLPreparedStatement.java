package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.global.MYSQL_TIME;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_BIND;
import org.swdc.mariadb.core.mysql.MYSQL_STMT;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLPreparedStatement extends MySQLStatement {

    static class BatchItem {

        MYSQL_BIND binds;

        Pointer[] buf;

    }

    private MYSQL_STMT stmt;

    private String sql;

    private List<BatchItem> batches = new ArrayList<>();

    private MYSQL_BIND binds;

    private Pointer[] buf;

    protected MySQLPreparedStatement(MYSQL mysqlConnection, String sql) {
        super(mysqlConnection);
        this.sql = sql;
        this.stmt = MariaDB.mysql_stmt_init(mysqlConnection);
        if( stmt == null || stmt.isNull() || MariaDB.mysql_stmt_prepare(stmt,sql,sql.length()) != 0) {
            throw new RuntimeException(
                    new SQLException("Can not prepare statement - errno :" + MariaDB.mysql_errno(mysqlConnection))
            );
        }

        this.binds = new MYSQL_BIND(stmt.param_count());
        this.buf = new Pointer[stmt.param_count()];
    }

    private void validate() {
        if (stmt == null || stmt.isNull() || binds == null || binds.isNull()) {
            throw new RuntimeException("statement has closed.");
        }
    }

    public void setInt(int index, int x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        IntPointer p = new IntPointer(
                Pointer.malloc(Pointer.sizeof(IntPointer.class))
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_LONG);
        bind.buffer_length(Pointer.sizeof(IntPointer.class));
        bind.buffer(p);

    }

    public void setString(int index, String str) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int size = str.getBytes(StandardCharsets.UTF_8).length;
        BytePointer p = new BytePointer(
                Pointer.malloc(size)
        );
        p.putString(str);
        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING);
        bind.buffer_length(size);
        bind.buffer(p);

    }


    public void setBoolean(int index, boolean x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(BoolPointer.class);
        BoolPointer p = new BoolPointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BIT);
        bind.buffer_length(leng);
        bind.buffer(p);
    }


    public void setByte(int index, byte x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(BytePointer.class);
        BytePointer p = new BytePointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        bind.buffer_length(leng);
        bind.buffer(p);
    }


    public void setShort(int index, short x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(ShortPointer.class);
        ShortPointer p = new ShortPointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_SHORT);
        bind.buffer_length(leng);
        bind.buffer(p);
    }

    public void setLong(int index, long x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(LongPointer.class);
        LongPointer p = new LongPointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_LONGLONG);
        bind.buffer_length(leng);
        bind.buffer(p);

    }


    public void setFloat(int index, float x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(FloatPointer.class);
        FloatPointer p = new FloatPointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_FLOAT);
        bind.buffer_length(leng);
        bind.buffer(p);

    }


    public void setDouble(int index, double x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(DoublePointer.class);
        DoublePointer p = new DoublePointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DOUBLE);
        bind.buffer_length(leng);
        bind.buffer(p);

    }

    public void setDecimal(int index, BigDecimal decimal) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        String str = decimal.stripTrailingZeros().toPlainString();
        int size = str.getBytes(StandardCharsets.UTF_8).length;
        BytePointer p = new BytePointer(
                Pointer.malloc(size)
        );
        p.putString(str);
        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DECIMAL);
        bind.buffer_length(size);
        bind.buffer(p);

    }

    public void setBytes(int index, byte[] x) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(BytePointer.class) * x.length;
        BytePointer p = new BytePointer(
                Pointer.malloc(leng)
        );
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        bind.buffer_length(leng);
        bind.buffer(p);

    }

    public void setDate(int index, Date date) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(MYSQL_TIME.class);
        MYSQL_TIME time = new MYSQL_TIME(
                Pointer.malloc(leng)
        );

        LocalDate localDate = date.toLocalDate();
        time.year(localDate.getYear());
        time.month(localDate.getMonth().getValue());
        time.day(localDate.getDayOfMonth());
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_DATE);

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DATE);
        bind.buffer_length(leng);
        bind.buffer(time);


    }

    public void setTime(int index, Time date) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(MYSQL_TIME.class);
        MYSQL_TIME time = new MYSQL_TIME(
                Pointer.malloc(leng)
        );

        LocalTime localDate = date.toLocalTime();
        time.hour(localDate.getHour());
        time.minute(localDate.getMinute());
        time.second(localDate.getSecond());
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_TIME);

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_TIME);
        bind.buffer_length(leng);
        bind.buffer(time);

    }

    public void setTimestamp(int index, Timestamp ts) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        int leng = Pointer.sizeof(MYSQL_TIME.class);
        MYSQL_TIME time = new MYSQL_TIME(
                Pointer.malloc(leng)
        );

        LocalDateTime localDate = ts.toLocalDateTime();

        time.year(localDate.getYear());
        time.month(localDate.getMonth().getValue());
        time.day(localDate.getDayOfMonth());

        time.hour(localDate.getHour());
        time.minute(localDate.getMinute());
        time.second(localDate.getSecond());
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_DATETIME);

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DATETIME);
        bind.buffer_length(leng);
        bind.buffer(time);


    }

    public void addBatch() {

        validate();

        BatchItem item = new BatchItem();
        item.buf = buf;
        item.binds = binds;
        batches.add(item);

        this.binds = new MYSQL_BIND(stmt.param_count());
        this.buf = new Pointer[stmt.param_count()];

    }


    public void clearParams() {

        validate();

        this.binds.close();
        this.binds = new MYSQL_BIND(stmt.param_count());

        for (int idx = 0; idx < buf.length; idx++) {
            Pointer p = buf[idx];
            p.close();
            buf[idx] = null;
        }

    }


}
