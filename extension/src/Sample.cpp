#include "Sample.h"
#include "ZendUtil.h"
#include "stubs/sample/Sample_arginfo.h"

#include <jni.h>
#include <iostream>
#include <cstdio>
#include <cstring>
using namespace std;

static zend_object_handlers sample_handlers;

typedef struct {
    zend_long size;
    double* buff;
    zend_object std;
} sample_obj;

static zend_object* sample_new(zend_class_entry* class_type) {
    auto object = alloc_custom_zend_object<sample_obj>(class_type, &sample_handlers);
    return &object->std;
}

static void sample_free(zend_object* obj) {
    zend_object_std_dtor(obj);
}

#define SAMPLE_METHOD(name) PHP_METHOD(sample_Sample, name)

SAMPLE_METHOD(__construct) {
    string str = "-Djava.class.path=.";
    char* cstr = new char[str.size() + 1];
    strcpy(cstr, str.c_str());

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
        cout << "jvm load failed" << endl;
    }

    cout << "JVM load succeeded: Version ";
    jint ver = env->GetVersion();
    cout << ((ver >> 16) & 0x0f) << "." << (ver & 0x0f) << endl;
    jclass cls2 = env->FindClass("jp\\tedo0627\\jeloader\\JELoader");
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
    jvm->DestroyJavaVM();
}

SAMPLE_METHOD(get) {
    zend_long x;
    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_LONG(x)
    ZEND_PARSE_PARAMETERS_END();

    RETURN_LONG(x + 1);
}

void register_sample_class() {
    memcpy(&sample_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    sample_handlers.offset = XtOffsetOf(sample_obj, std);
    sample_handlers.free_obj = sample_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "sample\\Sample", sample_methods);
    ce.create_object = sample_new;
    zend_register_internal_class(&ce);
}