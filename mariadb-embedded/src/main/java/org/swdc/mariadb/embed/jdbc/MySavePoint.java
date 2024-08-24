package org.swdc.mariadb.embed.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public class MySavePoint implements Savepoint {

    private Integer id;

    private String name;


    public MySavePoint(final String name) {
        this.name = name;
        this.id = null;
    }

    public MySavePoint(final int savepointId) {
        this.id = savepointId;
        this.name = null;
    }

    public String rawValue() {
        if (id != null) {
            return "_jid_" + id;
        }
        return name;
    }
    @Override
    public int getSavepointId() throws SQLException {
        if (name != null) {
            throw new SQLException("Cannot retrieve savepoint id of a named savepoint");
        }
        return id;
    }

    @Override
    public String getSavepointName() throws SQLException {
        if (id != null) {
            throw new SQLException("Cannot retrieve savepoint name of an unnamed savepoint");
        }
        return name;
    }
}
