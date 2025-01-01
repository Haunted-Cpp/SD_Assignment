#!/bin/bash

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
