#!/bin/bash

if [ -f localenv.sh ]; then
  . localenv.sh
fi

java -cp $RAM_CLASSPATH jp.mzw.revajaxmutator.Main $*
