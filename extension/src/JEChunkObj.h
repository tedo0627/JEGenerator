#ifndef HAVE_JE_CHUNK_OBJ_H
#define HAVE_JE_CHUNK_OBJ_H

#include <jni.h>

extern "C" {
#include "php.h"
}

typedef struct {
    jobject jechunk_obj;
    jclass jechunk_class;
    jmethodID get_blocks_method;
    zend_object std;
} jechunk_obj;

#endif
