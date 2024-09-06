cmake_minimum_required(VERSION 3.5)

project (DearImGUI)
set (CMAKE_CXX_STANDARD 11)

include_directories(${CMAKE_SOURCE_DIR}/libs/glfw/include ${CMAKE_SOURCE_DIR})
add_library(DearImGUI SHARED cimgui.cpp misc/imgui_stdlib.cpp imgui.cpp imgui_draw.cpp imgui_impl_glfw.cpp imgui_impl_opengl3.cpp imgui_tables.cpp imgui_widgets.cpp imgui_demo.cpp)

if(MSVC)

    add_library(glfw STATIC IMPORTED)
    set_target_properties(glfw PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/glfw/lib-vc2010-64/glfw3dll.lib)
    set_property(TARGET ${PROJECT_NAME} APPEND PROPERTY LINK_FLAGS "/NODEFAULTLIB:msvcrt.lib")
    target_link_libraries(DearImGUI legacy_stdio_definitions.lib)

elseif(APPLE)

    add_library(glfw SHARED IMPORTED)
    set_target_properties(
        glfw PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/glfw/lib-macos-universal/libglfw.3.dylib
    )

endif()

target_link_libraries(DearImGUI glfw)
