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
use ZipArchive;

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

        $this->extract($server);
        $paths = $this->getLibraryPath();
        $paths[] = $loader;
        $jvm = new JvmLoader(implode($separate, $paths));
        $jvm->init();

        $je = new JELoader();
        if (!$je->checkEula(Path::join($this->getDataFolder(), "eula.txt"))) {
            $this->getLogger()->error("You must agree to eula.");
            return;
        }

        $je->init(Path::join($this->getDataFolder(), "ignore"));
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

    private function extract(string $serverPath) {
        $extractPath = Path::join($this->getDataFolder(), "extract");
        if (file_exists($extractPath)) return;
        if (!file_exists($serverPath)) return;

        $pattern = [ "META-INF/classpath-joined", "META-INF/libraries/", "META-INF/versions/" ];
        $zip = new ZipArchive();
        if ($zip->open($serverPath) !== true) return;

        for ($i = 0; $i < $zip->numFiles; $i++) {
            $fileName = $zip->getNameIndex($i);
            if (str_ends_with($fileName, "/")) continue;

            for ($j = 0; $j < count($pattern); $j++) {
                if (!str_starts_with($fileName, $pattern[$j])) continue;

                $fileInfo = pathinfo($fileName);
                $dir = ltrim($fileInfo["dirname"], "META-INF/");
                $target = Path::join($extractPath, $dir, $fileInfo["basename"]);
                $targetPath = pathinfo($target)["dirname"] . "/";
                if (!file_exists($targetPath)) mkdir($targetPath, 0777, true);
                copy("zip://" . $serverPath . "#" . $fileName, $target);
            }
        }
        $zip->close();
    }

    private function getLibraryPath(): array {
        $text = file_get_contents(Path::join($this->getDataFolder(), "extract", "classpath-joined"));
        if ($text === false) return [];

        $split = explode(";", $text);
        for ($i = 0; $i < count($split); $i++) {
            $split[$i] = Path::join($this->getDataFolder(), "extract", $split[$i]);
        }

        return $split;
    }

    private function addGenerator(string $class, string $name) {
        GeneratorManager::getInstance()->addGenerator($class, $name, fn() => null);
    }
}