// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.mariadb.core.mysql;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.mariadb.core.global.*;
import static org.swdc.mariadb.core.MyGlobal.*;
import org.swdc.mariadb.core.com.*;
import static org.swdc.mariadb.core.MyCom.*;

import static org.swdc.mariadb.core.MariaDB.*;

@Properties(inherit = org.swdc.mariadb.conf.MariaDBConfigure.class)
public class Local_infile_end_Pointer extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    Local_infile_end_Pointer(Pointer p) { super(p); }
    protected Local_infile_end_Pointer() { allocate(); }
    private native void allocate();
    public native void call(Pointer arg0);
}
