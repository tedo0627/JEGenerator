--TEST--
Check if calljava is loaded
--SKIPIF--
<?php
if (!extension_loaded('calljava')) {
    echo 'skip';
}
?>
--FILE--
<?php
echo 'The extension "calljava" is available';
?>
--EXPECT--
The extension "calljava" is available
