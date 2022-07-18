#ifndef HAVE_JE_GENERATOR_OBJ_H
#define HAVE_JE_GENERATOR_OBJ_H

#include <jni.h>

extern "C" {
#include "php.h"
}

typedef struct {
    jvm_obj* jvm_obj;
    jclass jegenerator_class;
    jobject jegenerator_obj;
    zend_object std;
} jegenerator_obj;

#endif
