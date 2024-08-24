package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.MySQLPreparedResult;
import org.swdc.mariadb.embed.MySQLPreparedStatement;
import org.swdc.mariadb.embed.jdbc.results.MyQueryResult;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;

public class MyPreparedStatement extends MyStatement implements PreparedStatement {

    private String sql;

    private MyQueryResult result;

    public MyPreparedStatement(MyConnection connection,String sql, int resultType, int resultConcurrency) {
        super(connection, null, resultType, resultConcurrency);
        this.sql = sql;
    }

    protected MySQLPreparedStatement getStmt() throws SQLException {
        if(connection.isClosed()) {
            throw new SQLException("connection has closed");
        }
        if (statement == null) {
            statement = connection.connection.preparedStatement(
                    escapeTimeout(sql)
            );
        }
        return (MySQLPreparedStatement) statement;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if (result != null) {
            result.close();
            result = null;
        }
        MySQLPreparedStatement stmt = getStmt();
        MySQLPreparedResult result = stmt.execute();
        if (result != null) {
            this.result = new MyQueryResult(this,result);
            return this.result;
        }
        return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        if (result != null) {
            result.close();
            result = null;
        }
        return (int) getStmt().executeUpdate();
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        if (result != null) {
            result.close();
            result = null;
        }
        return (int)getStmt().executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setNull(parameterIndex);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setBoolean(parameterIndex,x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setByte(parameterIndex,x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setShort(parameterIndex,x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setInt(parameterIndex,x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setLong(parameterIndex,x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setFloat(parameterIndex,x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setDouble(parameterIndex,x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            stmt.setDecimal(parameterIndex,x);
        }
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            stmt.setString(parameterIndex,x);
        }
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x != null) {
            stmt.setBytes(parameterIndex,x);
        } else {
            stmt.setNull(parameterIndex);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x != null) {
            stmt.setDate(parameterIndex,x);
        } else {
            stmt.setNull(parameterIndex);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            stmt.setTime(parameterIndex,x);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            stmt.setTimestamp(parameterIndex,x);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.VARBINARY);
                return;
            }
            byte[] data = x.readNBytes(length);
            String str = new String(data, StandardCharsets.US_ASCII);
            setString(parameterIndex,str);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.BLOB);
                return;
            }
            byte[] data = x.readNBytes(length);
            String str = new String(data, StandardCharsets.UTF_8);
            setString(parameterIndex,str);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.BLOB);
                return;
            }
            byte[] data = x.readNBytes(length);
            setBytes(parameterIndex,data);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.clearParams();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setObject(parameterIndex,x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setObject(parameterIndex,x);
    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }

    @Override
    public void addBatch() throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        setCharacterStream(parameterIndex,reader,(long) length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("does not support ref");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.BLOB);
                return;
            }
            byte[] buf = x.getBinaryStream().readAllBytes();
            setBytes(parameterIndex,buf);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        setString(parameterIndex,x.toString());
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("does not support array");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

        if (x == null || cal == null) {
            throw new SQLException("parameter date or calendar can not be null");
        }

        LocalDate zoned = x.toLocalDate()
                .atStartOfDay(cal.getTimeZone().toZoneId())
                .toLocalDate();

        setDate(parameterIndex,Date.valueOf(zoned));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

        if (x == null || cal == null) {
            throw new SQLException("parameter date or calendar can not be null");
        }

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(cal.getTimeZone().getRawOffset() / 1000);
        LocalTime zoned = x.toLocalTime()
                .atOffset(offset)
                .toLocalTime();

        setTime(parameterIndex,Time.valueOf(zoned));

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

        if (x == null || cal == null) {
            setNull(parameterIndex,Types.DATE);
        }

        Timestamp ts = Timestamp.valueOf(x
                .toInstant()
                .atZone(cal.getTimeZone().toZoneId())
                .toLocalDateTime()
        );

        setTimestamp(parameterIndex,ts);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setNull(parameterIndex);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            stmt.setString(parameterIndex,x.toExternalForm());
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("does not support");
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("RowId parameter are not supported");
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setString(parameterIndex,value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        setCharacterStream(parameterIndex,value,length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        setCharacterStream(parameterIndex,value.getCharacterStream());
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex,reader,length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        setBinaryStream(parameterIndex,inputStream,length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex,reader,length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("does not support sqlxml");
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        stmt.setObject(parameterIndex,x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 1024];
            int len = -1;
            long readed = 0;
            try {
                while ((len = x.read(buf)) != -1) {
                    bos.write(buf,0,len);
                    readed = readed + len;
                    if (readed >= length) {
                        break;
                    }
                }
                setString(parameterIndex, bos.toString());
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (x == null) {
            stmt.setNull(parameterIndex);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 1024];
            int len = -1;
            long readed = 0;
            try {
                while ((len = x.read(buf)) != -1) {
                    bos.write(buf,0,len);
                    readed = readed + len;
                    if (readed >= length) {
                        break;
                    }
                }
                byte[] data = bos.toByteArray();
                setBytes(parameterIndex,data);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (value == null) {
            stmt.setNull(parameterIndex);
        } else {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                char[] buf = new char[1024 * 1024];
                int len = -1;
                long readed = 0;
                while ((len = value.read(buf)) != -1) {
                    byte[] bytes = String.valueOf(buf,0,len).getBytes();
                    bos.write(bytes);
                    readed = readed + len;
                    if (readed >= length) {
                        break;
                    }
                }
                setString(parameterIndex, bos.toString());
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.CLOB);
                return;
            }
            byte[] buf = x.readAllBytes();
            setString(parameterIndex,new String(buf));
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (x == null) {
                setNull(parameterIndex,Types.BLOB);
                return;
            }
            byte[] buf = x.readAllBytes();
            setBytes(parameterIndex,buf);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader value) throws SQLException {
        MySQLPreparedStatement stmt = getStmt();
        if (value == null) {
            stmt.setNull(parameterIndex);
        } else {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                char[] buf = new char[1024 * 1024];
                int len = -1;
                while ((len = value.read(buf)) != -1) {
                    byte[] bytes = String.valueOf(buf,0,len).getBytes();
                    bos.write(bytes);
                }
                setString(parameterIndex, bos.toString());
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
       setCharacterStream(parameterIndex,value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        setCharacterStream(parameterIndex,reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        setBinaryStream(parameterIndex,inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        setCharacterStream(parameterIndex,reader);
    }
}
