#pragma once

extern "C" {
    JNIEXPORT jstring JNICALL Java_compiler_jevm_NativeVM_execute(JNIEnv *env, jobject obj, jbyteArray array);
}