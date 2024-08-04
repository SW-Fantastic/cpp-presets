package org.swdc.mariadb.embed;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.core.mysql.MYSQL;
import org.swdc.mariadb.core.mysql.MYSQL_RES;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MySQLDBConnection implements Closeable {

    private MYSQL mariaDB;

    protected MySQLDBConnection(MYSQL db) {
        this.mariaDB = db;
    }

    private void valid() {

        if (mariaDB == null || mariaDB.isNull()) {
            throw new RuntimeException("this object has closed.");
        }

    }

    public synchronized List<String> getTables(String wild) {

        valid();

        List<String> results = new ArrayList<>();

        MYSQL_RES res = MariaDB.mysql_list_tables(mariaDB, wild);
        PointerPointer dbPointers = null;
        while ((dbPointers = MariaDB.mysql_fetch_row(res)) != null && !dbPointers.isNull()) {
            BytePointer cp = new BytePointer(dbPointers.get(0));
            String data = cp.getString();
            results.add(data);
            dbPointers.close();
        }
        MariaDB.mysql_free_result(res);

        return results;
    }

    public synchronized List<TableField> listFields(String tableName) {

        valid();
        String query = "SHOW COLUMNS FROM " + tableName;
        int rst = MariaDB.mysql_real_query(mariaDB,query,query.length());
        if (rst != 0) {
            return Collections.emptyList();
        }

        MYSQL_RES res = MariaDB.mysql_use_result(mariaDB);
        if (res == null || res.isNull()) {
            return Collections.emptyList();
        }

        try {
            List<TableField> fields = FieldMapper.mapFromResult(res,TableField.class);
            MariaDB.mysql_free_result(res);
            return fields;
        } catch (Exception e) {
            MariaDB.mysql_free_result(res);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void close() {
        if (mariaDB != null && !mariaDB.isNull()) {
            MariaDB.mysql_close(mariaDB);
            mariaDB = null;
        }
    }

}
