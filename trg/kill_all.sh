#!/bin/bash

# Sends a kill signal to:

# L1201 (calculatorServer)
# L1203
# L1204
# L1205
# L1206
# L1207 

# And kills the associated tmux panels 

# Name of the tmux session
SESSION_NAME="trg"

numberMachines=7

port=51243
startIP="1"
room="L120"

network="${room}$startIP"

for ((i = 1; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  network="$network ${room}$nextIP"
done

java -jar injectEnd.jar $port $network 

for ((i = 0; i < $((numberMachines - 1)); i++));
do
  tmux kill-pane -t "$SESSION_NAME:window_$i"
done
