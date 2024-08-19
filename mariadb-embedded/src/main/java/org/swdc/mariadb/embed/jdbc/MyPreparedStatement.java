package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.MySQLPreparedStatement;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;

public class MyPreparedStatement extends MyStatement implements PreparedStatement {


    public MyPreparedStatement(MyConnection connection, MySQLPreparedStatement statement, int resultType, int resultConcurrency) {
        super(connection, statement, resultType, resultConcurrency);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {

        return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return 0;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {

    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setBoolean(parameterIndex,x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setByte(parameterIndex,x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setShort(parameterIndex,x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setInt(parameterIndex,x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setLong(parameterIndex,x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setFloat(parameterIndex,x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setDouble(parameterIndex,x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setDecimal(parameterIndex,x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setString(parameterIndex,x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setBytes(parameterIndex,x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setDate(parameterIndex,x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setTime(parameterIndex,x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.setTimestamp(parameterIndex,x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
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
            byte[] data = x.readNBytes(length);
            setBytes(parameterIndex,data);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.clearParams();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {

    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }

    @Override
    public void addBatch() throws SQLException {
        MySQLPreparedStatement stmt = (MySQLPreparedStatement) statement;
        stmt.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {

    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {

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

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }
}
