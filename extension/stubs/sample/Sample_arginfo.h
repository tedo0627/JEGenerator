ZEND_BEGIN_ARG_INFO_EX(arginfo_Sample___construct, 0, 0, 0)
ZEND_END_ARG_INFO()

ZEND_BEGIN_ARG_WITH_RETURN_TYPE_INFO_EX(arginfo_Sample_get, 0, 1, IS_LONG, 0)
	ZEND_ARG_TYPE_INFO(0, x, IS_LONG, 0)
ZEND_END_ARG_INFO()

ZEND_METHOD(sample_Sample, __construct);
ZEND_METHOD(sample_Sample, get);

static const zend_function_entry sample_methods[] = {
    ZEND_ME(sample_Sample, __construct, arginfo_Sample___construct, ZEND_ACC_PUBLIC)
    ZEND_ME(sample_Sample, get, arginfo_Sample_get, ZEND_ACC_PUBLIC)
    ZEND_FE_END
};