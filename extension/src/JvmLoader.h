#ifndef HAVE_JVM_LOADER_H
#define HAVE_JVM_LOADER_H

extern "C" {
#include "php.h"
}

PHP_METHOD(JvmLoader, __construct);
PHP_METHOD(JvmLoader, checkEula);
PHP_METHOD(JvmLoader, init);

void register_jvmloader_class();

#endif