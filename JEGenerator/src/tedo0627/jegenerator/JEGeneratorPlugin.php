<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use pocketmine\world\generator\GeneratorManager;
use tedo0627\jegenerator\extension\JELoader;
use tedo0627\jegenerator\extension\JvmLoader;
use tedo0627\jegenerator\generator\JEAmplifiedGenerator;
use tedo0627\jegenerator\generator\JECavesGenerator;
use tedo0627\jegenerator\generator\JEEndGenerator;
use tedo0627\jegenerator\generator\JEFloatingIslangsGenerator;
use tedo0627\jegenerator\generator\JELargeBiomesGenerator;
use tedo0627\jegenerator\generator\JENetherGenerator;
use tedo0627\jegenerator\generator\JEOverworldGenerator;
use tedo0627\jegenerator\generator\JESingleBiomeGenerator;
use Webmozart\PathUtil\Path;

class JEGeneratorPlugin extends PluginBase {

    /**
     * This property is necessary because the JELoader object must be retained.
     */
    public JELoader $loader;

    public function onLoad(): void {
        $config = $this->getConfig();
        $loader = Path::join($this->getServer()->getDataPath(), $config->get("loader-path"));
        $server = Path::join($this->getServer()->getDataPath(), $config->get("server-path"));
        $separate = str_starts_with(PHP_OS, "WIN") ? ";" : ":";
        $jvm = new JvmLoader($loader . $separate . $server);
        $jvm->init();

        $je = new JELoader();
        if (!$je->checkEula()) {
            $this->getLogger()->error("You must agree to eula.");
            return;
        }

        $je->init();
        $this->loader = $je;

        $this->addGenerator(JEOverworldGenerator::class, "je_overworld");
        $this->addGenerator(JENetherGenerator::class, "je_nether");
        $this->addGenerator(JEEndGenerator::class, "je_end");
        $this->addGenerator(JELargeBiomesGenerator::class, "je_large_biomes");
        $this->addGenerator(JEAmplifiedGenerator::class, "je_amplified");
        $this->addGenerator(JESingleBiomeGenerator::class, "je_single_biome");
        $this->addGenerator(JECavesGenerator::class, "je_caves");
        $this->addGenerator(JEFloatingIslangsGenerator::class, "je_floating_islands");
    }

    private function addGenerator(string $class, string $name) {
        GeneratorManager::getInstance()->addGenerator($class, $name, fn() => null);
    }
}