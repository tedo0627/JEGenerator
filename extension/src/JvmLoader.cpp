#include "JvmLoader.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JvmLoader_arginfo.h"

#include <iostream>
#include <jni.h>

extern "C" {
#include "Zend/zend_exceptions.h"
}

typedef struct {
    zend_string* path;
    zend_object std;
} jvm_obj;

static zend_object_handlers jvmloader_handlers;

static zend_object* jvmloader_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<jvm_obj>(class_type, &jvmloader_handlers);
    return &object->std;
}

static void jvmloader_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

char* getCstr(std::string str) {
    char* cstr = new char[str.size() + 1];
    std::char_traits<char>::copy(cstr, str.c_str(), str.size() + 1);
    return cstr;
}

#define JVMLOADER_METHOD(name) PHP_METHOD(tedo0627_jegenerator_extension_JvmLoader, name)

JVMLOADER_METHOD(__construct) {
    zend_string* path;

    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_STR_EX(path, 1, 1)
    ZEND_PARSE_PARAMETERS_END();

    auto object = fetch_from_zend_object<jvm_obj>(Z_OBJ_P(getThis()));
    object->path = path;
}

JVMLOADER_METHOD(init) {
    auto object = fetch_from_zend_object<jvm_obj>(Z_OBJ_P(getThis()));
    zend_string* path = object->path;

    std::string str = "-Djava.class.path=";
    str.append(ZSTR_VAL(path));

    JavaVM *jvm;
    JNIEnv *env;

    JavaVMInitArgs vm_args;
    JavaVMOption* options = new JavaVMOption[2];
    options[0].optionString = getCstr(str);
    options[1].optionString = getCstr("--add-opens=java.base/java.lang=ALL-UNNAMED");
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 2;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;
    jint rc = JNI_CreateJavaVM(&jvm, (void**) &env, &vm_args);
    delete options;

    RETURN_BOOL(rc == JNI_OK);
}

JavaVM* getJvm() {
    JavaVM* jvm;
    jsize ct;
    JNI_GetCreatedJavaVMs(&jvm, 1, &ct);

    return jvm;
}

JNIEnv* getEnv() {
    JavaVM* jvm = getJvm();
    JNIEnv* env;
    jvm->GetEnv((void**) &env, JNI_VERSION_1_6);

    return env;
}

JNIEnv* attachThread() {
    JavaVM* jvm = getJvm();
    JNIEnv* env;
    jvm->AttachCurrentThread((void**) &env, NULL);

    return env;
}

bool exceptionCheck() {
    JNIEnv* env = getEnv();
    jboolean check = env->ExceptionCheck();
    if (check == JNI_FALSE) return false;

    env->ExceptionDescribe();
    zend_throw_exception_ex(NULL, 0, "%s", "jni error");
    return true;
}

void register_jvmloader_class() {
    memcpy(&jvmloader_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    jvmloader_handlers.offset = XtOffsetOf(jvm_obj, std);
    jvmloader_handlers.free_obj = jvmloader_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "tedo0627\\jegenerator\\extension\\JvmLoader", tedo0627_jegenerator_extension_jvmloader_methods);
    ce.create_object = jvmloader_new;
    zend_register_internal_class(&ce);
}