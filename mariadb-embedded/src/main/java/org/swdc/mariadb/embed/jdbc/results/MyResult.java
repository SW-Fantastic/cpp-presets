package org.swdc.mariadb.embed.jdbc.results;

import org.swdc.mariadb.embed.jdbc.MyBlob;
import org.swdc.mariadb.embed.jdbc.MyClob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public abstract class MyResult implements ResultSet {


    public MyResult() {
    }


    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return null;
    }


    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        String str = getString(columnIndex);
        if (str != null) {
            return new ByteArrayInputStream(str.getBytes());
        }
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        String str = getString(columnIndex);
        if (str != null) {
            return new ByteArrayInputStream(str.getBytes());
        }
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        byte[] data = getBytes(columnIndex);
        if (data == null) {
            return null;
        }
        return new ByteArrayInputStream(data);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel),scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date date = getDate(columnIndex);
        if (date != null) {
            return Date.valueOf(
                    date.toLocalDate()
                            .atStartOfDay(cal.getTimeZone().toZoneId())
                            .toLocalDate()
            );
        }
        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel),cal);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time time = getTime(columnIndex);
        if (time != null) {
            TimeZone zone = cal.getTimeZone();
            ZoneOffset theOffset = ZoneOffset.ofTotalSeconds(zone.getRawOffset() / 1000);
            return Time.valueOf(
                    time.toLocalTime()
                            .atOffset(theOffset)
                            .toLocalTime()
            );
        }
        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel),cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp timestampZonedDefault = getTimestamp(columnIndex);
        TimeZone zone = cal.getTimeZone();
        return new Timestamp(timestampZonedDefault
                .toInstant()
                .atZone(zone.toZoneId())
                .toEpochSecond()
        );
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel),cal);
    }


    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return null;
    }


    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLException("not supported.");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return FETCH_UNKNOWN;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw notSupport();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw notSupport();
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void insertRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public void deleteRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public void refreshRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw notSupport();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw notSupport();
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        if (map == null || map.isEmpty()) {
            return getObject(columnIndex);
        }
        throw new SQLException("Method ResultSet.getObject(int columnIndex, Map<String, Class<?>> map) not supported for non empty map");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException("Method ResultSet.getRef not supported");
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        byte[] data = getBytes(columnIndex);
        if (data == null) {
            return null;
        }
        return new MyBlob(data);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        byte[] data = getBytes(columnIndex);
        if (data == null) {
            return null;
        }
        return new MyClob(data);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException("Method ResultSet.getArray not supported");
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        if (map == null || map.isEmpty()) {
            return getObject(columnLabel);
        }
        throw new SQLException("Method ResultSet.getObject(int columnIndex, Map<String, Class<?>> map) not supported for non empty map");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLException("Method ResultSet.getRef not supported");
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLException("Method ResultSet.getArray not supported");
    }


    @Override
    public URL getURL(int columnIndex) throws SQLException {
        String str = getString(columnIndex);
        if (str == null) {
            return null;
        }
        try {
            return URI.create(str).toURL();
        } catch (MalformedURLException e) {
            throw new SQLException("invalid url format ", e);
        }
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw notSupport();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw notSupport();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw notSupport();
    }

    @Override
    public int getHoldability() throws SQLException {
        return HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw notSupport();
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return (NClob) getClob(columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return (NClob) getClob(columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw notSupport();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw notSupport();
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        String str = getNString(columnIndex);
        if (str == null) {
            return null;
        }
        return new StringReader(str);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw notSupport();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    protected SQLException notSupport(){
        return new SQLException("Not supported when using CONCUR_READ_ONLY concurrency");
    }

}
