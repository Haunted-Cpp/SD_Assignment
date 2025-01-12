#!/bin/bash

# Sends a start signal to L802 @ 51243

port=51243
startIP="2"
room="L120"

java -jar injectStart.jar $port $room$startIP
