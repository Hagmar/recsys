#!/bin/sh
mkdir -p build;
cd src;
javac -classpath ../libs/weka.jar:../libs/sqlite-jdbc-3.8.11.2.jar recsys/*.java recsys/*/*.java -d ../