#ifndef HAVE_JE_GENERATOR_OBJ_H
#define HAVE_JE_GENERATOR_OBJ_H

#include <jni.h>

extern "C" {
#include "php.h"
}

typedef struct {
    jobject jegenerator_obj;
    jclass jegenerator_class;
    jmethodID generate_chunk_method;
    zend_object std;
} jegenerator_obj;

#endif
