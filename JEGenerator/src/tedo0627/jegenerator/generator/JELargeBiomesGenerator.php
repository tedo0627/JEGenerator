<?php

namespace tedo0627\jegenerator\generator;

class JELargeBiomesGenerator extends JEGeneratorBase {

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset, "LARGE_BIOMES");
    }
}