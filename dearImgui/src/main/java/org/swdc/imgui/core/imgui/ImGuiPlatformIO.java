// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


//-----------------------------------------------------------------------------
// [SECTION] ImGuiPlatformIO + other Platform Dependent Interfaces (ImGuiPlatformMonitor, ImGuiPlatformImeData)
//-----------------------------------------------------------------------------

// [BETA] (Optional) Multi-Viewport Support!
// If you are new to Dear ImGui and trying to integrate it into your engine, you can probably ignore this for now.
//
// This feature allows you to seamlessly drag Dear ImGui windows outside of your application viewport.
// This is achieved by creating new Platform/OS windows on the fly, and rendering into them.
// Dear ImGui manages the viewport structures, and the backend create and maintain one Platform/OS window for each of those viewports.
//
// See Recap:   https://github.com/ocornut/imgui/wiki/Multi-Viewports
// See Glossary https://github.com/ocornut/imgui/wiki/Glossary for details about some of the terminology.
//
// About the coordinates system:
// - When multi-viewports are enabled, all Dear ImGui coordinates become absolute coordinates (same as OS coordinates!)
// - So e.g. ImGui::SetNextWindowPos(ImVec2(0,0)) will position a window relative to your primary monitor!
// - If you want to position windows relative to your main application viewport, use ImGui::GetMainViewport()->Pos as a base position.
//
// Steps to use multi-viewports in your application, when using a default backend from the examples/ folder:
// - Application:  Enable feature with 'io.ConfigFlags |= ImGuiConfigFlags_ViewportsEnable'.
// - Backend:      The backend initialization will setup all necessary ImGuiPlatformIO's functions and update monitors info every frame.
// - Application:  In your main loop, call ImGui::UpdatePlatformWindows(), ImGui::RenderPlatformWindowsDefault() after EndFrame() or Render().
// - Application:  Fix absolute coordinates used in ImGui::SetWindowPos() or ImGui::SetNextWindowPos() calls.
//
// Steps to use multi-viewports in your application, when using a custom backend:
// - Important:    THIS IS NOT EASY TO DO and comes with many subtleties not described here!
//                 It's also an experimental feature, so some of the requirements may evolve.
//                 Consider using default backends if you can. Either way, carefully follow and refer to examples/ backends for details.
// - Application:  Enable feature with 'io.ConfigFlags |= ImGuiConfigFlags_ViewportsEnable'.
// - Backend:      Hook ImGuiPlatformIO's Platform_* and Renderer_* callbacks (see below).
//                 Set 'io.BackendFlags |= ImGuiBackendFlags_PlatformHasViewports' and 'io.BackendFlags |= ImGuiBackendFlags_PlatformHasViewports'.
//                 Update ImGuiPlatformIO's Monitors list every frame.
//                 Update MousePos every frame, in absolute coordinates.
// - Application:  In your main loop, call ImGui::UpdatePlatformWindows(), ImGui::RenderPlatformWindowsDefault() after EndFrame() or Render().
//                 You may skip calling RenderPlatformWindowsDefault() if its API is not convenient for your needs. Read comments below.
// - Application:  Fix absolute coordinates used in ImGui::SetWindowPos() or ImGui::SetNextWindowPos() calls.
//
// About ImGui::RenderPlatformWindowsDefault():
// - This function is a mostly a _helper_ for the common-most cases, and to facilitate using default backends.
// - You can check its simple source code to understand what it does.
//   It basically iterates secondary viewports and call 4 functions that are setup in ImGuiPlatformIO, if available:
//     Platform_RenderWindow(), Renderer_RenderWindow(), Platform_SwapBuffers(), Renderer_SwapBuffers()
//   Those functions pointers exists only for the benefit of RenderPlatformWindowsDefault().
// - If you have very specific rendering needs (e.g. flipping multiple swap-chain simultaneously, unusual sync/threading issues, etc.),
//   you may be tempted to ignore RenderPlatformWindowsDefault() and write customized code to perform your renderingg.
//   You may decide to setup the platform_io's *RenderWindow and *SwapBuffers pointers and call your functions through those pointers,
//   or you may decide to never setup those pointers and call your code directly. They are a convenience, not an obligatory interface.
//-----------------------------------------------------------------------------

// Access via ImGui::GetPlatformIO()
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiPlatformIO extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public ImGuiPlatformIO() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public ImGuiPlatformIO(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public ImGuiPlatformIO(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public ImGuiPlatformIO position(long position) {
        return (ImGuiPlatformIO)super.position(position);
    }
    @Override public ImGuiPlatformIO getPointer(long i) {
        return new ImGuiPlatformIO((Pointer)this).offsetAddress(i);
    }

    //------------------------------------------------------------------
    // Input - Interface with OS/backends (basic)
    //------------------------------------------------------------------

    // Optional: Access OS clipboard
    // (default to use native Win32 clipboard on Windows, otherwise uses a private clipboard. Override to access OS clipboard on other architectures)
    public static class Platform_GetClipboardTextFn_ImGuiContext extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetClipboardTextFn_ImGuiContext(Pointer p) { super(p); }
        protected Platform_GetClipboardTextFn_ImGuiContext() { allocate(); }
        private native void allocate();
        public native @Cast("const char*") BytePointer call(ImGuiContext ctx);
    }
    public native Platform_GetClipboardTextFn_ImGuiContext Platform_GetClipboardTextFn(); public native ImGuiPlatformIO Platform_GetClipboardTextFn(Platform_GetClipboardTextFn_ImGuiContext setter);
    public static class Platform_SetClipboardTextFn_ImGuiContext_BytePointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetClipboardTextFn_ImGuiContext_BytePointer(Pointer p) { super(p); }
        protected Platform_SetClipboardTextFn_ImGuiContext_BytePointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiContext ctx, @Cast("const char*") BytePointer text);
    }
    public native Platform_SetClipboardTextFn_ImGuiContext_BytePointer Platform_SetClipboardTextFn(); public native ImGuiPlatformIO Platform_SetClipboardTextFn(Platform_SetClipboardTextFn_ImGuiContext_BytePointer setter);
    public native Pointer Platform_ClipboardUserData(); public native ImGuiPlatformIO Platform_ClipboardUserData(Pointer setter);

    // Optional: Open link/folder/file in OS Shell
    // (default to use ShellExecuteA() on Windows, system() on Linux/Mac)
    public static class Platform_OpenInShellFn_ImGuiContext_BytePointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_OpenInShellFn_ImGuiContext_BytePointer(Pointer p) { super(p); }
        protected Platform_OpenInShellFn_ImGuiContext_BytePointer() { allocate(); }
        private native void allocate();
        public native @Cast("bool") boolean call(ImGuiContext ctx, @Cast("const char*") BytePointer path);
    }
    public native Platform_OpenInShellFn_ImGuiContext_BytePointer Platform_OpenInShellFn(); public native ImGuiPlatformIO Platform_OpenInShellFn(Platform_OpenInShellFn_ImGuiContext_BytePointer setter);
    public native Pointer Platform_OpenInShellUserData(); public native ImGuiPlatformIO Platform_OpenInShellUserData(Pointer setter);

    // Optional: Notify OS Input Method Editor of the screen position of your cursor for text input position (e.g. when using Japanese/Chinese IME on Windows)
    // (default to use native imm32 api on Windows)
    public static class Platform_SetImeDataFn_ImGuiContext_ImGuiViewport_ImGuiPlatformImeData extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetImeDataFn_ImGuiContext_ImGuiViewport_ImGuiPlatformImeData(Pointer p) { super(p); }
        protected Platform_SetImeDataFn_ImGuiContext_ImGuiViewport_ImGuiPlatformImeData() { allocate(); }
        private native void allocate();
        public native void call(ImGuiContext ctx, ImGuiViewport viewport, ImGuiPlatformImeData data);
    }
    public native Platform_SetImeDataFn_ImGuiContext_ImGuiViewport_ImGuiPlatformImeData Platform_SetImeDataFn(); public native ImGuiPlatformIO Platform_SetImeDataFn(Platform_SetImeDataFn_ImGuiContext_ImGuiViewport_ImGuiPlatformImeData setter);
    public native Pointer Platform_ImeUserData(); public native ImGuiPlatformIO Platform_ImeUserData(Pointer setter);
    //void      (*SetPlatformImeDataFn)(ImGuiViewport* viewport, ImGuiPlatformImeData* data); // [Renamed to platform_io.PlatformSetImeDataFn in 1.91.1]

    // Optional: Platform locale
    // [Experimental] Configure decimal point e.g. '.' or ',' useful for some languages (e.g. German), generally pulled from *localeconv()->decimal_point
    public native @Cast("ImWchar") int Platform_LocaleDecimalPoint(); public native ImGuiPlatformIO Platform_LocaleDecimalPoint(int setter);  // '.'

    //------------------------------------------------------------------
    // Input - Interface with OS/backends (Multi-Viewport support!)
    //------------------------------------------------------------------

    // For reference, the second column shows which function are generally calling the Platform Functions:
    //   N = ImGui::NewFrame()                        ~ beginning of the dear imgui frame: read info from platform/OS windows (latest size/position)
    //   F = ImGui::Begin(), ImGui::EndFrame()        ~ during the dear imgui frame
    //   U = ImGui::UpdatePlatformWindows()           ~ after the dear imgui frame: create and update all platform/OS windows
    //   R = ImGui::RenderPlatformWindowsDefault()    ~ render
    //   D = ImGui::DestroyPlatformWindows()          ~ shutdown
    // The general idea is that NewFrame() we will read the current Platform/OS state, and UpdatePlatformWindows() will write to it.

    // The handlers are designed so we can mix and match two imgui_impl_xxxx files, one Platform backend and one Renderer backend.
    // Custom engine backends will often provide both Platform and Renderer interfaces together and so may not need to use all functions.
    // Platform functions are typically called _before_ their Renderer counterpart, apart from Destroy which are called the other way.

    // Platform Backend functions (e.g. Win32, GLFW, SDL) ------------------- Called by -----
    public static class Platform_CreateWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_CreateWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_CreateWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_CreateWindow_ImGuiViewport Platform_CreateWindow(); public native ImGuiPlatformIO Platform_CreateWindow(Platform_CreateWindow_ImGuiViewport setter);                                                      // . . U . .  // Create a new platform window for the given viewport
    public static class Platform_DestroyWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_DestroyWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_DestroyWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_DestroyWindow_ImGuiViewport Platform_DestroyWindow(); public native ImGuiPlatformIO Platform_DestroyWindow(Platform_DestroyWindow_ImGuiViewport setter);                                                     // N . U . D  //
    public static class Platform_ShowWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_ShowWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_ShowWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_ShowWindow_ImGuiViewport Platform_ShowWindow(); public native ImGuiPlatformIO Platform_ShowWindow(Platform_ShowWindow_ImGuiViewport setter);                                                        // . . U . .  // Newly created windows are initially hidden so SetWindowPos/Size/Title can be called on them before showing the window
    public static class Platform_SetWindowPos_ImGuiViewport_ImVec2 extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetWindowPos_ImGuiViewport_ImVec2(Pointer p) { super(p); }
        protected Platform_SetWindowPos_ImGuiViewport_ImVec2() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, @ByVal ImVec2 pos);
    }
    public native Platform_SetWindowPos_ImGuiViewport_ImVec2 Platform_SetWindowPos(); public native ImGuiPlatformIO Platform_SetWindowPos(Platform_SetWindowPos_ImGuiViewport_ImVec2 setter);                                          // . . U . .  // Set platform window position (given the upper-left corner of client area)
    public static class Platform_GetWindowPos_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowPos_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowPos_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native @ByVal ImVec2 call(ImGuiViewport vp);
    }
    public native Platform_GetWindowPos_ImGuiViewport Platform_GetWindowPos(); public native ImGuiPlatformIO Platform_GetWindowPos(Platform_GetWindowPos_ImGuiViewport setter);                                                    // N . . . .  //
    public static class Platform_SetWindowSize_ImGuiViewport_ImVec2 extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetWindowSize_ImGuiViewport_ImVec2(Pointer p) { super(p); }
        protected Platform_SetWindowSize_ImGuiViewport_ImVec2() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, @ByVal ImVec2 size);
    }
    public native Platform_SetWindowSize_ImGuiViewport_ImVec2 Platform_SetWindowSize(); public native ImGuiPlatformIO Platform_SetWindowSize(Platform_SetWindowSize_ImGuiViewport_ImVec2 setter);                                        // . . U . .  // Set platform window client area size (ignoring OS decorations such as OS title bar etc.)
    public static class Platform_GetWindowSize_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowSize_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowSize_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native @ByVal ImVec2 call(ImGuiViewport vp);
    }
    public native Platform_GetWindowSize_ImGuiViewport Platform_GetWindowSize(); public native ImGuiPlatformIO Platform_GetWindowSize(Platform_GetWindowSize_ImGuiViewport setter);                                                   // N . . . .  // Get platform window client area size
    public static class Platform_SetWindowFocus_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetWindowFocus_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_SetWindowFocus_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_SetWindowFocus_ImGuiViewport Platform_SetWindowFocus(); public native ImGuiPlatformIO Platform_SetWindowFocus(Platform_SetWindowFocus_ImGuiViewport setter);                                                    // N . . . .  // Move window to front and set input focus
    public static class Platform_GetWindowFocus_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowFocus_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowFocus_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native @Cast("bool") boolean call(ImGuiViewport vp);
    }
    public native Platform_GetWindowFocus_ImGuiViewport Platform_GetWindowFocus(); public native ImGuiPlatformIO Platform_GetWindowFocus(Platform_GetWindowFocus_ImGuiViewport setter);                                                    // . . U . .  //
    public static class Platform_GetWindowMinimized_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowMinimized_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowMinimized_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native @Cast("bool") boolean call(ImGuiViewport vp);
    }
    public native Platform_GetWindowMinimized_ImGuiViewport Platform_GetWindowMinimized(); public native ImGuiPlatformIO Platform_GetWindowMinimized(Platform_GetWindowMinimized_ImGuiViewport setter);                                                // N . . . .  // Get platform window minimized state. When minimized, we generally won't attempt to get/set size and contents will be culled more easily
    public static class Platform_SetWindowTitle_ImGuiViewport_BytePointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetWindowTitle_ImGuiViewport_BytePointer(Pointer p) { super(p); }
        protected Platform_SetWindowTitle_ImGuiViewport_BytePointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, @Cast("const char*") BytePointer str);
    }
    public native Platform_SetWindowTitle_ImGuiViewport_BytePointer Platform_SetWindowTitle(); public native ImGuiPlatformIO Platform_SetWindowTitle(Platform_SetWindowTitle_ImGuiViewport_BytePointer setter);                                   // . . U . .  // Set platform window title (given an UTF-8 string)
    public static class Platform_SetWindowAlpha_ImGuiViewport_float extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SetWindowAlpha_ImGuiViewport_float(Pointer p) { super(p); }
        protected Platform_SetWindowAlpha_ImGuiViewport_float() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, float alpha);
    }
    public native Platform_SetWindowAlpha_ImGuiViewport_float Platform_SetWindowAlpha(); public native ImGuiPlatformIO Platform_SetWindowAlpha(Platform_SetWindowAlpha_ImGuiViewport_float setter);                                       // . . U . .  // (Optional) Setup global transparency (not per-pixel transparency)
    public static class Platform_UpdateWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_UpdateWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_UpdateWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_UpdateWindow_ImGuiViewport Platform_UpdateWindow(); public native ImGuiPlatformIO Platform_UpdateWindow(Platform_UpdateWindow_ImGuiViewport setter);                                                      // . . U . .  // (Optional) Called by UpdatePlatformWindows(). Optional hook to allow the platform backend from doing general book-keeping every frame.
    public static class Platform_RenderWindow_ImGuiViewport_Pointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_RenderWindow_ImGuiViewport_Pointer(Pointer p) { super(p); }
        protected Platform_RenderWindow_ImGuiViewport_Pointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, Pointer render_arg);
    }
    public native Platform_RenderWindow_ImGuiViewport_Pointer Platform_RenderWindow(); public native ImGuiPlatformIO Platform_RenderWindow(Platform_RenderWindow_ImGuiViewport_Pointer setter);                                    // . . . R .  // (Optional) Main rendering (platform side! This is often unused, or just setting a "current" context for OpenGL bindings). 'render_arg' is the value passed to RenderPlatformWindowsDefault().
    public static class Platform_SwapBuffers_ImGuiViewport_Pointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_SwapBuffers_ImGuiViewport_Pointer(Pointer p) { super(p); }
        protected Platform_SwapBuffers_ImGuiViewport_Pointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, Pointer render_arg);
    }
    public native Platform_SwapBuffers_ImGuiViewport_Pointer Platform_SwapBuffers(); public native ImGuiPlatformIO Platform_SwapBuffers(Platform_SwapBuffers_ImGuiViewport_Pointer setter);                                     // . . . R .  // (Optional) Call Present/SwapBuffers (platform side! This is often unused!). 'render_arg' is the value passed to RenderPlatformWindowsDefault().
    public static class Platform_GetWindowDpiScale_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowDpiScale_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowDpiScale_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native float call(ImGuiViewport vp);
    }
    public native Platform_GetWindowDpiScale_ImGuiViewport Platform_GetWindowDpiScale(); public native ImGuiPlatformIO Platform_GetWindowDpiScale(Platform_GetWindowDpiScale_ImGuiViewport setter);                                                // N . . . .  // (Optional) [BETA] FIXME-DPI: DPI handling: Return DPI scale for this viewport. 1.0f = 96 DPI.
    public static class Platform_OnChangedViewport_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_OnChangedViewport_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_OnChangedViewport_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Platform_OnChangedViewport_ImGuiViewport Platform_OnChangedViewport(); public native ImGuiPlatformIO Platform_OnChangedViewport(Platform_OnChangedViewport_ImGuiViewport setter);                                                 // . F . . .  // (Optional) [BETA] FIXME-DPI: DPI handling: Called during Begin() every time the viewport we are outputting into changes, so backend has a chance to swap fonts to adjust style.
    public static class Platform_GetWindowWorkAreaInsets_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_GetWindowWorkAreaInsets_ImGuiViewport(Pointer p) { super(p); }
        protected Platform_GetWindowWorkAreaInsets_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native @ByVal ImVec4 call(ImGuiViewport vp);
    }
    public native Platform_GetWindowWorkAreaInsets_ImGuiViewport Platform_GetWindowWorkAreaInsets(); public native ImGuiPlatformIO Platform_GetWindowWorkAreaInsets(Platform_GetWindowWorkAreaInsets_ImGuiViewport setter);                                         // N . . . .  // (Optional) [BETA] Get initial work area inset for the viewport (won't be covered by main menu bar, dockspace over viewport etc.). Default to (0,0),(0,0). 'safeAreaInsets' in iOS land, 'DisplayCutout' in Android land.
    public static class Platform_CreateVkSurface_ImGuiViewport_long_Pointer_LongPointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Platform_CreateVkSurface_ImGuiViewport_long_Pointer_LongPointer(Pointer p) { super(p); }
        protected Platform_CreateVkSurface_ImGuiViewport_long_Pointer_LongPointer() { allocate(); }
        private native void allocate();
        public native int call(ImGuiViewport vp, @Cast("ImU64") long vk_inst, @Const Pointer vk_allocators, @Cast("ImU64*") LongPointer out_vk_surface);
    }
    public native Platform_CreateVkSurface_ImGuiViewport_long_Pointer_LongPointer Platform_CreateVkSurface(); public native ImGuiPlatformIO Platform_CreateVkSurface(Platform_CreateVkSurface_ImGuiViewport_long_Pointer_LongPointer setter); // (Optional) For a Vulkan Renderer to call into Platform code (since the surface creation needs to tie them both).

    // Renderer Backend functions (e.g. DirectX, OpenGL, Vulkan) ------------ Called by -----
    public static class Renderer_CreateWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Renderer_CreateWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Renderer_CreateWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Renderer_CreateWindow_ImGuiViewport Renderer_CreateWindow(); public native ImGuiPlatformIO Renderer_CreateWindow(Renderer_CreateWindow_ImGuiViewport setter);                                                      // . . U . .  // Create swap chain, frame buffers etc. (called after Platform_CreateWindow)
    public static class Renderer_DestroyWindow_ImGuiViewport extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Renderer_DestroyWindow_ImGuiViewport(Pointer p) { super(p); }
        protected Renderer_DestroyWindow_ImGuiViewport() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp);
    }
    public native Renderer_DestroyWindow_ImGuiViewport Renderer_DestroyWindow(); public native ImGuiPlatformIO Renderer_DestroyWindow(Renderer_DestroyWindow_ImGuiViewport setter);                                                     // N . U . D  // Destroy swap chain, frame buffers etc. (called before Platform_DestroyWindow)
    public static class Renderer_SetWindowSize_ImGuiViewport_ImVec2 extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Renderer_SetWindowSize_ImGuiViewport_ImVec2(Pointer p) { super(p); }
        protected Renderer_SetWindowSize_ImGuiViewport_ImVec2() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, @ByVal ImVec2 size);
    }
    public native Renderer_SetWindowSize_ImGuiViewport_ImVec2 Renderer_SetWindowSize(); public native ImGuiPlatformIO Renderer_SetWindowSize(Renderer_SetWindowSize_ImGuiViewport_ImVec2 setter);                                        // . . U . .  // Resize swap chain, frame buffers etc. (called after Platform_SetWindowSize)
    public static class Renderer_RenderWindow_ImGuiViewport_Pointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Renderer_RenderWindow_ImGuiViewport_Pointer(Pointer p) { super(p); }
        protected Renderer_RenderWindow_ImGuiViewport_Pointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, Pointer render_arg);
    }
    public native Renderer_RenderWindow_ImGuiViewport_Pointer Renderer_RenderWindow(); public native ImGuiPlatformIO Renderer_RenderWindow(Renderer_RenderWindow_ImGuiViewport_Pointer setter);                                    // . . . R .  // (Optional) Clear framebuffer, setup render target, then render the viewport->DrawData. 'render_arg' is the value passed to RenderPlatformWindowsDefault().
    public static class Renderer_SwapBuffers_ImGuiViewport_Pointer extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    Renderer_SwapBuffers_ImGuiViewport_Pointer(Pointer p) { super(p); }
        protected Renderer_SwapBuffers_ImGuiViewport_Pointer() { allocate(); }
        private native void allocate();
        public native void call(ImGuiViewport vp, Pointer render_arg);
    }
    public native Renderer_SwapBuffers_ImGuiViewport_Pointer Renderer_SwapBuffers(); public native ImGuiPlatformIO Renderer_SwapBuffers(Renderer_SwapBuffers_ImGuiViewport_Pointer setter);                                     // . . . R .  // (Optional) Call Present/SwapBuffers. 'render_arg' is the value passed to RenderPlatformWindowsDefault().

    // (Optional) Monitor list
    // - Updated by: app/backend. Update every frame to dynamically support changing monitor or DPI configuration.
    // - Used by: dear imgui to query DPI info, clamp popups/tooltips within same monitor and not have them straddle monitors.
    public native @ByRef ImVector_ImGuiPlatformMonitor Monitors(); public native ImGuiPlatformIO Monitors(ImVector_ImGuiPlatformMonitor setter);

    //------------------------------------------------------------------
    // Output - List of viewports to render into platform windows
    //------------------------------------------------------------------

    // Viewports list (the list is updated by calling ImGui::EndFrame or ImGui::Render)
    // (in the future we will attempt to organize this feature to remove the need for a "main viewport")
    public native @ByRef ImVector_ImGuiViewportPtr Viewports(); public native ImGuiPlatformIO Viewports(ImVector_ImGuiViewportPtr setter);                    // Main viewports, followed by all secondary viewports.
}