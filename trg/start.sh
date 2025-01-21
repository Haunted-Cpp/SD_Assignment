#!/bin/bash

# Sends a start signal to L1203 @ 51243

port=51243
startIP="3"
room="L120"

java -jar injectStart.jar $port $room$startIP
