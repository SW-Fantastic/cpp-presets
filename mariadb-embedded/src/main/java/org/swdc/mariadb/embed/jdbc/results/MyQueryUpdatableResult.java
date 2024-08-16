package org.swdc.mariadb.embed.jdbc.results;

import org.swdc.mariadb.embed.MySQLResultSet;
import org.swdc.mariadb.embed.jdbc.MyStatement;

import java.sql.SQLException;

public class MyQueryUpdatableResult extends MyQueryResult {

    public MyQueryUpdatableResult(MyStatement connection, MySQLResultSet rs, int type) {
        super(connection,rs,type);
    }

    @Override
    public int getConcurrency() throws SQLException {
        return CONCUR_UPDATABLE;
    }

}
