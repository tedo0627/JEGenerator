#ifndef HAVE_JE_GENERATOR_H
#define HAVE_JE_GENERATOR_H

extern "C" {
#include "php.h"
}

PHP_METHOD(JEGenerator, __construct);
PHP_METHOD(JEGenerator, generateChunk);
PHP_METHOD(JEGenerator, populateChunk);

extern zend_class_entry* jegenerator_class_entry;

void register_jegenerator_class();

#endif