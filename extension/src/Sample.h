#ifndef HAVE_SAMPLE_H
#define HAVE_SAMPLE_H

extern "C" {
#include "php.h"
}

PHP_METHOD(Sample, __construct);
PHP_METHOD(Sample, get);

void register_sample_class();

#endif