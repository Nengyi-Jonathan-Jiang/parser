cmake_minimum_required(VERSION 3.25)
project(native)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

set(CMAKE_CXX_STANDARD 23)

add_library(native SHARED src/library.cpp src/library.h)