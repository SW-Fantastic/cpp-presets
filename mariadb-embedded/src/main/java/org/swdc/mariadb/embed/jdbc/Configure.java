package org.swdc.mariadb.embed.jdbc;

import org.swdc.ours.common.annotations.Annotations;
import org.swdc.ours.common.type.Converter;
import org.swdc.ours.common.type.Converters;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class Configure {

    private static Converters converters = new Converters();

    private String dbName;

    private Properties info = new Properties();

    @QueryNames({"autocreate", "auto-create", "autoCreate"})
    private boolean autoCreate;

    @QueryNames({"datadir", "data-dir", "dataDir"})
    private String dataDir;

    @QueryNames({"basedir", "base-dir", "baseDir"})
    private String baseDir;

    public Configure(String url) {

        url = url.substring(EmbedMariaDBDriver.PREFIX.length());
        int indexOfQuery = url.lastIndexOf("?");
        String props = url.substring(indexOfQuery + 1);
        this.dbName = url.substring(0,indexOfQuery);

        List<Field> fields = Annotations.getAnnotationField(
                this.getClass(), QueryNames.class
        );

        Map<String, Field> mappedFields = new HashMap<>();
        for (Field field: fields) {
            QueryNames queryNames = field.getAnnotation(QueryNames.class);
            for (String k : queryNames.value()) {
                mappedFields.put(k,field);
            }
        }


        String[] kv = props.split("&");
        for (String pair : kv) {

            String key = null;
            String value = null;
            if (pair.contains("=")) {
                String[] pairData = pair.split("=");
                key = pairData[0];
                value = pairData[1];
            } else {
                key = pair;
                value = "true";
            }

            info.setProperty(key,value);

            if (mappedFields.containsKey(key)) {
                try {
                    Field field = mappedFields.get(key);
                    if (field.getType() == String.class) {
                        field.set(this,value);
                    } else {
                        Converter converter = converters.getConverter(String.class, field.getType());
                        if (converter != null) {
                            field.set(this,converter.convert(value));
                        }
                    }
                } catch (Exception e) {
                }
            }

        }
    }

    public Properties getInfo() {
        return info;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
