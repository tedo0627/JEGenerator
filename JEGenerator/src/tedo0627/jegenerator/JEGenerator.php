<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use tedo0627\jegenerator\extension\JvmLoader;
use Webmozart\PathUtil\Path;

class JEGenerator extends PluginBase {

    public function onEnable(): void {
        $this->getServer()->getLogger()->info("sample message");
        $jvm = new JvmLoader(Path::join($this->getServer()->getDataPath(), "JELoader-1.0-SNAPSHOT-all.jar"));
        var_dump($jvm->init());
    }
}