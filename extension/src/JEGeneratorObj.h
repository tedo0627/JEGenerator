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
    jmethodID populate_chunk_method;
    jmethodID get_index_method;
    jmethodID get_value_method;
    zend_object std;
} jegenerator_obj;

#endif
