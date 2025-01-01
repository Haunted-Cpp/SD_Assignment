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

java -jar injectEnd.jar $port $network 

for ((i = 0; i < $numberMachines; i++));
do
  tmux kill-pane -t assignment_2:$i
done
