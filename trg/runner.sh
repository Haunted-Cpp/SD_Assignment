#!/bin/bash

#Script variables

#-----------------------------------------------------------------------------------
numberMachines=5  # Number of machines the ring has
port=51243        # Port where the service will be hosted
room="L80"        # DCC room where the processes will be hosted
startIP="2"       # The PC hosting the first peer - L802 
                  # In this case, the PCs "L802 L803 L804 L805 L806" are used
calculatorIP="9"  # The PC hosting the Calculator Server - L809
#-----------------------------------------------------------------------------------

# Name of the tmux session
SESSION_NAME="assignment_1"

# Create a new tmux session
tmux new-session -d -s "$SESSION_NAME"

for ((i = 0; i < $numberMachines; i++));
do
  # Create a new window and run the command
  tmux new-window -t "$SESSION_NAME" -n "window_$i"
  nextIP=$((startIP + i))
  forwardingHost=$((startIP + (i + 1) % numberMachines))
  tmux send-keys -t "$SESSION_NAME:$i" "clear; ssh $room$nextIP 'cd Desktop/SD_1/; java -jar trg.jar $port $room$forwardingHost $room$calculatorIP'" C-m
done

# Launch the calculator server in a new window
tmux new-window -t "$SESSION_NAME" -n "window_$numberMachines"
tmux send-keys -t "$SESSION_NAME:$numberMachines" "clear; ssh $room$calculatorIP 'cd Desktop/SD_1/; java -jar calculatorServer.jar $port'" C-m

# Attach to the session
tmux attach-session -t "$SESSION_NAME"
