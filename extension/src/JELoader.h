#ifndef HAVE_JE_LOADER_H
#define HAVE_JE_LOADER_H

extern "C" {
#include "php.h"
}

PHP_METHOD(JELoader, __construct);
PHP_METHOD(JELoader, checkEula);
PHP_METHOD(JELoader, init);
PHP_METHOD(JELoader, getGenerator);

void register_jeloader_class();

#endif