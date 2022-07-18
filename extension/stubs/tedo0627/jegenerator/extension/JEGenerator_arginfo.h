ZEND_BEGIN_ARG_INFO_EX(arginfo_tedo0627_jegenerator_extension_JEGenerator___construct, 0, 0, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_OBJ_INFO_EX(arginfo_tedo0627_jegenerator_extension_JEGenerator_generateChunk, 0, 2, tedo0627\\jegenerator\\extension\\JEGenerator, 0)
    ZEND_ARG_TYPE_INFO(0, x, IS_LONG, 0)
    ZEND_ARG_TYPE_INFO(0, z, IS_LONG, 0)
ZEND_END_ARG_INFO()

ZEND_METHOD(tedo0627_jegenerator_extension_JEGenerator, __construct);
ZEND_METHOD(tedo0627_jegenerator_extension_JEGenerator, generateChunk);

static const zend_function_entry tedo0627_jegenerator_extension_jegenerator_methods[] = {
    ZEND_ME(tedo0627_jegenerator_extension_JEGenerator, __construct, arginfo_tedo0627_jegenerator_extension_JEGenerator___construct, ZEND_ACC_PUBLIC)
    ZEND_ME(tedo0627_jegenerator_extension_JEGenerator, generateChunk, arginfo_tedo0627_jegenerator_extension_JEGenerator_generateChunk, ZEND_ACC_PUBLIC)
    ZEND_FE_END
};