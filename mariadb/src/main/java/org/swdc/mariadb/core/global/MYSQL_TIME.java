// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.mariadb.core.global;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.mariadb.core.MyGlobal.*;



/*
  Structure which is used to represent datetime values inside MySQL.

  We assume that values in this structure are normalized, i.e. year <= 9999,
  month <= 12, day <= 31, hour <= 23, hour <= 59, hour <= 59. Many functions
  in server such as my_system_gmt_sec() or make_time() family of functions
  rely on this (actually now usage of make_*() family relies on a bit weaker
  restriction). Also functions that produce MYSQL_TIME as result ensure this.
  There is one exception to this rule though if this structure holds time
  value (time_type == MYSQL_TIMESTAMP_TIME) days and hour member can hold
  bigger values.
*/
@Properties(inherit = org.swdc.mariadb.conf.MyGlobalConfigure.class)
public class MYSQL_TIME extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public MYSQL_TIME() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public MYSQL_TIME(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public MYSQL_TIME(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public MYSQL_TIME position(long position) {
        return (MYSQL_TIME)super.position(position);
    }
    @Override public MYSQL_TIME getPointer(long i) {
        return new MYSQL_TIME((Pointer)this).offsetAddress(i);
    }

  public native @Cast("unsigned int") int year(); public native MYSQL_TIME year(int setter);
  public native @Cast("unsigned int") int month(); public native MYSQL_TIME month(int setter);
  public native @Cast("unsigned int") int day(); public native MYSQL_TIME day(int setter);
  public native @Cast("unsigned int") int hour(); public native MYSQL_TIME hour(int setter);
  public native @Cast("unsigned int") int minute(); public native MYSQL_TIME minute(int setter);
  public native @Cast("unsigned int") int second(); public native MYSQL_TIME second(int setter);
  public native @Cast("unsigned long") long second_part(); public native MYSQL_TIME second_part(long setter);
  public native @Cast("char") byte neg(); public native MYSQL_TIME neg(byte setter);
  public native enum_mysql_timestamp_type time_type(); public native MYSQL_TIME time_type(enum_mysql_timestamp_type setter);
}