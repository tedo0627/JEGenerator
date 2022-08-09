<?php

namespace tedo0627\jegenerator\extension;

class JELoader {

    public function __construct() {}

    public function checkEula(string $path): bool {}

    public function init(string $path): bool {}

    public function getGenerator(string $type, int $seed, string $biome): JEGenerator {}
}