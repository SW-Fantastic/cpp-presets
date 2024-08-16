package org.swdc.mariadb.embed.jdbc.results;

import org.swdc.mariadb.embed.MySQLResultMetadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MyResultMetadata implements ResultSetMetaData {

    private MySQLResultMetadata metadata;

    public MyResultMetadata(MySQLResultMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return metadata.getFieldCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return metadata.isAutoIncrease(column);
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return metadata.isNotNull(column) ? columnNoNulls : columnNullable;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return !metadata.isUnsigned(column);
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return metadata.getColumnDisplaySize(column);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return metadata.getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return metadata.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return metadata.getPrecision(column);
    }

    @Override
    public int getScale(int column) throws SQLException {
        return metadata.getDecimals(column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return metadata.getTableName(column);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return metadata.getCategoryName(column);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return metadata.getJDBCColumnType(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return metadata.getTypeName(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
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
}
