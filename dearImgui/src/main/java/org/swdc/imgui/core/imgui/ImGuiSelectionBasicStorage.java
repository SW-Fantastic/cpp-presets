// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


// Optional helper to store multi-selection state + apply multi-selection requests.
// - Used by our demos and provided as a convenience to easily implement basic multi-selection.
// - Iterate selection with 'void* it = NULL; ImGuiID id; while (selection.GetNextSelectedItem(&it, &id)) { ... }'
//   Or you can check 'if (Contains(id)) { ... }' for each possible object if their number is not too high to iterate.
// - USING THIS IS NOT MANDATORY. This is only a helper and not a required API.
// To store a multi-selection, in your application you could:
// - Use this helper as a convenience. We use our simple key->value ImGuiStorage as a std::set<ImGuiID> replacement.
// - Use your own external storage: e.g. std::set<MyObjectId>, std::vector<MyObjectId>, interval trees, intrusively stored selection etc.
// In ImGuiSelectionBasicStorage we:
// - always use indices in the multi-selection API (passed to SetNextItemSelectionUserData(), retrieved in ImGuiMultiSelectIO)
// - use the AdapterIndexToStorageId() indirection layer to abstract how persistent selection data is derived from an index.
// - use decently optimized logic to allow queries and insertion of very large selection sets.
// - do not preserve selection order.
// Many combinations are possible depending on how you prefer to store your items and how you prefer to store your selection.
// Large applications are likely to eventually want to get rid of this indirection layer and do their own thing.
// See https://github.com/ocornut/imgui/wiki/Multi-Select for details and pseudo-code using this helper.
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiSelectionBasicStorage extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public ImGuiSelectionBasicStorage() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public ImGuiSelectionBasicStorage(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public ImGuiSelectionBasicStorage(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public ImGuiSelectionBasicStorage position(long position) {
        return (ImGuiSelectionBasicStorage)super.position(position);
    }
    @Override public ImGuiSelectionBasicStorage getPointer(long i) {
        return new ImGuiSelectionBasicStorage((Pointer)this).offsetAddress(i);
    }

    // Members
    public native int Size(); public native ImGuiSelectionBasicStorage Size(int setter);             //          // Number of selected items, maintained by this helper.
    public native @Cast("bool") boolean PreserveOrder(); public native ImGuiSelectionBasicStorage PreserveOrder(boolean setter);    // = false  // GetNextSelectedItem() will return ordered selection (currently implemented by two additional sorts of selection. Could be improved)
    public native Pointer UserData(); public native ImGuiSelectionBasicStorage UserData(Pointer setter);         // = NULL   // User data for use by adapter function        // e.g. selection.UserData = (void*)my_items;
    public static class AdapterIndexToStorageId_ImGuiSelectionBasicStorage_int extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    AdapterIndexToStorageId_ImGuiSelectionBasicStorage_int(Pointer p) { super(p); }
        protected AdapterIndexToStorageId_ImGuiSelectionBasicStorage_int() { allocate(); }
        private native void allocate();
        public native @Cast("ImGuiID") int call(ImGuiSelectionBasicStorage self, int idx);
    }
    public native AdapterIndexToStorageId_ImGuiSelectionBasicStorage_int AdapterIndexToStorageId(); public native ImGuiSelectionBasicStorage AdapterIndexToStorageId(AdapterIndexToStorageId_ImGuiSelectionBasicStorage_int setter); // e.g. selection.AdapterIndexToStorageId = [](ImGuiSelectionBasicStorage* self, int idx) { return ((MyItems**)self->UserData)[idx]->ID; };
    public native int _SelectionOrder(); public native ImGuiSelectionBasicStorage _SelectionOrder(int setter);  // [Internal] Increasing counter to store selection order
    public native @ByRef ImGuiStorage _Storage(); public native ImGuiSelectionBasicStorage _Storage(ImGuiStorage setter);         // [Internal] Selection set. Think of this as similar to e.g. std::set<ImGuiID>. Prefer not accessing directly: iterate with GetNextSelectedItem().
}
