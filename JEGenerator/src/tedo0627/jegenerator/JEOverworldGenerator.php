<?php

namespace tedo0627\jegenerator;

use pocketmine\world\ChunkManager;
use pocketmine\world\format\BiomeArray;
use pocketmine\world\format\Chunk;
use pocketmine\world\generator\Generator;
use tedo0627\jegenerator\extension\JEGenerator;

class JEOverworldGenerator extends Generator {

    private JEGenerator $generator;

    public static JEGenerator $sgenerator;

    public function __construct(int $seed, string $preset) {
        parent::__construct($seed, $preset);

        //$this->generator = JEGeneratorPlugin::$loader->getGenerator("OVERWORLD", $seed);
        echo "thread id: " . \Thread::getCurrentThreadId() . "\n";

        try {
            throw new \InvalidArgumentException();
        } catch (\Exception $e) {
            echo $e->getTraceAsString() . "\n";
        }
    }

    public function generateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
    }

    public function populateChunk(ChunkManager $world, int $chunkX, int $chunkZ): void {
        /*
        $chunk = new Chunk([], BiomeArray::fill(0), false);
        $jechunk = $this->generator->generateChunk($chunkX, $chunkZ);
        foreach ($jechunk->getBlocks() as $i => $value) {
            $y = $i % (16 * 256);
            $z = $i / 256;
            $x = $i / (16 * 256);

            $chunk->setFullBlock($x, $y, $z, $value);
        }
        */
    }
}