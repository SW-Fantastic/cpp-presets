package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.EmbeddedMariaDB;
import org.swdc.mariadb.embed.MySQLDBConnection;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class EmbedMariaDBDriver implements Driver {

    private static String PREFIX = "jdbc:mysql://";

    private EmbeddedMariaDB mariaDB;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        url = url.substring(PREFIX.length());
        int indexOfQuery = url.lastIndexOf("?");
        String props = url.substring(indexOfQuery + 1);
        String databaseName = url.substring(0,indexOfQuery);
        String[] kv = props.split("&");
        for (String pair : kv) {
            if (pair.contains("=")) {
                String[] pairData = pair.split("=");
                info.put(pairData[0],pairData[1]);
            } else {
                info.put(pair,"true");
            }
        }
        info.put("database",databaseName);

        if (mariaDB == null) {
            if (!info.containsKey("datadir") || !info.containsKey("basedir")) {
                throw new SQLException("parameter datadir and basedir is required, please add them on url");
            }

            this.mariaDB = EmbeddedMariaDB.getMariaDB(
                    new File(info.getProperty("basedir")),
                    new File(info.getProperty("datadir"))
            );

        }

        if (!mariaDB.initialize()) {
            throw new SQLException("failed to initialize this mariaDB library");
        }

        MySQLDBConnection connection = mariaDB.connect(databaseName);
        if (connection != null) {
            return new MyConnection(connection);
        }
        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.toLowerCase().startsWith(PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
