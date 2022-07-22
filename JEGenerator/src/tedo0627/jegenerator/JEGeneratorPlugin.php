<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use pocketmine\world\generator\GeneratorManager;
use tedo0627\jegenerator\extension\JELoader;
use tedo0627\jegenerator\extension\JvmLoader;
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
        $jvm = new JvmLoader($loader . ";" . $server);
        $jvm->init();
        $je = new JELoader();
        $je->checkEula();
        $je->init();

        $this->loader = $je;
        GeneratorManager::getInstance()->addGenerator(JEOverworldGenerator::class, "je", fn() => null);
    }
}