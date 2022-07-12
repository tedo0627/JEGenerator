#ifndef HAVE_JVM_LOADER_OBJ_H
#define HAVE_JVM_LOADER_OBJ_H

#include <jni.h>

extern "C" {
#include "php.h"
}

typedef struct {
    zend_string* path;
    JavaVM* jvm;
    JNIEnv* env;
    zend_object std;
} jvm_obj;

#endif
