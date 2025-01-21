#!/bin/bash

# Sends a kill signal to $1 - that is, the host given as argument

if test "$#" -ne 1; then
  echo "Invalid number of arguments"
  echo "Usage: \`host to kill\`"
  exit 0
fi

port=51243
java -jar injectEnd.jar $port $1

