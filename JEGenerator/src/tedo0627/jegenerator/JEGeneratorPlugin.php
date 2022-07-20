<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use pocketmine\world\generator\GeneratorManager;
use tedo0627\jegenerator\extension\JELoader;
use tedo0627\jegenerator\extension\JvmLoader;
use Webmozart\PathUtil\Path;

class JEGeneratorPlugin extends PluginBase {

    public function onLoad(): void {
        $config = $this->getConfig();
        $loader = Path::join($this->getServer()->getDataPath(), $config->get("loader-path"));
        $server = Path::join($this->getServer()->getDataPath(), $config->get("server-path"));
        $jvm = new JvmLoader($loader . ";" . $server);
        $je = new JELoader($jvm);
        $je->init();

        GeneratorManager::getInstance()->addGenerator(JEOverworldGenerator::class, "je", fn() => null);
    }
}