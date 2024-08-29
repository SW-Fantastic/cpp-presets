package org.swdc.mariadb.embed;


/**
 * 可关闭监听器。
 *
 * Mariadb在关闭前需要关闭所有Statement和ResultSet等本地对象，
 * 方可安全的退出，所以，需要了解有那些对象尚未关闭，一个对象关闭的时候应该
 * 首先关闭和它关联的对象，例如关闭Statement应当首先关闭ResultSet。
 *
 * 通过Listener，可以在一个关联的可关闭对象关闭的后得到通知，从而将它在列表中移除。
 */
public interface CloseableListener {

    void closed(CloseableSource closeable);

}
