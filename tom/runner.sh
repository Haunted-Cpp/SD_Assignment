#!/bin/bash

# Name of the tmux session
SESSION_NAME="assignment_3"

# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME"

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

rm -rf output/L*.txt
rm -rf output/L*.comp

for ((i = 0; i < $numberMachines; i++));
do
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd Desktop/SD_3/; java -ea -jar tom.jar $port $network'" C-m
done

# Attach to the session
tmux attach-session -t "$SESSION_NAME"

