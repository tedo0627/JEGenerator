<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use tedo0627\jegenerator\extension\JELoader;
use tedo0627\jegenerator\extension\JvmLoader;
use Webmozart\PathUtil\Path;

class JEGenerator extends PluginBase {

    public function onEnable(): void {
        $config = $this->getConfig();
        $loader = Path::join($this->getServer()->getDataPath(), $config->get("loader-path"));
        $server = Path::join($this->getServer()->getDataPath(), $config->get("server-path"));
        $jvm = new JvmLoader($loader . ";" . $server);
        var_dump($jvm->init());
        $je = new JELoader($jvm);
        var_dump($je->checkEula());
        echo "1\n";
        $je->init();
        echo "2\n";
        $generator = $je->getGenerator("OVERWORLD", 0);
        $generator->generateChunk(0, 0);
        echo "3\n";
    }
}