#! /bin/sh

# Auteur : gl41
# Version initiale : 09/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "\e[32mEchec attendu\e[0m pour test_synt sur $1."
    else
        echo "\e[31mSucces inattendu\e[0m de test_synt sur $1."
    fi
}  

test_synt_valide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "\e[31mEchec inattendu\e[0m pour test_synt sur $1."
    else
        echo "\e[32mSucces attendu\e[0m de test_synt sur $1."
    fi
} 

echo "-- \e[31mINVALID\e[0m --"
for dossier in src/test/deca/syntax/invalid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        test_synt_invalide "$cas_de_test"
    done
done

echo "-- \e[32mVALID\e[0m --"
for dossier in src/test/deca/syntax/valid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        test_synt_valide "$cas_de_test"
    done
done