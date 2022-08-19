# JEGenerator
This is the PocketMine plugin that generates the terrain for Minecraft Java Edition v1.17

# Support Generator
- je_overworld
- je_nether
- je_end
- je_large_biomes
- je_amplified
- je_single_biome ※
- je_caves ※
- je_floating_islands ※

## Biome Option
※ These can use the biome option (default plains)  
When used in `server.propertie`, `generator-settings=biome=plains`  
When used in `pocketmine.yml`, `preset: biome=plains`
<details>
 <summary>
  Biome list
 </summary>  
 <ul>
  <li>ocean
  <li>plains
  <li>desert
  <li>mountains
  <li>forest
  <li>taiga
  <li>swamp
  <li>river
  <li>nether_wastes
  <li>the_end
  <li>frozen_ocean
  <li>frozen_river
  <li>snowy_tundra
  <li>snowy_mountains
  <li>mushroom_fields
  <li>mushroom_field_shore
  <li>beach
  <li>desert_hills
  <li>wooded_hills
  <li>taiga_hills
  <li>mountain_edge
  <li>jungle
  <li>jungle_hills
  <li>jungle_edge
  <li>deep_ocean
  <li>stone_shore
  <li>snowy_beach
  <li>birch_forest
  <li>birch_forest_hills
  <li>dark_forest
  <li>snowy_taiga
  <li>snowy_taiga_hills
  <li>giant_tree_taiga
  <li>giant_tree_taiga_hills
  <li>wooded_mountains
  <li>savanna
  <li>savanna_plateau
  <li>badlands
  <li>wooded_badlands_plateau
  <li>badlands_plateau
  <li>warm_ocean
  <li>lukewarm_ocean
  <li>cold_ocean
  <li>deep_warm_ocean
  <li>deep_lukewarm_ocean
  <li>deep_cold_ocean
  <li>deep_frozen_ocean
  <li>legacy_frozen_ocean
  <li>bamboo_jungle
  <li>bamboo_jungle_hills
  <li>sunflower_plains
  <li>desert_lakes
  <li>gravelly_mountains
  <li>flower_forest
  <li>taiga_mountains
  <li>swamp_hills
  <li>ice_spikes
  <li>modified_jungle
  <li>modified_jungle_edge
  <li>tall_birch_forest
  <li>tall_birch_hills
  <li>dark_forest_hills
  <li>snowy_taiga_mountains
  <li>giant_spruce_taiga
  <li>giant_spruce_taiga_hills
  <li>modified_gravelly_mountains
  <li>shattered_savanna
  <li>shattered_savanna_plateau
  <li>eroded_badlands
  <li>modified_wooded_badlands_plateau
  <li>modified_badlands_plateau
  <li>soul_sand_valley
  <li>crimson_forest
  <li>warped_forest
  <li>basalt_deltas
  <li>small_end_islands
  <li>end_midlands
  <li>end_highlands
  <li>end_barrens
  <li>the_void
 </ul>
</details>

# How to install
1. Install java17 and set `JAVA_HOME` environment variable
2. Download server.jar [here](https://launcher.mojang.com/v1/objects/a16d67e5807f57fc4e550299cf20226194497dc2/server.jar)
## Windows
3. Arrange the files as in the tree below.
```
├── bin/
│   └── php/
│       └── ext/
│           └── php_calljava.dll
├── plugin_data/
│   └── JEGenerator/
│       ├── JELoader.jar
│       └── server.jar
├── plugins/
│   └── JEGenerator.phar
└── PocketMine-MP.phar
```
4. Add the following text to the last line of php.ini
```
extension=php_calljava.dll
```
5. Add the following text to the `PATH` environment variable
```
%JAVA_HOME%\bin\server
```
## Linux
3. Arrange the files as in the tree below.
```
├── bin/
│   └── php7/
│       └── bin/
│           └── ext/
│               └── calljava.so
├── plugin_data/
│   └── JEGenerator/
│       ├── JELoader.jar
│       └── server.jar
├── plugins/
│   └── JEGenerator.phar
└── PocketMine-MP.phar
```
4. Add the following sample text to the last line of php.ini  
extension_dir should be the absolute path of bin/php7/bin/ext
```
extension_dir=/home/tedo0627/pmmp/bin/php7/bin/ext
extension=calljava.so
```
5. Add the following text to the `LD_LIBRARY_PATH` environment variable
```
$JAVA_HOME/lib/server
```
---
6. Start pmmp once and agree to eula from the eula.txt file generated in plugin_data/JEGenerator

# Note
Takes a long time to start up  
Generating a few chunks after startup is a bit slow  
This error may occur in some environments, but it is not a problem
```
Illegal format in tzmappings file: illegal non-newline character found at line 1, offset 46.
```

