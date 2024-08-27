package org.swdc.mariadb.embed.exec;

import org.swdc.mariadb.embed.EmbeddedMariaDB;

import java.util.concurrent.Callable;

public interface DBCallable<V> {

    V call(EmbeddedMariaDB mariaDB) throws Exception;

}
