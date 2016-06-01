#!/bin/sh
classpath=".:libs/weka.jar:libs/sqlite-jdbc-3.8.11.2.jar";
if [ "$1" = "cli" ]; then
    java -cp $classpath recsys.Main
else
    java -cp $classpath recsys.weka.TestRunner
fi