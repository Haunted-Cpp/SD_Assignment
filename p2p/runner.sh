#!/bin/bash

#Script variables

#-----------------------------------------------------------------------------------

numberMachines=7  # Number of machines the p2p network has
port=51243        # Port where the service will be hosted
seconds=60        # Timeout parameter
room="L120"       # DCC room where the processes will be hosted
startIP="1"       # The PC hosting the first peer - L1201 
                  # In this case, the PCs "L1201 L1203 L1204 L1205 L1206 L1207" are used
folder=$(pwd)     # Folder where the files are contained

# Build the `network` parameter 
network=("${room}3" "${room}1 ${room}4 ${room}5" "${room}3" "${room}3 ${room}6 ${room}7" "${room}5" "${room}5")

# It encodes the following graph (adjacency list format)
#L1201: L1203 
#L1203: L1201 L1204 L1205
#L1204: L1203
#L1205: L1203 L1206 L1207
#L1206: L805
#L1207: L805

#-----------------------------------------------------------------------------------

# Name of the tmux session
SESSION_NAME="assignment_2"


# Create a new tmux session (detached)
tmux new-session -d -s "$SESSION_NAME" -n "start"

index=0
for ((i = 0; i < $numberMachines; i++));
do
  if [ "$i" -eq "1" ]; then
      continue
  fi
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  tmux send-keys -t "$SESSION_NAME:window_$i" "clear; ssh $room$nextIP 'cd $folder; java -jar p2p.jar ${seconds} ${port} ${network[$index]}'" C-m
  index=$((index + 1))
done

tmux select-window -t "$SESSION_NAME:start"

# Attach to the session
tmux attach-session -t "$SESSION_NAME"


