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

        $jechunk = $this->generator->generateChunk($chunkX, $chunkZ);
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

    public function populateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        $xOffset = 16 * ($chunkX - 1);
        $zOffset = 16 * ($chunkZ - 1);

        foreach ($this->generator->populateChunk($chunkX, $chunkZ) as $i => $value) {
            $x = ($i >> 16);
            $y = $i % 256;
            $z = ($i >> 8) - ($x << 8);

            $x += $xOffset;
            $z += $zOffset;

            $chunk = $world->getChunk($x >> 4, $z >> 4);
            if ($chunk == null) continue;

            $chunk->setFullBlock($x - (($x >> 4) * 16), $y, $z - (($z >> 4) * 16), $value);
        }
    }
}