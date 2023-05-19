#include <jni.h>

#include "library.h"

#include <iostream>

using namespace std;

JNIEXPORT void JNICALL Java_compiler_jevm_NativeVM_execute(JNIEnv *env, jobject, jbyteArray array){
    jbyte* bufferPtr = env->GetByteArrayElements(array, nullptr);
    jsize lengthOfArray = env->GetArrayLength(array);
    env->ReleaseByteArrayElements(array, bufferPtr, 0);

    cout << "Hello World";
}