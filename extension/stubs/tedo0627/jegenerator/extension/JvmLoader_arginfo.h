ZEND_BEGIN_ARG_INFO_EX(arginfo_tedo0627_jegenerator_extension_JvmLoader___construct, 0, 0, 1)
    ZEND_ARG_TYPE_INFO(0, path, IS_STRING, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_TYPE_INFO_EX(arginfo_tedo0627_jegenerator_extension_JvmLoader_init, 0, 0, IS_VOID, 0)
ZEND_END_ARG_INFO()

ZEND_METHOD(tedo0627_jegenerator_extension_JvmLoader, __construct);
ZEND_METHOD(tedo0627_jegenerator_extension_JvmLoader, init);

static const zend_function_entry tedo0627_jegenerator_extension_jvmloader_methods[] = {
    ZEND_ME(tedo0627_jegenerator_extension_JvmLoader, __construct, arginfo_tedo0627_jegenerator_extension_JvmLoader___construct, ZEND_ACC_PUBLIC)
    ZEND_ME(tedo0627_jegenerator_extension_JvmLoader, init, arginfo_tedo0627_jegenerator_extension_JvmLoader_init, ZEND_ACC_PUBLIC)
    ZEND_FE_END
};