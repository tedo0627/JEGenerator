#include "JEGenerator.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JEGenerator_arginfo.h"
#include "JvmLoaderObj.h"
#include "JEGeneratorObj.h"

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
    zend_object* zend_obj;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_OBJ_EX(zend_obj, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    auto object = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(getThis()));
    object->jvm_obj = fetch_from_zend_object<jvm_obj>(zend_obj);

    cout << "JEGenerator construct" << endl;
}

JEGENERATOR_METHOD(generateChunk) {
    auto object = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(getThis()));
    JNIEnv *env = object->jvm_obj->env;
    jmethodID mid = env->GetMethodID(object->jegenerator_class, "generateChunk", "(II)Ljp/tedo0627/jeloader/JEChunk;");
    env->CallObjectMethod(object->jegenerator_obj, mid, 0, 0);
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