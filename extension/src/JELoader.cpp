#include "JEGenerator.h"
#include "JEGeneratorObj.h"
#include "JELoader.h"
#include "JvmLoader.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JELoader_arginfo.h"

#include <jni.h>

static zend_object_handlers jeloader_handlers;

typedef struct {
    jobject jeloader_obj;
    jclass jeloader_class;
    jmethodID get_generator_method;
    zend_object std;
} je_obj;

static je_obj* instance;

static zend_object* jeloader_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<je_obj>(class_type, &jeloader_handlers);

    JNIEnv* env = attachThread();
    object->jeloader_class = env->FindClass("jp/tedo0627/jeloader/JELoader");
    if (exceptionCheck()) return &object->std;
    object->get_generator_method = env->GetMethodID(object->jeloader_class, "getGenerator", "(Ljava/lang/String;JLjava/lang/String;)Ljp/tedo0627/jeloader/JEGenerator;");
    if (exceptionCheck()) return &object->std;

    return &object->std;
}

static void jeloader_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

#define JELOADER_METHOD(name) PHP_METHOD(tedo0627_jegenerator_extension_JELoader, name)

JELOADER_METHOD(__construct) {
    auto object = fetch_from_zend_object<je_obj>(Z_OBJ_P(getThis()));
    
    JNIEnv* env = getEnv();
    jclass cls = env->FindClass("jp/tedo0627/jeloader/JELoader");
    if (exceptionCheck()) return;
    jmethodID mid = env->GetMethodID(cls, "<init>", "()V");
    if (exceptionCheck()) return;
    jobject obj = env->NewObject(cls, mid);
    if (exceptionCheck()) return;

    object->jeloader_class = cls;
    object->jeloader_obj = obj;

    instance = object;
}

JELOADER_METHOD(checkEula) {
    zend_string* path;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_STR_EX(path, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    auto object = fetch_from_zend_object<je_obj>(Z_OBJ_P(getThis()));
    JNIEnv* env = getEnv();
    jmethodID mid = env->GetMethodID(object->jeloader_class, "checkEula", "(Ljava/lang/String;)Z");
    if (exceptionCheck()) return;
    bool result = (bool) env->CallBooleanMethod(object->jeloader_obj, mid, env->NewStringUTF(ZSTR_VAL(path)));
    if (exceptionCheck()) return;
    RETURN_BOOL(result);
}

JELOADER_METHOD(init) {
    zend_string* path;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_STR_EX(path, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    auto object = fetch_from_zend_object<je_obj>(Z_OBJ_P(getThis()));
    JNIEnv* env = getEnv();
    jmethodID mid = env->GetMethodID(object->jeloader_class, "init", "(Ljava/lang/String;)V");
    if (exceptionCheck()) return;
    env->CallVoidMethod(object->jeloader_obj, mid, env->NewStringUTF(ZSTR_VAL(path)));
    if (exceptionCheck()) return;

    RETURN_BOOL(true);
}

JELOADER_METHOD(getGenerator) {
    zend_string* type;
    zend_long seed;
    zend_string* biome;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 3, 3)
        Z_PARAM_STR_EX(type, 1, 1)
        Z_PARAM_LONG(seed)
        Z_PARAM_STR_EX(biome, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    auto object = instance;
    JNIEnv* env = attachThread();
    jobject jegenerator = env->CallObjectMethod(object->jeloader_obj, object->get_generator_method, env->NewStringUTF(ZSTR_VAL(type)), (jlong) seed, env->NewStringUTF(ZSTR_VAL(biome)));
    if (exceptionCheck()) return;

    object_init_ex(return_value, jegenerator_class_entry);
    jegenerator_obj* generator_obj = fetch_from_zend_object<jegenerator_obj>(Z_OBJ_P(return_value));
    generator_obj->jegenerator_obj = jegenerator;
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