#!/bin/bash

#Script variables

#-----------------------------------------------------------------------------------

numberMachines=7 # Number of machines the network has
port=51243       # Port where the service will be hosted
startIP="1"      # The PC hosting the first peer - L1201 
                 # In this case, the PCs "L1201 L1203 L1204 L1205 L1206 L1207" are used
room="L120"      # DCC room where the processes will be hosted
folder=$(pwd)    # Folder where the files are contained

# Build the `network` parameter (contains all the hostnames)
network=""
for ((i = 0; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  network="$network $room$nextIP"
done

#-----------------------------------------------------------------------------------

# Name of the tmux session
SESSION_NAME="assignment_3"

# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME" -n "start"

# Remove old files
rm -rf output/L*.txt
rm -rf output/L*.comp

for ((i = 0; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  tmux send-keys -t "$SESSION_NAME:window_$i" "clear; ssh $room$nextIP 'cd $folder; java -jar tom.jar $port $network'" C-m
done

tmux select-window -t "$SESSION_NAME:start"

# Attach to the session
tmux attach-session -t "$SESSION_NAME"

