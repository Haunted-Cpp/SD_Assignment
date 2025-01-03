#!/bin/bash

#Script variables

#-----------------------------------------------------------------------------------

numberMachines=6 # Number of machines the p2p network has
port=51243       # Port where the service will be hosted
seconds=60       # Timeout parameter
room="L80"       # DCC room where the processes will be hosted
startIP="2"      # The PC hosting the first peer - L802 
                 # In this case, the PCs "L802 L803 L804 L805 L806 L807" are used
folder=$(pwd)    # Folder where the files are contained

# Build the `network` parameter 
network=("L803" "L802 L804 L805" "L803" "L803 L806 L807" "L805" "L805")

# It encodes the following graph (adjacency list format)
#L802: L803 
#L803: L802 L804 L805
#L804: L803
#L805: L803 L806 L807 
#L806: L805
#L807: L805

#-----------------------------------------------------------------------------------

# Name of the tmux session
SESSION_NAME="assignment_2"

# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME"
for ((i = 0; i < $numberMachines; i++));
do
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd $folder; java -jar p2p.jar ${seconds} ${port} ${network[$i]}'" C-m
done

# Attach to the session
tmux attach-session -t "$SESSION_NAME"
