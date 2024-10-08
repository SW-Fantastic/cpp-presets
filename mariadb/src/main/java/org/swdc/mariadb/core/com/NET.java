// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.mariadb.core.com;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.mariadb.core.global.*;
import static org.swdc.mariadb.core.MyGlobal.*;

import static org.swdc.mariadb.core.MyCom.*;
	/* Default width for blob */

@Properties(inherit = org.swdc.mariadb.conf.MyComConfigure.class)
public class NET extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public NET() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public NET(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public NET(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public NET position(long position) {
        return (NET)super.position(position);
    }
    @Override public NET getPointer(long i) {
        return new NET((Pointer)this).offsetAddress(i);
    }

// #if !defined(CHECK_EMBEDDED_DIFFERENCES) || !defined(EMBEDDED_LIBRARY)
  public native Vio vio(); public native NET vio(Vio setter);
  public native @Cast("unsigned char*") BytePointer buff(); public native NET buff(BytePointer setter);
  public native @Cast("unsigned char*") BytePointer buff_end(); public native NET buff_end(BytePointer setter);
  public native @Cast("unsigned char*") BytePointer write_pos(); public native NET write_pos(BytePointer setter);
  public native @Cast("unsigned char*") BytePointer read_pos(); public native NET read_pos(BytePointer setter);					/* For Perl DBI/dbd */
  /*
    The following variable is set if we are doing several queries in one
    command ( as in LOAD TABLE ... FROM MASTER ),
    and do not want to confuse the client with OK at the wrong time
  */
  public native @Cast("unsigned long") long remain_in_buf(); public native NET remain_in_buf(long setter);
  public native @Cast("unsigned long") long length(); public native NET length(long setter);
  public native @Cast("unsigned long") long buf_length(); public native NET buf_length(long setter);
  public native @Cast("unsigned long") long where_b(); public native NET where_b(long setter);
  public native @Cast("unsigned long") long max_packet(); public native NET max_packet(long setter);
  public native @Cast("unsigned long") long max_packet_size(); public native NET max_packet_size(long setter);
  public native @Cast("unsigned int") int pkt_nr(); public native NET pkt_nr(int setter);
  public native @Cast("unsigned int") int compress_pkt_nr(); public native NET compress_pkt_nr(int setter);
  public native @Cast("unsigned int") int write_timeout(); public native NET write_timeout(int setter);
  public native @Cast("unsigned int") int read_timeout(); public native NET read_timeout(int setter);
  public native @Cast("unsigned int") int retry_count(); public native NET retry_count(int setter);
  public native int fcntl(); public native NET fcntl(int setter);
  public native @Cast("unsigned int*") IntPointer return_status(); public native NET return_status(IntPointer setter);
  public native @Cast("unsigned char") byte reading_or_writing(); public native NET reading_or_writing(byte setter);
  public native @Cast("char") byte save_char(); public native NET save_char(byte setter);
  public native @Cast("char") byte net_skip_rest_factor(); public native NET net_skip_rest_factor(byte setter);
  public native @Cast("char") byte thread_specific_malloc(); public native NET thread_specific_malloc(byte setter);
  public native @Cast("unsigned char") byte compress(); public native NET compress(byte setter);
  public native @Cast("char") byte pkt_nr_can_be_reset(); public native NET pkt_nr_can_be_reset(byte setter);
  public native @Cast("char") byte using_proxy_protocol(); public native NET using_proxy_protocol(byte setter);
  /*
    Pointer to query object in query cache, do not equal NULL (0) for
    queries in cache that have not stored its results yet
  */
// #endif
  public native Pointer thd(); public native NET thd(Pointer setter); 	   /* Used by MariaDB server to avoid calling current_thd */
  public native @Cast("unsigned int") int last_errno(); public native NET last_errno(int setter);
  public native @Cast("unsigned char") byte error(); public native NET error(byte setter); 
  public native @Cast("char") byte unused4(); public native NET unused4(byte setter); /* Please remove with the next incompatible ABI change. */
  public native @Cast("char") byte unused5(); public native NET unused5(byte setter); /* Please remove with the next incompatible ABI change. */
  /** Client library error message buffer. Actually belongs to struct MYSQL. */
  public native @Cast("char") byte last_error(int i); public native NET last_error(int i, byte setter);
  @MemberGetter public native @Cast("char*") BytePointer last_error();
  /** Client library sqlstate buffer. Set along with the error message. */
  public native @Cast("char") byte sqlstate(int i); public native NET sqlstate(int i, byte setter);
  @MemberGetter public native @Cast("char*") BytePointer sqlstate();
  public native Pointer extension(); public native NET extension(Pointer setter);
}
