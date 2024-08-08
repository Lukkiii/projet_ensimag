#! /bin/sh

# Auteur : gl41
# Version initiale : 09/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_codegen_valide () {
    # $1 = premier argument.
    if !./../../main/bin/decac "$1" 2>&1 | grep "tomate"
    then
        echo "\e[31mEchec inattendu\e[0m sur $1."
    else
        echo "\e[32mSucces attendu\e[0m sur $1."
    fi
} 

echo "-- \e[32mVALID\e[0m --"
for dossier in src/test/deca/codegen/valid/3_*
do 
    for cas_de_test in $dossier/*.deca
    do
        test_codegen_valide "$cas_de_test"
    done
done