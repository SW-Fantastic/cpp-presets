package org.swdc.mariadb.test;

import org.bytedeco.javacpp.*;
import org.swdc.mariadb.core.MariaDB;
import org.swdc.mariadb.embed.EmbeddedMariaDB;
import org.swdc.mariadb.embed.MySQLDBConnection;

import java.io.File;
import java.util.List;

public class MariaDBEmbedTest {

    public static void main(String[] args) {

        Loader.load(MariaDB.class);

        EmbeddedMariaDB mariaDB = EmbeddedMariaDB.getMariaDB(
                new File("./mysqlData/base"),
                new File("./mysqlData/data")
        );

        if (mariaDB == null || !mariaDB.initialize()) {
            System.err.println("failed to init mariadb.");
            return;
        }

        mariaDB.withMySQL(() -> {


            List<String> names = mariaDB.getDatabases();
            System.err.println("load databases");
            for (String name: names) {
                System.err.println("DB: " + name);
            }

            String myCustomDB = "test_db";
            MySQLDBConnection customDB = mariaDB.connect("information_schema");
            if (customDB == null) {
                mariaDB.createDatabase(myCustomDB,null,null);
            } else {
                List<String> tables = customDB.getTables(null);
                for (String t: tables) {
                    customDB.listFields(t).forEach(f -> {
                        System.err.println("----------------------------------------");
                        System.err.println(f);
                    });
                }
            }

            customDB.close();


        });


        mariaDB.shutdown();
    }

}
