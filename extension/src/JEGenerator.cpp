#include "JEGenerator.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JEGenerator_arginfo.h"
#include "JvmLoaderObj.h"
#include "JEGeneratorObj.h"
#include "JEChunkObj.h"
#include "JEChunk.h"

#include <jni.h>
#include <iostream>
#include <cstdio>
#include <cstring>
using namespace std;

zend_class_entry* jegenerator_class_entry;
static zend_object_handlers jegenerator_handlers;

static zend_object* jegenerator_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<jegenerator_obj>(class_type, &jegenerator_handlers);
    return &object->std;
}

static void jegenerator_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

#define JEGENERATOR_METHOD(name) PHP_METHOD(tedo0627_jegenerator_extension_JEGenerator, name)

JEGENERATOR_METHOD(__construct) {
}

JEGENERATOR_METHOD(generateChunk) {
    zend_long x;
    zend_long z;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 2, 2)
        Z_PARAM_LONG(x)
        Z_PARAM_LONG(z)
    ZEND_PARSE_PARAMETERS_END();

    JavaVM* jvm;
    jsize ct;
    JNI_GetCreatedJavaVMs(&jvm, 1, &ct);
    JNIEnv* env;
    jvm->GetEnv((void**) &env, JNI_VERSION_1_6);
    jvm->AttachCurrentThread((void**) &env, NULL);

    auto object = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(getThis()));
    jmethodID mid = env->GetMethodID(object->jegenerator_class, "generateChunk", "(II)Ljp/tedo0627/jeloader/JEChunk;");
    jobject jechunk = env->CallObjectMethod(object->jegenerator_obj, mid, (int) x, (int) z);
    object_init_ex(return_value, jechunk_class_entry);
    jechunk_obj* jechunk_o = fetch_from_zend_object<jechunk_obj>(Z_OBJ_P(return_value));
    jechunk_o->jvm_obj = object->jvm_obj;
    jechunk_o->jechunk_class = env->FindClass("jp/tedo0627/jeloader/JEChunk");
    jechunk_o->jechunk_obj = jechunk;
}

void register_jegenerator_class() {
    memcpy(&jegenerator_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    jegenerator_handlers.offset = XtOffsetOf(jegenerator_obj, std);
    jegenerator_handlers.free_obj = jegenerator_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "tedo0627\\jegenerator\\extension\\JEGenerator", tedo0627_jegenerator_extension_jegenerator_methods);
    ce.create_object = jegenerator_new;
    jegenerator_class_entry = zend_register_internal_class(&ce);
}