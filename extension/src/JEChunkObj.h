#ifndef HAVE_JE_CHUNK_OBJ_H
#define HAVE_JE_CHUNK_OBJ_H

#include <jni.h>

extern "C" {
#include "php.h"
}

typedef struct {
    jvm_obj* jvm_obj;
    jclass jechunk_class;
    jobject jechunk_obj;
    zend_object std;
} jechunk_obj;

#endif
