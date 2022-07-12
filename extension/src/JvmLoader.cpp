#include "JvmLoader.h"
#include "ZendUtil.h"
#include "stubs/tedo0627/jegenerator/extension/JvmLoader_arginfo.h"
#include "JvmLoaderObj.h"

#include <jni.h>
#include <iostream>
#include <cstdio>
#include <cstring>
using namespace std;

static zend_object_handlers jvmloader_handlers;

static zend_object* jvmloader_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<jvm_obj>(class_type, &jvmloader_handlers);
    return &object->std;
}

static void jvmloader_free(zend_object* obj) {
    zend_object_std_dtor(obj);
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

    string str = "-Djava.class.path=";
    str.append(ZSTR_VAL(path));
    char* cstr = new char[str.size() + 1];
    char_traits<char>::copy(cstr, str.c_str(), str.size() + 1);

    JavaVM *jvm;
    JNIEnv *env;

    JavaVMInitArgs vm_args;
    JavaVMOption* options = new JavaVMOption[1];
    options[0].optionString = cstr;
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;
    jint rc = JNI_CreateJavaVM(&jvm, (void**) &env, &vm_args);
    delete options;
    if (rc != JNI_OK) {
        RETURN_BOOL(false);
        return;
    }

    object->jvm = jvm;
    object->env = env;

    cout << "JVM load succeeded: Version ";
    jint ver = env->GetVersion();
    cout << ((ver >> 16) & 0x0f) << "." << (ver & 0x0f) << endl;
    jclass cls2 = env->FindClass("jp/tedo0627/jeloader/JELoader");
    if (cls2 == nullptr) {
        cerr << "ERROR : class not find" << endl;
    } else {
        cout << "Class JELoader found" << endl;
        jmethodID mid = env->GetStaticMethodID(cls2, "sayHi", "()V");
        if(mid == nullptr) {
            cerr << "ERROR : method void sayHi() not found!" << endl;
        } else {
            env->CallStaticVoidMethod(cls2, mid);
        }
    }
    
    jclass cls3 = env->FindClass("Ljp/tedo0627/jeloader/JELoader;");
    if (cls3 != nullptr) {
        cout << "find JELoader" << endl;
    }

    jmethodID mid2 = env->GetStaticMethodID(cls2, "Square", "(I)I");
    if (mid2 == nullptr) {
        cerr << "ERROR: method Square(int) not find!" << endl;
    } else {
        int i = 11;
        cout << "get Square return = " << env->CallStaticIntMethod(cls2, mid2, (jint) i);
        cout << endl;
    }
    //jvm->DestroyJavaVM();
    RETURN_BOOL(true);
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