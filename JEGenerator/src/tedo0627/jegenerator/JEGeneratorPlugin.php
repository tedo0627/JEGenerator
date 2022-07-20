<?php

namespace tedo0627\jegenerator;

use pocketmine\plugin\PluginBase;
use pocketmine\Server;
use pocketmine\snooze\SleeperNotifier;
use pocketmine\world\generator\GeneratorManager;
use tedo0627\jegenerator\extension\JELoader;
use tedo0627\jegenerator\extension\JvmLoader;
use Webmozart\PathUtil\Path;

class JEGeneratorPlugin extends PluginBase {

    public static JELoader $loader;

    private SleeperNotifier $notifier;

    public function onLoad(): void {
        echo "thread id: " . \Thread::getCurrentThreadId() . "\n";
        $config = $this->getConfig();
        $loader = Path::join($this->getServer()->getDataPath(), $config->get("loader-path"));
        $server = Path::join($this->getServer()->getDataPath(), $config->get("server-path"));
        $jvm = new JvmLoader($loader . ";" . $server);
        var_dump($jvm->init());
        $je = new JELoader($jvm);
        var_dump($je->checkEula());
        echo "1\n";
        $je->init();

        self::$loader = $je;
        echo "2\n";
        $generator = $je->getGenerator("OVERWORLD", 0);
        echo "3\n";
        $chunk = $generator->generateChunk(0, 0);
        echo "4\n";

        echo "count: " . count($chunk->getBlocks()) . "\n";
        /*
        $x = -1;
        $z = -1;
        $i = 0;
        foreach ($chunk->getBlocks() as $value) {
            $xx = $i % (256 * 16);
            $zz = $i % 256;
            if ($x == $xx) {
                echo "\nx: " . $x;
                $x = $xx;
            }
            if ($z == $zz) {
                echo "\nz: " . $z;
                $z = $zz;
            }
            $i++;
            echo $value . ", ";
        }
        */
        echo "5\n";

        GeneratorManager::getInstance()->addGenerator(JEOverworldGenerator::class, "je", fn() => null);

        $this->notifier = new SleeperNotifier();
        Server::getInstance()->getTickSleeper()->addNotifier($this->notifier, function () {

        });
    }
}