#include "JEChunk.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JEChunk_arginfo.h"
#include "JvmLoaderObj.h"
#include "JEChunkObj.h"

#include <jni.h>
#include <iostream>
#include <cstdio>
#include <cstring>
using namespace std;

zend_class_entry* jechunk_class_entry;
static zend_object_handlers jechunk_handlers;

static zend_object* jechunk_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<jechunk_obj>(class_type, &jechunk_handlers);
    return &object->std;
}

static void jechunk_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

#define JECHUNK_METHOD(name) PHP_METHOD(tedo0627_jegenerator_extension_JEChunk, name)

JECHUNK_METHOD(__construct) {
}

JECHUNK_METHOD(getBlocks) {
    auto object = fetch_from_zend_object<jechunk_obj>(Z_OBJ_P(getThis()));
    JNIEnv *env = object->jvm_obj->env;
    jmethodID mid = env->GetMethodID(object->jechunk_class, "getBlocks", "()[I");
    jintarray obj = (jintarray) env->CallObjectMethod(object->jechunk_obj, mid, 0, 0);
}

void register_jechunk_class() {
    memcpy(&jechunk_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    jechunk_handlers.offset = XtOffsetOf(jechunk_obj, std);
    jechunk_handlers.free_obj = jechunk_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "tedo0627\\jegenerator\\extension\\JEChunk", tedo0627_jegenerator_extension_jechunk_methods);
    ce.create_object = jechunk_new;
    jechunk_class_entry = zend_register_internal_class(&ce);
}