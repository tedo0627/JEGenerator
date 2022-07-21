<?php

namespace tedo0627\jegenerator;

use pocketmine\block\Block;
use pocketmine\world\ChunkManager;
use pocketmine\world\format\BiomeArray;
use pocketmine\world\format\Chunk;
use pocketmine\world\generator\Generator;
use tedo0627\jegenerator\extension\JEGenerator;
use tedo0627\jegenerator\extension\JELoader;

class JEOverworldGenerator extends Generator {

    private JEGenerator $generator;

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset);

        $this->generator = JELoader::getGenerator("OVERWORLD", $seed);
    }

    public function generateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        $chunk = new Chunk([], BiomeArray::fill(0), false);
        $world->setChunk($chunkX, $chunkZ, $chunk);
    }

    public function populateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        $chunk = $world->getChunk($chunkX, $chunkZ);
        $jechunk = $this->generator->generateChunk($chunkX, $chunkZ);
        foreach ($jechunk->getBlocks() as $i => $value) {
            if ($value == 0) continue;

            $y = $i % 256;
            $z = $i / 256;
            $x = $i / (16 * 256);

            $chunk->setFullBlock($x, $y, $z, $value);
        }
    }
}