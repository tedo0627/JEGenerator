PHP_ARG_ENABLE([calljava],
  [whether to enable calljava support],
  [AS_HELP_STRING([--enable-calljava],
    [Enable calljava support])],
  [no])

if test "$PHP_CALLJAVA" != "no"; then
  PHP_REQUIRE_CXX()
  JAVA_HOME = esyscmd(printenv JAVA_HOME)

  PHP_CHECK_LIBRARY(jvm, JNI_CreateJavaVM, [
    PHP_ADD_INCLUDE($JAVA_HOME/include)
    PHP_ADD_INCLUDE($JAVA_HOME/include/linux)
    PHP_ADD_LIBRARY_WITH_PATH(jvm, $JAVA_HOME/lib/server, CALLJAVA_SHARED_LIBADD)
  ], [
    AC_MSG_ERROR([lib not found])
  ], [-L$JAVA_HOME/lib/server])

  PHP_NEW_EXTENSION(calljava, calljava.cpp src/JEChunk.cpp src/JEGenerator.cpp src/JELoader.cpp src/JvmLoader.cpp, $ext_shared)
  PHP_SUBST(CALLJAVA_SHARED_LIBADD)
fi
