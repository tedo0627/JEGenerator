#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "src/Sample.h"
#include "src/JvmLoader.h"

extern "C" {
#include "php.h"
#include "ext/standard/info.h"
#include "php_calljava.h"
}

PHP_MINIT_FUNCTION(calljava) {
    register_sample_class();
    register_jvmloader_class();
    return SUCCESS;
}

PHP_MINFO_FUNCTION(calljava) {
    php_info_print_table_start();
    php_info_print_table_header(2, "calljava support", "enabled");
    php_info_print_table_end();
}

zend_module_entry calljava_module_entry = {
    STANDARD_MODULE_HEADER,
    "calljava",
    NULL,
    PHP_MINIT(calljava),
    NULL,
    NULL,
    NULL,
    PHP_MINFO(calljava),
    PHP_CALLJAVA_VERSION,
    STANDARD_MODULE_PROPERTIES
};

#ifdef COMPILE_DL_CALLJAVA
extern "C" {
#ifdef ZTS
    ZEND_TSRMLS_CACHE_DEFINE()
#endif
    ZEND_GET_MODULE(calljava)
}
#endif
