#!/bin/bash

# Sends a start signal to:

# L802 @ 51243
# L803 @ 51243
# L804 @ 51243
# L805 @ 51243
# L806 @ 51243
# L807 @ 51243

numberMachines=6

port=51243
startIP="2"
room="L80"

network="$room$startIP"

for ((i = 1; i < $numberMachines; i++));
do
  nextIP=$((startIP + i))
  network="$network $room$nextIP"
done

java -jar injectStart.jar $port $network 
