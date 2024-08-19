package org.swdc.mariadb.test;

import org.swdc.mariadb.embed.jdbc.EmbedMariaDBDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MariaDBJDBCTest {

    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new EmbedMariaDBDriver());
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://test_db?basedir=./mysqlData/base&datadir=./mysqlData/data"
        );
        System.err.println("ok");
    }

}
