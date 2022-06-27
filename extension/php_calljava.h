/* calljava extension for PHP */

#ifndef PHP_CALLJAVA_H
# define PHP_CALLJAVA_H

extern zend_module_entry calljava_module_entry;
# define phpext_calljava_ptr &calljava_module_entry

# define PHP_CALLJAVA_VERSION "0.1.0"

# if defined(ZTS) && defined(COMPILE_DL_CALLJAVA)
ZEND_TSRMLS_CACHE_EXTERN()
# endif

#endif	/* PHP_CALLJAVA_H */
