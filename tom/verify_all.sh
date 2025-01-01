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

bash verify.sh $number_of_lines
