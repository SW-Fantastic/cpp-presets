package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.CLongPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.MyCom;
import org.swdc.mariadb.core.mysql.MYSQL_FIELD;
import org.swdc.mariadb.core.mysql.MYSQL_RES;
import org.swdc.ours.common.type.Converter;
import org.swdc.ours.common.type.Converters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldMapper {

    private static final Converters converters = new Converters();

    private static Map<String,Field> getDeclaredField(Class type) {

        Map<String,Field> fields = new HashMap<>();
        Class curr = type;
        while (curr != null) {
            Field[] declaredFields = curr.getDeclaredFields();
            for (Field field: declaredFields) {
                ResultField rsField = field.getAnnotation(ResultField.class);
                if (rsField == null) {
                    continue;
                }
                fields.put(rsField.value(), field);
            }
            curr = curr.getSuperclass();
        }

        return fields;
    }

    private static Object extractData(MYSQL_FIELD rsField, Pointer data, long length) throws Exception {

        BytePointer pData = new BytePointer(data);
        Object rsData = null;
        if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_STRING.value ||
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_VAR_STRING.value
        ) {
            rsData = pData.getString();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_FLOAT.value
        ) {
            rsData = pData.getFloat();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_INT24.value
        ) {
            rsData = pData.getInt();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_SHORT.value
        ) {
            rsData = pData.getShort();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_TINY.value
        ) {
            rsData = pData.get();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_BIT.value
        ) {
            rsData = pData.getBool();
        } else if (
                rsField.type().value == MyCom.enum_field_types.MYSQL_TYPE_BLOB.value
        ) {
            byte[] buf = new byte[(int)length];
            pData.get(buf);
            rsData = buf;
        }

        return rsData;

    }

    public static <T> List<T> mapFromResult(MYSQL_RES res, Class type) throws Exception {

        int fields = MariaDB.mysql_num_fields(res);
        Map<String,Field> rsFields = getDeclaredField(type);

        List<T> resultList = new ArrayList<>();

        PointerPointer dbPointers = null;
        while ((dbPointers = MariaDB.mysql_fetch_row(res)) != null && !dbPointers.isNull()) {

            Object obj = type.getConstructor()
                    .newInstance();

            for (int fidx = 0; fidx < fields; fidx++) {


                MYSQL_FIELD field = MariaDB.mysql_fetch_field_direct(res,fidx);
                String fieldRsName = field.name().getString();

                if (!rsFields.containsKey(fieldRsName)) {
                    continue;
                }

                Pointer colData = dbPointers.get(fidx);
                if (colData == null || colData.isNull()) {
                    continue;
                }

                CLongPointer curLength = MariaDB.mysql_fetch_lengths(res);

                Object extracted = extractData(field,colData,curLength.get(fidx));
                Field rsField = rsFields.get(fieldRsName);

                rsField.setAccessible(true);

                if (rsField.getType().isAssignableFrom(extracted.getClass()) || rsField.getType() == extracted.getClass()) {
                    rsField.set(obj,extracted);
                } else {
                    Converter converter = converters.getConverter(extracted.getClass(),rsField.getType());
                    if (converter == null) {
                        continue;
                    }
                    rsField.set(obj,converter.convert(extracted));
                }

            }
            resultList.add((T)obj);
        }

        return resultList;

    }

}
