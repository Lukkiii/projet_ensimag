#! /bin/sh

# Auteur : gl41
# Version initiale : 09/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_context_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "\e[32mEchec attendu\e[0m pour test_context sur $1."
    else
        echo "\e[31mSucces inattendu\e[0m de test_context sur $1."
    fi
}  

test_context_valide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "\e[31mEchec inattendu\e[0m pour test_context sur $1."
    else
        echo "\e[32mSucces attendu\e[0m de test_context sur $1."
    fi
} 

echo "-- \e[31mINVALID\e[0m --"
for dossier in src/test/deca/context/invalid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        test_context_invalide "$cas_de_test"
    done
done

echo "-- \e[32mVALID\e[0m --"
for dossier in src/test/deca/context/valid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        test_context_valide "$cas_de_test"
    done
done