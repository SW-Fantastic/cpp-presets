package org.swdc.dear.layout;

public enum Justify {

    /**
     * 从头到尾依次排列，
     * space属性应当生效
     */
    START,
    /**
     * 从尾到头依次排列，
     * Space属性应当生效。
     */
    END,

    /**
     * 居中排列，
     * Space属性应当生效
     */
    CENTER,

    /**
     * 间距均等，首尾不留空隙，
     * Space属性不生效
     */
    BETWEEN,


    /**
     * 间距均等，首尾留有空隙，
     * Space属性不生效。
     */
    SPACE

}
