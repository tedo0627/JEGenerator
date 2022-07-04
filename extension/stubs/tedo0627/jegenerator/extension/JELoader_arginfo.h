ZEND_BEGIN_ARG_INFO_EX(arginfo_tedo0627_jegenerator_extension_JELoader___construct, 0, 0, 1)
    ZEND_ARG_TYPE_INFO(0, jvm, tedo0627\\jegenerator\\extension\\JvmLoader, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_TYPE_INFO_EX(arginfo_tedo0627_jegenerator_extension_JELoader_checkEula, 0, 1, _IS_BOOL, 0)
    ZEND_ARG_TYPE_INFO(0, path, IS_STRING, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_TYPE_INFO_EX(arginfo_tedo0627_jegenerator_extension_JELoader_init, 0, 0, _IS_BOOL, 0)
ZEND_END_ARG_INFO()

ZEND_METHOD(tedo0627_jegenerator_extension_JELoader, __construct);
ZEND_METHOD(tedo0627_jegenerator_extension_JELoader, checkEula);
ZEND_METHOD(tedo0627_jegenerator_extension_JELoader, init);

static const zend_function_entry sample_methods[] = {
    ZEND_ME(tedo0627_jegenerator_extension_JELoader, __construct, arginfo_tedo0627_jegenerator_extension_JELoader___construct, ZEND_ACC_PUBLIC)
    ZEND_ME(tedo0627_jegenerator_extension_JELoader, checkEula, arginfo_tedo0627_jegenerator_extension_JELoader_checkEula, ZEND_ACC_PUBLIC)
    ZEND_ME(tedo0627_jegenerator_extension_JELoader, init, arginfo_tedo0627_jegenerator_extension_JELoader_init, ZEND_ACC_PUBLIC)
    ZEND_FE_END
};