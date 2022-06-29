#include "Sample.h"
#include "ZendUtil.h"
#include "stubs/Sample_arginfo.h"

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

}

/*
SAMPLE_METHOD(get) {
    zend_long x;
    ZEND_PARSE_PARAMETERS_START_EX(ZEND_PARSE_PARAMS_THROW, 1, 1)
        Z_PARAM_LONG(x)
    ZEND_PARSE_PARAMETERS_END();

    RETURN_LONG(x + 1);
}
*/

void register_sample_class() {
    mcmcpy(&sample_handlers, zend_get_std_object_handlers(), sizeof(zend_object_handlers));
    sample_handlers.offset = XtOffsetOf(sample_obj, std);
    sample_handlers.free_obj = sample_free;

    zend_class_entry ce;
    INIT_CLASS_ENTRY(ce, "sample\\Sample", sample_methods);
    ce.create_object = sample_new;
    zend_register_internal_class(&ce);
}