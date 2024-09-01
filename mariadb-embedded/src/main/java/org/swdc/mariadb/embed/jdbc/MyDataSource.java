package org.swdc.mariadb.embed.jdbc;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class MyDataSource implements DataSource, Closeable, AutoCloseable {

    private String url;

    private PrintWriter writer;

    public MyDataSource() {
    }

    public MyDataSource(String url) throws SQLException {
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Driver driver = DriverManager.getDriver(url);
        if (driver instanceof EmbedMariaDBDriver) {
            return driver.connect(url,new Properties());
        }
        throw new SQLException("can not get driver");
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return writer;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.writer = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return iface.cast(this);
        }
        throw new SQLException("Datasource is not a wrapper for " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public void close() throws IOException {

    }
}
