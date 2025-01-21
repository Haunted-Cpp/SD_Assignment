#!/bin/bash

# Sends a kill signal to:

# L1203
# L1204
# L1205
# L1206
# L1207
# L1210 (calculatorServer)

# And kills the associated tmux panels 

numberMachines=5

port=51243
startIP="3"
calculatorIP="10"
room="L12"

network="${room}0$startIP"

for ((i = 1; i < $numberMachines; i++));
do
  nextIP=$((startIP + i))
  network="$network ${room}0$nextIP"
done

network="$network $room$calculatorIP"
 

java -jar injectEnd.jar $port $network 

for ((i = 0; i < $((numberMachines + 1)); i++));
do
  tmux kill-pane -t assignment_1:$i
done
