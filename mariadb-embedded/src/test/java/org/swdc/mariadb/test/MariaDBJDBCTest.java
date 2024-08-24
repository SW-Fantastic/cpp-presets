package org.swdc.mariadb.test;

import org.swdc.mariadb.embed.EmbeddedMariaDB;
import org.swdc.mariadb.embed.jdbc.EmbedMariaDBDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MariaDBJDBCTest {

    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new EmbedMariaDBDriver());
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://dbForTest?basedir=./mysqlData&datadir=./mysqlData/data&autocreate=true"
        );
        boolean init = connection.createStatement().execute("CREATE TABLE IF NOT EXISTS testuser (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "age INT" +
                ")");
        if (init) {
            System.err.println("init table ok");
        }
        System.err.println("ok");
        connection.close();
    }

}
