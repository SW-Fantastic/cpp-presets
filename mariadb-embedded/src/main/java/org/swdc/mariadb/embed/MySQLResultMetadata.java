package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.mysql.MYSQL_FIELD;
import org.swdc.mariadb.core.mysql.MYSQL_RES;
import org.swdc.mariadb.embed.jdbc.CharsetEncodingLength;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;



public class MySQLResultMetadata {

    private static final int BINARY_CHARSET = 63;

    /**
     * 结果集的字段信息
     */
    private Map<Integer, MYSQL_FIELD> indexedFieldMap = new HashMap<>();
    private Map<String, Integer> namedFieldMap = new HashMap<>();

    private int fieldCount;

    public MySQLResultMetadata(MYSQL_RES res) {
        this.fieldCount = MariaDB.mysql_num_fields(res);
        for (int index = 0; index < fieldCount; index ++) {
            MYSQL_FIELD field = MariaDB.mysql_fetch_field_direct(res,index);
            indexedFieldMap.put(index, field);
            namedFieldMap.put(field.name().getString(),index);
        }
    }

    MYSQL_FIELD getField(int index) throws SQLException {
        index = index - 1;
        if (!indexedFieldMap.containsKey(index)) {
            throw new SQLException("no such field :" + index);
        }
        return indexedFieldMap.get(index);
    }

    MYSQL_FIELD getField(String field) throws SQLException {
        if (!namedFieldMap.containsKey(field)) {
            throw new SQLException("no such field");
        }
        return indexedFieldMap.get(namedFieldMap.get(field));
    }

    public boolean isAutoIncrease(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return (field.flags() & MyCom.AUTO_INCREMENT_FLAG) != 0;
    }


    public boolean isNotNull(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return (field.flags() & MyCom.NOT_NULL_FLAG) != 0;
    }


    public boolean isUnsigned(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return (field.flags() & MyCom.UNSIGNED_FLAG) != 0;
    }

    public int getColumnDisplaySize(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR,
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_ENUM,
                MyCom.enum_field_types.MYSQL_TYPE_SET
        )) {
            Integer len = CharsetEncodingLength.maxCharlen.get(field.charsetnr());
            if (len != null) {
                return (int) (field.length() / len);
            }
        }
        return (int)field.length();
    }


    public String getColumnLabel(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return field.name().getString();
    }

    public String getColumnName(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return field.org_name().getString();
    }

    public int getPrecision(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING
        )) {
            if (field.charsetnr() != BINARY_CHARSET) {
                // is not the binary data
                Integer len = CharsetEncodingLength.maxCharlen.get(field.charsetnr());
                if (len != null) {
                    return (int) (field.length() / len);
                }
            }
        } else if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DECIMAL,
                MyCom.enum_field_types.MYSQL_TYPE_NEWDECIMAL)
        ) {
            boolean isNotSigned = (field.flags() & MyCom.UNSIGNED_FLAG) != 0;
            if (!isNotSigned) {
                return (int) (field.max_length() - ((field.decimals() > 0) ? 2 : 1));
            } else {
                return (int) (field.max_length() - ((field.decimals() > 0) ? 1 : 0));
            }
        }
        return (int)field.length();
    }

    public int getDecimals(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return field.decimals();
    }

    public int findField(String field) throws SQLException {
        if (!namedFieldMap.containsKey(field)) {
            throw new SQLException("no such field");
        }
        return namedFieldMap.get(field);
    }

    public String getTableName(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return field.table().getString();
    }

    public String getCategoryName(int index) throws SQLException {
        MYSQL_FIELD field = getField(index);
        return field.db().getString();
    }

    public int getJDBCColumnType(int col) throws SQLException {
        MYSQL_FIELD field = getField(col);

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DECIMAL)) {
            return Types.DECIMAL;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_BIT)) {
            if (field.length() == 1) {
                return Types.BOOLEAN;
            } else {
                return Types.VARBINARY;
            }
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {
            if (field.length() <= 0 || getColumnDisplaySize(col) > 16777215) {
                return field.charsetnr() == BINARY_CHARSET ? Types.LONGVARBINARY : Types.LONGVARCHAR;
            } else if (IMySQLResultSet.accept(
                    field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                    MyCom.enum_field_types.MYSQL_TYPE_BLOB
            )) {
                return (field.charsetnr() == BINARY_CHARSET) ? Types.VARBINARY : Types.VARCHAR;
            }
            return field.charsetnr() == BINARY_CHARSET ? Types.LONGVARBINARY : Types.LONGVARCHAR;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DATE)) {
            return Types.DATE;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DOUBLE)) {
            return Types.DOUBLE;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_FLOAT)) {
            return Types.REAL;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_GEOMETRY)) {
            return Types.VARBINARY;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_LONGLONG)) {
            return Types.BIGINT;
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONG,
                MyCom.enum_field_types.MYSQL_TYPE_INT24
        )) {
            return Types.INTEGER;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_SHORT)) {
            return Types.SMALLINT;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_TINY)) {
            return (field.flags() & MyCom.UNSIGNED_FLAG) != 0 ? Types.SMALLINT : Types.TINYINT;
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
        )) {

            boolean binary = field.charsetnr() == BINARY_CHARSET;
            if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_STRING)) {
                return binary ? Types.VARBINARY : Types.CHAR;
            }
            if(field.length()  <= 0 || getColumnDisplaySize(col) > 16777215) {
                return binary ? Types.LONGVARBINARY : Types.LONGVARCHAR;
            }
            return binary ? Types.VARBINARY : Types.VARCHAR;
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            return Types.TIME;
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DATE)) {
            return Types.DATE;
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME
        )) {
            return Types.TIMESTAMP;
        }

        throw new SQLException("unsupport type");
    }

    public String getTypeName(int index) throws SQLException {

        MYSQL_FIELD field = getField(index);
        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DECIMAL)) {
            return "DECIMAL";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_BIT)) {
            return "BIT";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TINY_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_LONG_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_MEDIUM_BLOB,
                MyCom.enum_field_types.MYSQL_TYPE_BLOB
        )) {

            long length = field.length();

            if (field.charsetnr() == BINARY_CHARSET) {
                if (length < 0) {
                    return "LONGBLOB";
                } else if (length <= 255) {
                    return "TINYBLOB";
                } else if (length <= 65535) {
                    return "BLOB";
                } else if (length <= 16777215) {
                    return "MEDIUMBLOB";
                } else {
                    return "LONGBLOB";
                }
            } else {

                length = getColumnDisplaySize(index);
                if (length < 0) {
                    return "LONGTEXT";
                } else if (length <= 65532) {
                    return "VARCHAR";
                } else if (length <= 65535) {
                    return "TEXT";
                } else if (length <= 16777215) {
                    return "MEDIUMTEXT";
                } else {
                    return "LONGTEXT";
                }
            }

        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DATE)) {
            return "DATE";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DOUBLE)) {
            return "DOUBLE";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_FLOAT)) {
            return "FLOAT";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_GEOMETRY)) {
            return "GEOMETRY";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_LONGLONG)) {
            return "BIGINT";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_LONG
        )) {
            return "INTEGER";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_INT24
        )) {
            return "MEDIUMINT";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_SHORT)) {
            boolean unsigned = (field.flags() & MyCom.UNSIGNED_FLAG) != 0;
            if (unsigned) {
                return "SMALLINT UNSIGNED";
            }
            return "SMALLINT";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_TINY)) {
            return (field.flags() & MyCom.UNSIGNED_FLAG) != 0 ? "TINYINT UNSIGNED" : "TINYINT";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
        )) {

            boolean binary = field.charsetnr() == BINARY_CHARSET;
            if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_STRING)) {
                return binary ? "BINARY" : "CHAR";
            }
            if (IMySQLResultSet.accept(
                    field.type(),
                    MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING,
                    MyCom.enum_field_types.MYSQL_TYPE_VARCHAR
            )) {
                int len = getColumnDisplaySize(index);
                if (len < 0) {
                    return "LONGTEXT";
                } else if (len <= 65532) {
                    return "VARCHAR";
                } else if (len <= 65535) {
                    return "TEXT";
                } else if (len <= 16777215) {
                    return "MEDIUMTEXT";
                } else {
                    return "LONGTEXT";
                }
            }

        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIME,
                MyCom.enum_field_types.MYSQL_TYPE_TIME2
        )) {
            return "TIME";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME,
                MyCom.enum_field_types.MYSQL_TYPE_DATETIME2
        )) {
            return "DATETIME";
        }

        if (IMySQLResultSet.accept(field.type(), MyCom.enum_field_types.MYSQL_TYPE_DATE)) {
            return "DATE";
        }

        if (IMySQLResultSet.accept(
                field.type(),
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP,
                MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2
        )) {
            return "TIMESTAMP";
        }

        throw new SQLException("no support type for type index " + index + " named : " + field.name().getString());
    }

    public int getFieldCount() {
        return fieldCount;
    }


}
