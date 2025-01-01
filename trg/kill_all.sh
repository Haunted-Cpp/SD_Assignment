#!/bin/bash

numberMachines=5

port=51243
startIP="2"
calculatorIP="9"
room="L80"

network="$room$startIP"

for ((i = 1; i < $numberMachines; i++));
do
  nextIP=$((startIP + i))
  network="$network $room$nextIP"
done

network="$network $room$calculatorIP"
 

java -jar injectEnd.jar $port $network 

for ((i = 0; i < $((numberMachines + 1)); i++));
do
  tmux kill-pane -t assignment_1:$i
done
