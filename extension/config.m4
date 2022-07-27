PHP_ARG_ENABLE([calljava],
  [whether to enable calljava support],
  [AS_HELP_STRING([--enable-calljava],
    [Enable calljava support])],
  [no])

if test "$PHP_CALLJAVA" != "no"; then
  PHP_NEW_EXTENSION(calljava, calljava.cpp src/JEChunk.cpp src/JEGenerator.cpp src/JELoader.cpp src/JvmLoader.cpp, $ext_shared)
  JAVA_HOME = esyscmd(`printenv JAVA_HOME')
  PHP_ADD_INCLUDE($JAVA_HOME/lib/server)
fi
