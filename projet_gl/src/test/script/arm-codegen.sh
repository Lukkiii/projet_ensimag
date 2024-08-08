#! /bin/sh

# Auteur : Yung Pheng THOR
# Version initiale : 12/01/2024

# Pour chaque fichier .deca dans test/deca/codegen/valid/*, on 
# lance ima dessus, et on compare le résultat avec la valeur attendue.

arm_env_dir=$1
compile_deca_arm() {
  arm-linux-gnueabi-gcc --sysroot=$arm_env_dir $1 -o "${1%.*}" -ldeca
}
alias qemu-deca-arm="qemu-arm -L $arm_env_dir/usr/arm-linux-gnueabi/"

DATE='/bin/date'

BEFORE=$($DATE +'%s')

cd "$(dirname "$0")"/../../.. || exit 1 # on est sur gl41

PATH=./src/test/script/launchers:./src/main/bin:"$PATH" # add launcher and bin into path to include decac and test_(?)

total=0
success=0

# for loop to run through all deca file in deca/codegen folder
for folder in src/test/deca/codegen/valid/*
do
    for testfile in $folder/*.deca
    do  
        # Use awk to extract lines below "Resultats:" without leading "//" and spaces until an empty line
        attendu=$(awk '/Resultats:/ {flag=1; next} flag && /^\/\/ *$/ {flag=0; next} flag {sub(/^ *\/\/ */, ""); print}' $testfile)
        # Get the name of the compiled file in the same location as its deca file
        compiledfile="${testfile%????}s"
        compiled="${testfile%????}"
        # Remove any .ass file compiled previously
        rm -f $compiledfile 2>/dev/null

        total=$(($total + 1)) 

        error_file="/tmp/error.txt"
        if  decac -arm $testfile > $error_file 2>&1 | grep "FATAL*" $error_file # true on error
        then 
            echo "\e[31mEchec inattendu\e[0m pour compilation :   ${testfile#*/*/*/*/*/}"
        else 
            if [ ! -f $compiledfile ]; 
            then
                echo "\e[31mEchec inattendu\e[0m Fichier non généré : ${compiledfile#*/*/*/*/*/}"
            else 
                compile_deca_arm $compiledfile
                resultat=$(echo "eee" | qemu-deca-arm $compiled) 
                if [ "$resultat" = "" ]; # true on error TO BE continued
                then 
                    echo "\e[31mEchec inattendu\e[0m qemu :                ${compiledfile#*/*/*/*/*/}"
                else 
                    if [ "$resultat" = "$attendu" ];
                    then 
                        echo "\e[32mSucces attendu\e[0m :                     ${testfile#*/*/*/*/*/}"
                        success=$(($success + 1)) 
                    else 
                        echo "\e[31mEchec inattendu\e[0m de ima :             ${testfile#*/*/*/*/*/} :"
                        echo "Résultat : $resultat   ---   Attendu : $attendu"
                    fi
                fi
                rm -f $compiledfile
            fi
        fi 
    done
done

if [ "$success" = "$total" ];
then 
    echo "BILAN : \e[32m$success / $total\e[0m"
else 
    if [ "$success" -gt "$((3*$total/4))" ];
    then
        echo "BILAN : \e[33m$success / $total\e[0m"
    else
        echo "BILAN : \e[31m$success / $total\e[0m"
    fi
fi

AFTER=$($DATE +'%s')

ELAPSED=$(($AFTER-$BEFORE))

echo "Temps : " $ELAPSED " secondes"