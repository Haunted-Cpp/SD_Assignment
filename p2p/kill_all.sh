#!/bin/bash

# Sends a kill signal to:

# L802
# L803
# L804
# L805
# L806
# L807 

# And kills the associated tmux panels 

numberMachines=6

port=51243
startIP="1"
room="L120"

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
