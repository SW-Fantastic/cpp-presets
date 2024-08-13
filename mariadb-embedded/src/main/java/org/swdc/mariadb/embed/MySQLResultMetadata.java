package org.swdc.mariadb.embed;

import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.mysql.MYSQL_FIELD;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class MySQLResultMetadata {

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

    public int findField(String field) throws SQLException {
        if (!namedFieldMap.containsKey(field)) {
            throw new SQLException("no such field");
        }
        return namedFieldMap.get(field);
    }

    public int getJDBCColumnType(int col) throws SQLException {
        MYSQL_FIELD field = getField(col);
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_TINY.value) {
            return Types.TINYINT;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_INT24.value) {
            return Types.INTEGER;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_LONG.value) {
            return Types.BIGINT;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_VARCHAR.value) {
            return Types.VARCHAR;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING.value) {
            return Types.VARCHAR;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_STRING.value) {
            return Types.VARCHAR;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_SHORT.value) {
            return Types.INTEGER;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_FLOAT.value) {
            return Types.FLOAT;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_DOUBLE.value) {
            return Types.DOUBLE;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_DATE.value) {
            return Types.DATE;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_TIME.value) {
            return Types.TIME;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP.value) {
            return Types.TIMESTAMP;
        }
        if (field.type().value == MyCom.enum_field_types.MYSQL_TYPE_TIMESTAMP2.value) {
            return Types.TIMESTAMP;
        }

        throw new SQLException("unsupport type");
    }

    public int getFieldCount() {
        return fieldCount;
    }


}
