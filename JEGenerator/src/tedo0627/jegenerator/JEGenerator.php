<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;

class JEGenerator extends PluginBase {

    public function onEnable(): void {
        $this->getServer()->getLogger()->info("sample message");
    }
}