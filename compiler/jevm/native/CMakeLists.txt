cmake_minimum_required(VERSION 3.25)
project(native)

set(CMAKE_CXX_STANDARD 23)

add_library(native SHARED src/library.cpp)

# JNI dependencies
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Rename output
set_target_properties(native PROPERTIES PREFIX "")
set_target_properties(native PROPERTIES OUTPUT_NAME "native_vm")
set_target_properties(native PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/lib/)

# Add c++ standard libraries to dll
set(CMAKE_SHARED_LINKER_FLAGS " -static")
target_link_libraries(native -static-libgcc -static-libstdc++)
