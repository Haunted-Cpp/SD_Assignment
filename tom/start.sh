#!/bin/bash

# Sends a start signal to:

# L1201 @ 51243
# L1203 @ 51243
# L1204 @ 51243
# L1205 @ 51243
# L1206 @ 51243
# L1207 @ 51243

numberMachines=7

port=51243
startIP="1"
room="L120"

network="$room$startIP"

for ((i = 1; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  network="$network $room$nextIP"
done

java -jar injectStart.jar $port $network 
