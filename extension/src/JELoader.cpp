#include "JELoader.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JELoader_arginfo.h"
#include "JvmLoaderObj.h"

#include <jni.h>
#include <iostream>
#include <cstdio>
#include <cstring>
using namespace std;

static zend_object_handlers jeloader_handlers;

typedef struct {
    zend_object std;
} je_obj;

static zend_object* jeloader_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<je_obj>(class_type, &jeloader_handlers);
    return &object->std;
}

static void jeloader_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

#define JELOADER_METHOD(name) PHP_METHOD(tedo0627_jegenerator_extension_JELoader, name)

JELOADER_METHOD(__construct) {
    zend_object* zobj;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_OBJ_EX(zobj, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    jvm_obj* jvm = fetch_from_zend_object<jvm_obj>(zobj);
    jint ver = jvm->env->GetVersion();
    cout << ((ver >> 16) & 0x0f) << "." << (ver & 0x0f) << endl;

    auto object = fetch_from_zend_object<je_obj>(Z_OBJ_P(getThis()));
}

JELOADER_METHOD(checkEula) {
    RETURN_BOOL(false);
}

JELOADER_METHOD(init) {
    auto object = fetch_from_zend_object<je_obj>(Z_OBJ_P(getThis()));

    RETURN_BOOL(true);
}

void register_jeloader_class() {
    memcpy(&jeloader_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    jeloader_handlers.offset = XtOffsetOf(je_obj, std);
    jeloader_handlers.free_obj = jeloader_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "tedo0627\\jegenerator\\extension\\JELoader", tedo0627_jegenerator_extension_jeloader_methods);
    ce.create_object = jeloader_new;
    zend_register_internal_class(&ce);
}