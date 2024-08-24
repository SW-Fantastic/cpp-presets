package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.global.MYSQL_TIME;
import org.swdc.mariadb.core.mysql.*;
import org.swdc.mariadb.embed.jdbc.results.MyQueryResult;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public void setNull(int index) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        buf[index] = null;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_length(0);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_NULL);


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


    public void setObject(int index, Object obj) throws SQLException {
        if (obj.getClass() == int.class || obj.getClass() == Integer.class) {
            setInt(index,(int)obj);
        } else if (obj.getClass() == short.class || obj.getClass() == Short.class) {
            setShort(index,(short)obj);
        } else if (obj.getClass() == float.class || obj.getClass() == Float.class) {
            setFloat(index,(float) obj);
        } else if (obj.getClass() == long.class || obj.getClass() == Long.class) {
            setLong(index,(long) obj);
        } else if (obj.getClass() == Timestamp.class) {
            setTimestamp(index,(Timestamp) obj);
        } else if (obj.getClass() == Date.class) {
            setDate(index,(Date) obj);
        } else if (obj.getClass() == Time.class) {
            setTime(index,(Time) obj);
        } else if (obj.getClass() == LocalDateTime.class) {
            setTimestamp(index,Timestamp.valueOf((LocalDateTime) obj));
        } else if (obj.getClass() == BigDecimal.class) {
            setDecimal(index,(BigDecimal) obj);
        } else if (obj.getClass() == boolean.class || obj.getClass() == Boolean.class) {
            setBoolean(index,(Boolean)obj);
        } else if (obj.getClass() == byte[].class) {
            setBytes(index,(byte[]) obj);
        } else if (obj.getClass() == String.class) {
            setString(index,(String) obj);
        } else if (obj instanceof Blob) {
            try {
                Blob blob = (Blob) obj;
                InputStream is = blob.getBinaryStream();
                byte[] data = is.readAllBytes();
                setBytes(index,data);
                is.close();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            Reader reader = clob.getCharacterStream();
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                char[] buf = new char[1024 * 1024];
                int len = -1;
                while ((len = reader.read(buf)) != -1) {
                    byte[] bytes = String.valueOf(buf,0,len).getBytes();
                    bos.write(bytes);
                }
                setString(index, bos.toString());
                reader.close();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        } else {
            throw new SQLException("un support java type: "  + obj.getClass());
        }
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


    public long executeUpdate() throws SQLException {

        validate();

        if(MariaDB.mysql_stmt_reset(stmt) != 0) {
            throw new SQLException("failed to complete operation.");
        }

        int state = MariaDB.mysql_stmt_execute(stmt);
        if (state != 0) {
            throw new SQLException("failed to execute this query : " + sql + " with errno : " + MariaDB.mysql_stmt_errno(stmt));
        }


        return MariaDB.mysql_stmt_affected_rows(stmt);

    }

    public MySQLPreparedResult execute() throws SQLException {

        validate();

        if(MariaDB.mysql_stmt_reset(stmt) != 0) {
            throw new SQLException("failed to complete operation.");
        }

        int state = MariaDB.mysql_stmt_bind_param(stmt,binds);
        if (state != 0) {
            throw new SQLException("failed to bind parameter : " + sql + " with errno : " + MariaDB.mysql_stmt_errno(stmt));
        }

        state = MariaDB.mysql_stmt_execute(stmt);
        if (state != 0) {
            throw new SQLException("failed to execute this query : " + sql + " with errno : " + MariaDB.mysql_stmt_errno(stmt) +
                    "\n caused by " + MariaDB.mysql_stmt_error(stmt).getString());
        }

        MYSQL_RES res = MariaDB.mysql_stmt_result_metadata(stmt);
        if (res == null || res.isNull()) {
            return null;
        }
        state = MariaDB.mysql_stmt_store_result(stmt);
        if (state != 0) {
            MariaDB.mysql_free_result(res);
            throw new SQLException("failed to store a result set , errno : " + MariaDB.mysql_stmt_errno(stmt));
        }


        int count = res.field_count();
        MYSQL_BIND binds = new MYSQL_BIND(Pointer.malloc(
                (long) Pointer.sizeof(MYSQL_BIND.class) * count
        ));
        CLongPointer lengths = new CLongPointer(Pointer.malloc(
                (long) Pointer.sizeof(CLongPointer.class) * count
        ));
        for (int i = 0; i < count; i++) {
            MYSQL_FIELD field = MariaDB.mysql_fetch_field_direct(res,i);
            MYSQL_BIND bind = binds.getPointer(i);
            bind.buffer_type(field.type());
            bind.buffer_length(0);
            bind.length(lengths.getPointer(i));
        }

        state = MariaDB.mysql_stmt_bind_result(stmt,binds);
        if (state != 0) {
            for (int i = 0; i < count; i++) {
                MYSQL_BIND bind = binds.getPointer(i);
                bind.buffer().close();
                bind.close();
            }
            MariaDB.mysql_free_result(res);
            throw new SQLException("failed to bind result , errno : " + MariaDB.mysql_stmt_errno(stmt));
        }

        return new MySQLPreparedResult(res,stmt,binds,lengths);

    }

    public synchronized long[] executeBatch() throws SQLException {

        BatchItem saved = new BatchItem();
        saved.binds = this.binds;
        saved.buf = this.buf;

        long[] effectRows = new long[batches.size()];

        ListIterator<BatchItem> iter = batches.listIterator();
        while (iter.hasNext()) {

            int idx = iter.nextIndex();
            BatchItem item = iter.next();
            this.binds = item.binds;
            this.buf = item.buf;

            try {

                effectRows[idx] = this.executeUpdate();
                for (int colIdx = 0; colIdx < stmt.param_count(); colIdx ++) {
                    this.binds.getPointer(colIdx).close();
                    this.buf[colIdx].close();
                }

                iter.remove();

            } finally {
                this.binds = saved.binds;
                this.buf = saved.buf;
            }

        }

        this.binds = saved.binds;
        this.buf = saved.buf;

        return effectRows;

    }



}
