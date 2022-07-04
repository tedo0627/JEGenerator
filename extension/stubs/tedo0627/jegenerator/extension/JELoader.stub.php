<?php

namespace tedo0627\jegenerator\extension;

class JELoader {

    public function __construct(JvmLoader $jvm) {}

    public function checkEula(string $path): bool {}

    public function init(): bool {}
}