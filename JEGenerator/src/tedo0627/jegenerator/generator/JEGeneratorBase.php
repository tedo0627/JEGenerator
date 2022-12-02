<?php

namespace tedo0627\jegenerator\generator;

use pocketmine\world\ChunkManager;
use pocketmine\world\format\BiomeArray;
use pocketmine\world\format\Chunk;
use pocketmine\world\generator\Generator;
use tedo0627\jegenerator\extension\JEGenerator;
use tedo0627\jegenerator\extension\JELoader;

class JEGeneratorBase extends Generator {

    private JEGenerator $generator;

    public function __construct(int $seed, string $preset, string $type) {
        parent::__construct($seed, $preset);

        $biome = "";
        $split = explode(",", $preset);
        foreach ($split as $str) {
            $pair = explode(",", $str);
            if (count($pair) < 2) continue;

            if ($pair[0] == "biome") $biome = $pair[1];
        }

        $this->generator = JELoader::getGenerator($type, $seed, $biome);
    }

    public function generateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        $chunk = new Chunk([], BiomeArray::fill(0), false);
        $world->setChunk($chunkX, $chunkZ, $chunk);

        $this->generator->generateChunk($chunkX, $chunkZ);
    }

    public function populateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        $jechunk = $this->generator->populateChunk($chunkX, $chunkZ);
        $chunk = $world->getChunk($chunkX, $chunkZ);
        foreach ($jechunk->getBlocks() as $i => $value) {
            if ($value == 0) continue;

            $y = $i % 256;
            $z = $i / 256;
            $x = $i / (16 * 256);

            $chunk->setFullBlock($x, $y, $z, $value);
        }

        foreach ($jechunk->getBiomes() as $i => $value) {
            $x = $i / 16;
            $z = $i % 16;

            $chunk->setBiomeId($x, $z, $value);
        }
    }
}