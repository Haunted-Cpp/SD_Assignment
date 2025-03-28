# Distributed Systems Practical Assignment
## A Basic Chat Application Using Totally-Ordered Multicast

### Using the .jar Files Provided

The project has been compiled into several `.jar` files to facilitate execution on DCC Lab computers.

#### Launch the Peers

Simply execute:

```
java -jar tom.jar {port} {network}
```

This will launch a peer on `localhost` at the designated `port`.

- The `network` parameter represents a list of hosts within the network (forming a complete graph of the peers).

#### Inject Start Token

Simply execute:

```
java -jar injectStart.jar {port} {network}
```

This will send the starting token to each host listed in `network` on the specified `port`.

- `network`: Should contain **all** the hostnames of the computers involved in the process.

#### Inject End Token

Simply execute:

```
java -jar injectEnd.jar {port} {network}
```

This will send a kill signal to each host listed in `network` on the specified `port`.

- `network`: Should contain **all the hosts** in the network, as it is used to terminate the whole process.

#### Example

```
(@ L802) java -jar tom.jar 12345 L802 L803 L804
(@ L803) java -jar tom.jar 12345 L802 L803 L804
(@ L804) java -jar tom.jar 12345 L802 L803 L804

@ any DCC computer with access to L802, L803 and L804 (to start the process)
java -jar injectStart.jar 12345 L802 L803 L804

@ any DCC computer with access to L802, L803 and L804 (to stop the process)
java -jar injectEnd.jar 12345 L802 L803 L804
```

#### Compiling and Running with `gradle`

The `.jar` file can be generated by running the following command:

```
gradle fatJar
```

This command creates a `.jar` file in the `build/libs/` folder with all dependencies included.

### Scripts to Run on DCC Computers

1. Start by running the script:

```
bash runner.sh
``` 

2. To begin the process by sending the start signal, execute:


```
bash start.sh
```

3. To terminate the process, use the following script:

``` 
bash kill_all.sh
```

This will create the network consisting of the computers: `L802`, `L803`, `L804`, `L805`, `L806`, and `L807`.

- Recall that every computer has a table with the IPs of all the other peers/machines.

Afterwards, a start signal will be sent to all the hosts.

The default port being used is: `51243`.

During the process, each machine will update the file `output/{hostname}.txt` with the words it has received.

### Algorithm Verification

To verify if the algorithm is functioning correctly, execute **Step 3**. After that, run:


```
bash verify.sh {number_of_words}
```

Where `number_of_words` is a variable specifying how many words from the beginning of the file will be compared.

For example: 


```
bash verify.sh 1000
```

This will verify if the first `1000` words are the same across all seven files. 

This value should be less than or equal to the minimum number of words across all files. 

If no differences are detected, `OK!` will be printed; otherwise, the differences will be shown, and `ERROR!` will be displayed.

Alternatively, run:


```
bash verify_all.sh 
```

This will automatically call `verify.sh` with the maximum number of words possible.

### Disclaimers

- The scripts must be executed from the folder in which they are located.  

- The `dict.txt` file was extracted from the following source: [GitHub - Shreda/pentestTools: random-words.txt](https://github.com/Shreda/pentestTools/blob/master/random-words.txt)

- Note that the scripts create and launch a tmux session.
