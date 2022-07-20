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

    public static JEGenerator $sgenerator;

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset);

        $this->generator = JELoader::getGenerator("OVERWORLD", $seed);
        echo "construct thread id: " . \Thread::getCurrentThreadId() . "\n";
    }

    public function generateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
    }

    public function populateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        echo "thread id: " . \Thread::getCurrentThreadId() . "\n";
        echo "pupulate chunk x: " . $chunkX . ", z: " . $chunkZ . "\n";
        $chunk = new Chunk([], BiomeArray::fill(0), false);
        $jechunk = $this->generator->generateChunk($chunkX, $chunkZ);
        echo "aaa2\n";
        foreach ($jechunk->getBlocks() as $i => $value) {
            if ($value == 0) continue;

            $y = $i % 256;
            $z = $i / 256;
            $x = $i / (16 * 256);

            $chunk->setFullBlock($x, $y, $z, ($value << Block::INTERNAL_METADATA_BITS) | 0);
            //echo "set: " . $value . ", full: " . (($value << Block::INTERNAL_METADATA_BITS) | 0) . "\n";
        }
        echo "pupulate chunk finish x: " . $chunkX . ", z: " . $chunkZ . "\n";
    }
}