#!/bin/bash

source conf_env

export LIB_TARGET=trqbox-demo-frontend/rails/trqbox-demo-frontend/lib

rm -rf $LIB_TARGET/*.jar

cp trqbox-demo-ror-cdi-int/target/trqbox-demo-ror-cdi-int-1.0.0-SNAPSHOT/WEB-INF/lib/*.jar $LIB_TARGET
rm $LIB_TARGET/guava-r07.jar
