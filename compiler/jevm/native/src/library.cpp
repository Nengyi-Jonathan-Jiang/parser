#include <jni.h>

#include "library.h"

#include <iostream>

using namespace std;

JNIEXPORT jstring JNICALL Java_compiler_jevm_NativeVM_helloWorldBy(JNIEnv *env, jobject obj, jbyteArray array){
    const jbyte* bufferPtr = (*env)->GetByteArrayElements(env, array, NULL);
    jsize lengthOfArray = (*env)->GetArrayLength(env, array);
    env->ReleaseByteArrayElements(env, array, bufferPtr, 0);
    
    const char *cpp_message = "Message sent by JNI: ";

    char result[100];   // array to hold the result.
    strcpy(result,cpp_message); // copy cpp_message into the result.
    strcat(result,nativeString); // concat nativeString into the cpp_message.

    return charToJstring(env, result);
}