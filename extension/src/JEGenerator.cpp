#include "JEChunk.h"
#include "JEChunkObj.h"
#include "JEGenerator.h"
#include "JEGeneratorObj.h"
#include "JvmLoader.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JEGenerator_arginfo.h"

#include <jni.h>

zend_class_entry* jegenerator_class_entry;
static zend_object_handlers jegenerator_handlers;

static zend_object* jegenerator_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<jegenerator_obj>(class_type, &jegenerator_handlers);

    JNIEnv* env = attachThread();
    object->jegenerator_class = env->FindClass("jp/tedo0627/jeloader/JEGenerator");
    if (exceptionCheck()) return &object->std;
    object->generate_chunk_method = env->GetMethodID(object->jegenerator_class, "generateChunk", "(II)Ljp/tedo0627/jeloader/JEChunk;");
    if (exceptionCheck()) return &object->std;
    object->populate_chunk_method = env->GetMethodID(object->jegenerator_class, "populateChunk", "(II)[Ljp/tedo0627/jeloader/FeatureData;");
    if (exceptionCheck()) return &object->std;

    jclass cls = env->FindClass("jp/tedo0627/jeloader/FeatureData");
    if (exceptionCheck()) return &object->std;
    object->get_index_method = env->GetMethodID(cls, "getIndex", "()I");
    if (exceptionCheck()) return &object->std;
    object->get_value_method = env->GetMethodID(cls, "getValue", "()I");
    if (exceptionCheck()) return &object->std;
    
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

    JNIEnv* env = attachThread();

    auto object = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(getThis()));
    jobject jechunk = env->CallObjectMethod(object->jegenerator_obj, object->generate_chunk_method, (int) x, (int) z);
    if (exceptionCheck()) return;

    object_init_ex(return_value, jechunk_class_entry);
    jechunk_obj* jechunk_o = fetch_from_zend_object<jechunk_obj>(Z_OBJ_P(return_value));
    jechunk_o->jechunk_obj = jechunk;
}

JEGENERATOR_METHOD(populateChunk) {
    zend_long x;
    zend_long z;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 2, 2)
        Z_PARAM_LONG(x)
        Z_PARAM_LONG(z)
    ZEND_PARSE_PARAMETERS_END();

    JNIEnv* env = attachThread();

    auto object = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(getThis()));
    jobjectArray array = (jobjectArray) env->CallObjectMethod(object->jegenerator_obj, object->populate_chunk_method, (int) x, (int) z);
    if (exceptionCheck()) return;

    array_init(return_value);
    int len = env->GetArrayLength(array);
    if (exceptionCheck()) return;
    for (int i = 0; i < len; i++) {
        jobject obj = env->GetObjectArrayElement(array, i);
        if (exceptionCheck()) return;
        jint index = env->CallIntMethod(obj, object->get_index_method);
        if (exceptionCheck()) return;
        jint value = env->CallIntMethod(obj, object->get_value_method);
        if (exceptionCheck()) return;
        
        add_index_long(return_value, (long) index, (long) value);
    }
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