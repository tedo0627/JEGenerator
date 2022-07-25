<?php

namespace tedo0627\jegenerator\generator;

class JEOverworldGenerator extends JEGeneratorBase {

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset, "OVERWORLD");
    }
}