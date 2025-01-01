#!/bin/bash

# Name of the tmux session
SESSION_NAME="assignment_2"

# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME"

numberMachines=6
port=51243
seconds=60
# timeout=$((seconds * 1000))
startIP="2"
room="L80"
network=("L803" "L802 L804 L805" "L803" "L803 L806 L807" "L805" "L805")

for ((i = 0; i < $numberMachines; i++));
do
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd Desktop/SD_2/; java -jar p2p.jar ${seconds} ${port} ${network[$i]}'" C-m
done

# Attach to the session
tmux attach-session -t "$SESSION_NAME"
