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



@Opaque @Properties(inherit = org.swdc.mariadb.conf.MariaDBConfigure.class)
public class MARIADB_CONST_STRING extends Pointer {
    /** Empty constructor. Calls {@code super((Pointer)null)}. */
    public MARIADB_CONST_STRING() { super((Pointer)null); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public MARIADB_CONST_STRING(Pointer p) { super(p); }
}