# Checks if the output files:

# L1201.txt 
# L1203.txt 
# L1204.txt 
# L1205.txt
# L1206.txt 
# L1207.txt

# Are equal up to the first `$1` lines - that is, the value given as argument
# Make sure: $1 <= min { cat L120i.txt | wc -l }, for 1 <= i <= 7, i != 2. 

if test "$#" -ne 1; then
  echo "Invalid number of arguments"
  echo "Usage: \`number of lines to compare\`"
  exit 0
fi

numberMachines=7
startIP="1"
room="L120"

for ((i = 0; i < $numberMachines; i++)); 
do 
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  head -n $1 output/$room$nextIP.txt > output/$room$nextIP.comp
done

GREEN='\033[1;32m'
RED='\033[1;31m'
NC='\033[0m'

for ((i = 1; i < $numberMachines; i++)); 
do 
  if [ "$i" -eq "1" ]; then
      continue
  fi
  nextIP=$((startIP + i))
  diff output/$room$startIP.comp output/$room$nextIP.comp > file.diff
  if [ $? -ne 0 ]; then
      cat file.diff
      echo -e "${RED}ERROR! ${NC}"
      exit 1
  fi
done

echo -e "${GREEN}OK! ${NC}"
