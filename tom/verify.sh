if test "$#" -ne 1; then
  echo "Invalid number of arguments"
  echo "Usage: \`number of lines to compare\`"
  exit 0
fi

numberMachines=6
startIP="2"
room="L80"

for ((i = 0; i < $numberMachines; i++)); 
do 
  nextIP=$((startIP + i))
  head -n $1 output/$room$nextIP.txt > output/$room$nextIP.comp
done

for ((i = 1; i < $numberMachines; i++)); 
do 
  nextIP=$((startIP + i))
  diff output/$room$startIP.comp output/$room$nextIP.comp
done
