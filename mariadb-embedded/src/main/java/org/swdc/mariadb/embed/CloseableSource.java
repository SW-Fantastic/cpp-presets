package org.swdc.mariadb.embed;

import java.io.Closeable;

/**
 * 可关闭事件源。
 *
 * Mariadb关闭前必须关闭所有的Statement和ResultSet，因此需要了解那些对象没有被关闭，
 * 如果存在未关闭的对象，需要在系统退出的时候由内而外依次关闭它们。
 *
 * 当对象被关闭时，需要通知对象的创建者，将已关闭对象从列表中移除。
 *
 * 本接口定义了添加关闭监听的功能，当可关闭对象调用close的时候，当对象关闭完成，
 * 将会通过listener通知该对象的来源。
 *
 */
public interface CloseableSource extends Closeable {

    void setCloseListener(CloseableListener listener);

    boolean closeBySource();

}
