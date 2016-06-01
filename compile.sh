#!/bin/sh
weka="weka-stable-3.6.13.jar";
sqlite="sqlite-jdbc-3.8.11.2.jar";
weka_download="http://central.maven.org/maven2/nz/ac/waikato/cms/weka/weka-stable/3.6.13/weka-stable-3.6.13.jar";
sqlite_download="http://central.maven.org/maven2/org/xerial/sqlite-jdbc/3.8.11.2/sqlite-jdbc-3.8.11.2.jar";

mkdir -p libs;
cd libs;
# Download libraries
if [ ! -f "$weka" ]; then
    echo "Downloading weka library...";
    wget "$weka_download";
fi
if [ ! -f "$sqlite" ]; then
    echo "Downloading sqlite library...";
    wget "$sqlite_download";
fi
cd ..;

# Compile
mkdir -p build;
cd src;
javac -classpath "../libs/$weka:../libs/$sqlite" recsys/*.java recsys/*/*.java -d ../;
echo "Compiled!";