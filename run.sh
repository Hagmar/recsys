#!/bin/sh
weka="weka-stable-3.6.13.jar";
sqlite="sqlite-jdbc-3.8.11.2.jar";
classpath=".:libs/$weka:libs/$sqlite";

if [ "$1" = "cli" ]; then
    java -cp $classpath recsys.Main
else
    java -cp $classpath recsys.weka.TestRunner
fi