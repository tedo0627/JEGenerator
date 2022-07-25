<?php

namespace tedo0627\jegenerator\generator;

class JENetherGenerator extends JEGeneratorBase {

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset, "NETHER");
    }
}