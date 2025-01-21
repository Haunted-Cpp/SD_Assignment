#!/bin/bash

# Sends a kill signal to:

# L1201 -- Calculator Server
# L1203
# L1204
# L1205
# L1206
# L1207 

# And kills the associated tmux panels 

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

java -jar injectEnd.jar $port $network 

for ((i = 0; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  tmux kill-pane -t "assignment_2:window_$i"
done
