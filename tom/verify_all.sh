# Checks if the output files:

# L802.txt 
# L803.txt 
# L804.txt 
# L805.txt
# L806.txt 
# L807.txt

# Are equal up to the maximum number of lines possible - min { cat L80i.txt | wc -l }

numberMachines=6
startIP="2"
room="L80"

number_of_lines=-1

for ((i = 0; i < $numberMachines; i++)); 
do 
  nextIP=$((startIP + i))
  lines=$(cat output/$room$nextIP.txt | wc -l)
  number_of_lines=$(( number_of_lines == -1 || number_of_lines > lines ? lines : number_of_lines ))
done

# Calls verify.sh with the maximum number of lines possible
bash verify.sh $number_of_lines
