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
  tmux kill-pane -t assignment_3:$i
  # Create a new window and run the command
  # tmux new-window -t "$SESSION_NAME" -n "window_$i"
  # nextIP=$((startIP + i))
  # tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd Desktop/SD_testar/; java -jar tom.jar $port $network'" C-m
  # tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd Desktop/SD_testar/; java -jar tom.jar $port $network > $room$nextIP.txt'" C-m
done
