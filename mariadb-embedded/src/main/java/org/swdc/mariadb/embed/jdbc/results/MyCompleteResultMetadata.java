package org.swdc.mariadb.embed.jdbc.results;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyCompleteResultMetadata implements ResultSetMetaData {

    private Map<Integer, String> labels = new HashMap<>();
    private Map<Integer, Class> types = new HashMap<>();

    public MyCompleteResultMetadata() {

    }

    public MyCompleteResultMetadata putColumn(int index, String label, Class type) {
        labels.put(index,label);
        types.put(index,type);
        return this;
    }

    public Class getJavaType(int column) {
        return types.get(column - 1);
    }

    public int findColumn(String label) throws SQLException{
        String lbl = label.toUpperCase();
        for (Map.Entry<Integer,String> ent: labels.entrySet()) {
            if (ent.getValue().toUpperCase().equals(lbl)) {
                return ent.getKey() + 1;
            }
        }
        throw new SQLException("no such column" + label);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return labels.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
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
        return ResultSetMetaData.columnNullable;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        Class type = types.get(column - 1);
        if (type == null) {
            return false;
        }
        if (type.isPrimitive()) {
            if (type == int.class || type == short.class || type == long.class) {
                return true;
            }
            return false;
        } else if (type == Integer.class || type == Long.class || type == Short.class) {
            return true;
        }
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return types.get(column - 1) == String.class ? labels.get(column - 1).length() : 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return labels.get(column - 1);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return labels.get(column - 1);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        Class type = types.get(column - 1);
        if (type == int.class || type == Integer.class) {
            return Types.INTEGER;
        } else if (type == long.class || type == Long.class) {
            return Types.INTEGER;
        } else if (type == String.class || type == char[].class) {
            return Types.VARCHAR;
        } else if (type == boolean.class || type == Boolean.class) {
            return Types.BIT;
        } else if (type == Short.class || type == short.class) {
            return Types.SMALLINT;
        } else if (type == Date.class) {
            return Types.DATE;
        } else if (type == Time.class) {
            return Types.TIME;
        } else if (type == BigDecimal.class) {
            return Types.DECIMAL;
        } else if (type == Timestamp.class) {
            return Types.TIMESTAMP;
        }
        throw new SQLException("not supported type : " + column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        Class type = types.get(column - 1);
        if (type == int.class || type == Integer.class) {
            return "MEDIUMINT";
        } else if (type == long.class || type == Long.class) {
            return "INTEGER";
        } else if (type == String.class || type == char[].class) {
            return "VARCHAR";
        } else if (type == boolean.class || type == Boolean.class) {
            return "BIT";
        } else if (type == Short.class || type == short.class) {
            return "SMALLINT";
        } else if (type == Date.class) {
            return "DATE";
        } else if (type == Time.class) {
            return "TIME";
        } else if (type == BigDecimal.class) {
            return "DECIMAL";
        } else if (type == Timestamp.class) {
            return "TIMESTAMP";
        }
        throw new SQLException("not support type : " + column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
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
        return types.containsKey(column - 1) ? types.get(column - 1).getName() : null;
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
