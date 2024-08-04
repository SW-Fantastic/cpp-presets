package org.swdc.mariadb.embed;

/**
 * Mysql的Table中的一个字段。
 */
public class TableField {

    @ResultField("Field")
    private String field;

    @ResultField("Type")
    private String type;

    @ResultField("Null")
    private String nullable;

    @ResultField("Key")
    private String key;

    @ResultField("Default")
    private String defaultValue;

    @ResultField("Extra")
    private String extra;

    public TableField() {

    }

    public String getField() {
        return field;
    }

    public String getType() {
        return type;
    }

    public String getNullable() {
        return nullable;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "TableField{" +
                "field='" + field + '\'' +
                ", type='" + type + '\'' +
                ", nullable='" + nullable + '\'' +
                ", key='" + key + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
