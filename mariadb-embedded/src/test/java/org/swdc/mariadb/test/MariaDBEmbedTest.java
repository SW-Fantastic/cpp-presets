package org.swdc.mariadb.test;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.embed.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class MariaDBEmbedTest {

    public static void main(String[] args) throws SQLException {

        Loader.load(MariaDB.class);

        EmbeddedMariaDB mariaDB = EmbeddedMariaDB.getMariaDB(
                new File("./mysqlData/base"),
                new File("./mysqlData/data")
        );

        if (!mariaDB.initialize()) {
            System.err.println("Failed to init mariaDB");
        }

        mariaDB.initSystemData();

        List<String> names = mariaDB.getDatabases();
        System.err.println("load databases");
        for (String name: names) {
            System.err.println("DB: " + name);
        }

        String myCustomDB = "dbForTest";
        MySQLDBConnection customDB = mariaDB.connect(myCustomDB);
        if (customDB == null) {
            return;
        } else {
            System.err.println("");
            for (int i = 0; i < 80 ; i ++) {
                if (i == 60) {
                    throw new SQLException("just throw for test close function on exception.");
                }
                MySQLStatement statement=customDB.createStatement();
                MySQLResultSet result = statement.executeQuery("SELECT id,name,age,nextAim,source,createdOn,createdAt,state FROM entuser");
                while (result.next()) {
                    System.err.print("Id : " + result.getLong(0) + " | ");
                    System.err.print("Name:" + result.getString(1) + " | ");
                    System.err.print("Age: " + result.getInt(2) + " | ");
                    System.err.print("NextAim: " + result.getFloat(3) + " | ");
                    System.err.print("Source: " + result.getDouble(4) + " | ");
                    System.err.print("Created : " + result.getDate(5) + " | ");
                    System.err.print("Created at: " + result.getTimestamp(6) + " | ");
                    System.err.print("State : " + result.getBoolean(7) + " | ");
                    System.err.println();
                }
                result.close();
                statement.close();
            }

        }
        customDB.close();
    }

}
