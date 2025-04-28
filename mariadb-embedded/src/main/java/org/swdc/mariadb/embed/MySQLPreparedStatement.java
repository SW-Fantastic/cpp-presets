package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.MyGlobal;
import org.swdc.mariadb.core.global.MYSQL_TIME;
import org.swdc.mariadb.core.mysql.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class MySQLPreparedStatement extends MySQLStatement {

    static class BatchItem {

        MYSQL_BIND binds;

        Pointer[] buf;

        BytePointer nullFlags;

        CLongPointer lengths;

    }

    private MYSQL_STMT stmt;

    private String sql;

    private Deque<BatchItem> batches = new ArrayDeque<>();

    private MYSQL_BIND binds;

    private Pointer[] buf;

    private BytePointer nullFlags;

    private CLongPointer lengths;

    protected MySQLPreparedStatement(MYSQL mysqlConnection, String sql, String timeZoneId) {
        super(mysqlConnection,timeZoneId);
        this.sql = sql;
        this.stmt = MariaDB.mysql_stmt_init(mysqlConnection);
        if( stmt == null || stmt.isNull() || MariaDB.mysql_stmt_prepare(stmt,sql,sql.length()) != 0) {
            throw new RuntimeException(
                    new SQLException("Can not prepare statement - errno :" + MariaDB.mysql_errno(mysqlConnection) + "\n caused by " + MariaDB.mysql_stmt_error(stmt))
            );
        }

        long bindsSize = (long) Pointer.sizeof(MYSQL_BIND.class) * stmt.param_count();
        this.binds = new MYSQL_BIND(Pointer.malloc(bindsSize));
        Pointer.memset(binds,0,bindsSize);

        this.buf = new Pointer[stmt.param_count()];

        long nullSize =(long)Pointer.sizeof(BytePointer.class) * stmt.param_count();
        this.nullFlags = new BytePointer(Pointer.malloc(nullSize));
        Pointer.memset(this.nullFlags,0,nullSize);

        long lengthSize = (long) Pointer.sizeof(CLongPointer.class) * stmt.param_count();
        this.lengths = new CLongPointer(Pointer.malloc(lengthSize));
        Pointer.memset(lengths,0,lengthSize);
    }

    private void validate() {
        if (stmt == null || stmt.isNull() || binds == null || binds.isNull()) {
            throw new RuntimeException("statement has closed.");
        }
    }

    private void updateBind(MYSQL_BIND bind, int column, boolean nullVal, int length) {

        BytePointer nullFlag = nullFlags.getPointer(column);
        bind.is_null(nullFlag);
        nullFlag.put((byte) (nullVal ? 1 : 0));

        CLongPointer pLength = lengths.getPointer(column);
        pLength.put(length);
        bind.length(pLength);
        bind.buffer_length(length);

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

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_LONG);
        bind.buffer_length(Pointer.sizeof(IntPointer.class));
        bind.buffer(p);

        updateBind(bind,index,false,4);

    }

    public void setNull(int index) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        buf[index] = null;

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_length(0);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_NULL);
        updateBind(bind,index,true,0);

    }

    public void setString(int index, String str) throws SQLException {

        validate();

        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        byte [] strBytes = str.getBytes(StandardCharsets.UTF_8);
        int size = strBytes.length;
        BytePointer p = new BytePointer(
                Pointer.malloc(size + 4)
        );
        Pointer.memset(p,0,size);
        p.putString(str);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = binds.getPointer(index);
        if (size <= 65535) {
            bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        } else if (size <= 16777215) {
            bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB);
        } else {
            bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB);
        }
        bind.buffer_length(size);
        bind.buffer(p);

        updateBind(bind,index,false,size);
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

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        bind.buffer_length(leng);
        bind.buffer(p);

        updateBind(bind,index,false,leng);
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

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        bind.buffer_length(leng);
        bind.buffer(p);

        updateBind(bind,index,false,Pointer.sizeof(BytePointer.class));
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
        updateBind(bind,index,false,Pointer.sizeof(ShortPointer.class));
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

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_LONGLONG);
        bind.buffer_length(leng);
        bind.buffer(p);

        updateBind(bind,index,false,Pointer.sizeof(LongPointer.class));

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
        updateBind(bind,index,false,Pointer.sizeof(FloatPointer.class));

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

        updateBind(bind,index,false,Pointer.sizeof(DoublePointer.class));
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
        Pointer.memset(p,0,size);
        p.putString(str);
        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DECIMAL);
        bind.buffer_length(size);
        bind.buffer(p);

        updateBind(bind,index,false,size);
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
        Pointer.memset(p,0,leng);
        p.put(x);

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        buf[index] = p;

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_BLOB);
        bind.buffer_length(leng);
        bind.buffer(p);

        updateBind(bind,index,false,leng);
    }

    public void setDate(int index, Date date) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        int leng = Pointer.sizeof(MYSQL_TIME.class);
        Pointer p = Pointer.malloc(leng);
        Pointer.memset(p,0,leng);

        buf[index] = p;

        MYSQL_TIME time = new MYSQL_TIME(p);

        LocalDate localDate = date.toLocalDate();
        time.year(localDate.getYear());
        time.month(localDate.getMonth().getValue());
        time.day(localDate.getDayOfMonth());
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_DATE);

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DATE);
        bind.buffer_length(leng);
        bind.buffer(time);

        updateBind(bind,index,false,Pointer.sizeof(MYSQL_TIME.class));
    }

    public void setTime(int index, Time date) throws SQLException {

        validate();
        if (index < 0 || index > buf.length) {
            throw new SQLException("no such parameter : " + index);
        }

        if (buf[index] != null && !buf[index].isNull()) {
            buf[index].close();
        }

        int leng = Pointer.sizeof(MYSQL_TIME.class);
        Pointer p = Pointer.malloc(leng);
        Pointer.memset(p,0,leng);

        buf[index] = p;

        MYSQL_TIME time = new MYSQL_TIME(p);

        LocalTime localDate = date.toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalTime();

        time.hour(localDate.getHour());
        time.minute(localDate.getMinute());
        time.second(localDate.getSecond());
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_TIME);

        MYSQL_BIND bind = binds.getPointer(index);
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_TIME);
        bind.buffer_length(leng);
        bind.buffer(time);

        updateBind(bind,index,false,Pointer.sizeof(MYSQL_TIME.class));

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
        Pointer.memset(time,0,leng);

        LocalDateTime localDate = ts.toInstant()
                .atZone(ZoneId.of(getTimeZoneId()))
                .toLocalDateTime();

        time.year(localDate.getYear());
        time.month(localDate.getMonth().getValue());
        time.day(localDate.getDayOfMonth());

        time.hour(localDate.getHour());
        time.minute(localDate.getMinute());
        time.second(localDate.getSecond());
        time.second_part(0);
        time.time_type(MyGlobal.enum_mysql_timestamp_type.MYSQL_TIMESTAMP_DATETIME);

        MYSQL_BIND bind = new MYSQL_BIND(binds.getPointer(index));
        bind.buffer_type(MyCom.enum_field_types.MYSQL_TYPE_DATETIME);
        bind.buffer_length(leng);
        bind.buffer(time);

        updateBind(bind,index,false,Pointer.sizeof(MYSQL_TIME.class));

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
        item.nullFlags = nullFlags;
        item.lengths = lengths;

        batches.add(item);

        this.binds = new MYSQL_BIND(stmt.param_count());
        this.buf = new Pointer[stmt.param_count()];
        this.nullFlags = new BytePointer(Pointer.malloc(
                (long) Pointer.sizeof(BytePointer.class) * stmt.param_count())
        );
        this.lengths = new CLongPointer(Pointer.malloc(
                (long) Pointer.sizeof(CLongPointer.class) * stmt.param_count()
        ));

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

        int state = MariaDB.mysql_stmt_bind_param(stmt,binds);
        if (state != 0) {
            throw new SQLException("failed to execute this query : " + sql + " with errno : " + MariaDB.mysql_stmt_errno(stmt) +
                    "\n" + MariaDB.mysql_stmt_error(stmt).getString());
        }

        state = MariaDB.mysql_stmt_execute(stmt);
        if (state != 0) {
            throw new SQLException("failed to execute this query : " + sql + " with errno : " + MariaDB.mysql_stmt_errno(stmt) +
                    "\n" + MariaDB.mysql_stmt_error(stmt).getString());
        }


        return MariaDB.mysql_stmt_affected_rows(stmt);

    }

    public MySQLPreparedResult execute() throws SQLException {

        validate();

        if(MariaDB.mysql_stmt_reset(stmt) != 0) {
            throw new SQLException("failed to complete operation.");
        }

        int state = 0;
        if (stmt.param_count() > 0) {
            state = MariaDB.mysql_stmt_bind_param(stmt,binds);
        }

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

        return new MySQLPreparedResult(res,stmt,getTimeZoneId());

    }

    public boolean executeNext() throws SQLException {

        this.executeUpdate();
        if (!batches.isEmpty()) {

            for (int idx = 0; idx < stmt.param_count(); idx ++) {
                MYSQL_BIND bind = binds.getPointer(idx);
                if (bind.buffer() != null && !bind.buffer().isNull()) {
                    bind.close();
                }
                if (buf[idx] != null && !buf[idx].isNull()) {
                    buf[idx].close();
                }
                bind.close();
                buf = null;
            }
            lengths.close();
            nullFlags.close();

            BatchItem item = batches.removeFirst();
            this.binds = item.binds;
            this.buf = item.buf;
            this.lengths = item.lengths;
            this.nullFlags = item.nullFlags;
        }

        return true;

    }

    public long executeUpdateNext() throws SQLException {

        long affectRows = this.executeUpdate();
        if (!batches.isEmpty()) {
            for (int idx = 0; idx < stmt.param_count(); idx ++) {
                MYSQL_BIND bind = binds.getPointer(idx);
                if (bind.buffer() != null && !bind.buffer().isNull()) {
                    bind.close();
                }
                if (buf[idx] != null && !buf[idx].isNull()) {
                    buf[idx].close();
                }
                bind.close();
                buf = null;
            }
            lengths.close();
            nullFlags.close();

            BatchItem item = batches.removeFirst();
            this.binds = item.binds;
            this.buf = item.buf;
            this.lengths = item.lengths;
            this.nullFlags = item.nullFlags;
        }

        return affectRows;

    }

    public int getBatchSize() {
        return batches.size();
    }

    @Override
    public synchronized boolean closeBySource() {

        if (stmt != null && !stmt.isNull()) {

            MariaDB.mysql_stmt_free_result(stmt);
            MariaDB.mysql_stmt_close(stmt);
            stmt = null;

            for (Pointer buf: this.buf) {
                if (buf != null && !buf.isNull()) {
                    buf.close();
                }
            }
            this.buf = null;
            if (nullFlags != null && !nullFlags.isNull()) {
                nullFlags.close();
                nullFlags = null;
            }
            if (lengths != null && !lengths.isNull()) {
                lengths.close();
                lengths = null;
            }
            if (binds != null) {
                binds.close();
                binds = null;
            }

            return true;

        }

        return false;

    }
}
