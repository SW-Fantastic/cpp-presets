package org.swdc.mariadb.embed.jdbc;

import org.swdc.mariadb.embed.EmbeddedMariaDB;
import org.swdc.mariadb.embed.MySQLDBConnection;
import org.swdc.mariadb.embed.exec.MySQLExecutor;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class EmbedMariaDBDriver implements Driver {

    public static final String PREFIX = "jdbc:mysql://";

    private MySQLExecutor mariaDB;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Configure configure = new Configure(url);
        info.putAll(configure.getInfo());

        if (configure.getBaseDir() == null || configure.getBaseDir().isBlank() || configure.getDataDir() == null || configure.getDataDir().isBlank()) {
            throw new SQLException("parameter datadir and basedir is required, please add them on url");
        }

        return connect(configure);
    }

    public Connection connect(Configure configure) throws SQLException {
        File dataDir = new File(configure.getDataDir());
        File baseDir = new File(configure.getBaseDir());

        if (mariaDB == null) {

            if (!baseDir.exists()) {
                if(!baseDir.mkdirs()) {
                    throw new SQLException("Failed to init database ,can not access basedir");
                }
            }

            if (!dataDir.exists()) {
                if(!dataDir.mkdirs()) {
                    throw new SQLException("Failed to init database ,can not access datadir");
                }
            }

            this.mariaDB = EmbeddedMariaDB.getMariaDB(
                    baseDir, dataDir
            );

        }

        /*if (!mariaDB.initialize()) {
            throw new SQLException("failed to initialize this mariaDB library");
        }

        mariaDB.initSystemData();*/

        try {
            MySQLDBConnection connection = mariaDB.execute(
                    db -> db.connect(configure.getDbName())
            );
            if (connection != null) {
                return new MyConnection(mariaDB,connection);
            } else {
                if (configure.isAutoCreate() && mariaDB.execute(
                        db -> db.createDatabase(configure.getDbName(),null,null)
                )) {
                    connection = mariaDB.execute(
                            db -> db.connect(configure.getDbName())
                    );
                    if (connection != null) {
                        return new MyConnection(mariaDB,connection);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("failed to connect database", e);
        }
        throw new SQLException("failed to connect database");
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
