ZEND_BEGIN_ARG_INFO_EX(arginfo_tedo0627_jegenerator_extension_JEChunk___construct, 0, 0, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_OBJ_INFO_EX(arginfo_tedo0627_jegenerator_extension_JEChunk_getBlocks, 0, 0, IS_VOID, 0)
ZEND_END_ARG_INFO()

ZEND_METHOD(tedo0627_jegenerator_extension_JEChunk, __construct);
ZEND_METHOD(tedo0627_jegenerator_extension_JEChunk, getBlocks);

static const zend_function_entry tedo0627_jegenerator_extension_jechunk_methods[] = {
    ZEND_ME(tedo0627_jegenerator_extension_JEChunk, __construct, arginfo_tedo0627_jegenerator_extension_JEChunk___construct, ZEND_ACC_PRIVATE)
    ZEND_ME(tedo0627_jegenerator_extension_JEChunk, getBlocks, arginfo_tedo0627_jegenerator_extension_JEChunk_getBlocks, ZEND_ACC_PUBLIC)
    ZEND_FE_END
};