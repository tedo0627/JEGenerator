--TEST--
calljava_test2() Basic test
--SKIPIF--
<?php
if (!extension_loaded('calljava')) {
    echo 'skip';
}
?>
--FILE--
<?php
var_dump(calljava_test2());
var_dump(calljava_test2('PHP'));
?>
--EXPECT--
string(11) "Hello World"
string(9) "Hello PHP"
