#!/bin/bash

# Sends a start signal to L1201 @ 51243

port=51243
startIP="1"
room="L120"

java -jar injectStart.jar $port $room$startIP
