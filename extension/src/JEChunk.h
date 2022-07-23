#ifndef HAVE_JE_CHUNK_H
#define HAVE_JE_CHUNK_H

extern "C" {
#include "php.h"
}

PHP_METHOD(JEChunk, __construct);
PHP_METHOD(JEChunk, getBlocks);
PHP_METHOD(JEChunk, getBiomes);

extern zend_class_entry* jechunk_class_entry;

void register_jechunk_class();

#endif