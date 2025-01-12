#!/bin/bash

#Script variables

#-----------------------------------------------------------------------------------

numberMachines=6 # Number of machines the network has
port=51243       # Port where the service will be hosted
startIP="1"      # The PC hosting the first peer - L802 
                 # In this case, the PCs "L802 L803 L804 L805 L806 L807" are used
room="L120"       # DCC room where the processes will be hosted
folder=$(pwd)    # Folder where the files are contained

# Build the `network` parameter (contains all the hostnames)
network=""
for ((i = 0; i < $numberMachines; i++));
do
  nextIP=$((startIP + i))
  network="$network $room$nextIP"
done

#-----------------------------------------------------------------------------------

# Name of the tmux session
SESSION_NAME="assignment_3"

# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME"

# Remove old files
rm -rf output/L*.txt
rm -rf output/L*.comp

for ((i = 0; i < $numberMachines; i++));
do
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd $folder; java -jar tom.jar $port $network'" C-m
done

# Attach to the session
tmux attach-session -t "$SESSION_NAME"

