#!/bin/bash

# Sends a kill signal to:

# L802
# L803
# L804
# L805
# L806
# L809 (calculatorServer)

# And kills the associated tmux panels 

numberMachines=5

port=51243
startIP="1"
calculatorIP="7"
room="L120"

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
