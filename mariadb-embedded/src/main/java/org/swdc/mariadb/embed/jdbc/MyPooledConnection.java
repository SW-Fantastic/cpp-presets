package org.swdc.mariadb.embed.jdbc;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyPooledConnection implements PooledConnection {

    private final MyConnection connection;
    private final List<ConnectionEventListener> connectionEventListeners;
    private final List<StatementEventListener> statementEventListeners;

    /**
     * Constructor.
     *
     * @param connection connection to retrieve connection options
     */
    public MyPooledConnection(MyConnection connection) {
        this.connection = connection;
        this.connection.setPoolConnection(this);
        statementEventListeners = new CopyOnWriteArrayList<>();
        connectionEventListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.remove(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        statementEventListeners.add(listener);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        statementEventListeners.remove(listener);
    }


    public void fireConnectionClosed(ConnectionEvent connectionEvent) {
        for (ConnectionEventListener listener : connectionEventListeners) {
            listener.connectionClosed(connectionEvent);
        }
    }

    /**
     * Fire connection error event to registered listeners.
     *
     * @param returnEx exception
     */
    public void fireConnectionErrorOccurred(SQLException returnEx) {
        ConnectionEvent event = new ConnectionEvent(this, returnEx);
        for (ConnectionEventListener listener : connectionEventListeners) {
            listener.connectionErrorOccurred(event);
        }
    }


    /**
     * Close underlying connection
     *
     * @throws SQLException if close fails
     */
    @Override
    public void close() throws SQLException {
        fireConnectionClosed(new ConnectionEvent(this));
        connection.setPoolConnection(null);
        connection.close();
    }

}
