#! /bin/sh

# Auteur : gl41
# Version initiale : 09/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

echo "-- LEXER --"

total=0
success=0

for cas_de_test in src/test/deca/lexer/invalid/*.deca
do
    if test_lex "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        success=$(($success + 1))
    fi
    total=$(($total + 1))
done
echo "  INVALID : $success / $total"

total=0
success=0

for cas_de_test in src/test/deca/lexer/valid/*.deca
do
    if ! test_lex "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        success=$(($success + 1))
    fi
    total=$(($total + 1))
done

echo "  VALID : $success / $total"

echo "\n-- SYNTAX --"

total=0
success=0

for dossier in src/test/deca/syntax/invalid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        if test_synt "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
        then
            success=$(($success + 1))
        fi
        total=$(($total + 1))
    done
done
echo "  INVALID : $success / $total"

total=0
success=0

for dossier in src/test/deca/syntax/valid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        if ! test_synt "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
        then
            success=$(($success + 1))
        fi
        total=$(($total + 1))
    done

done

echo "  VALID : $success / $total"

total=0
success=0

for cas_de_test in src/test/deca/sansObjet/*.deca
do 
    if ! test_synt "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        success=$(($success + 1))
    fi
    total=$(($total + 1))

done
echo "  SANS OBJET : $success / $total"


echo "\n-- CONTEXT --"

total=0
success=0
for dossier in src/test/deca/context/invalid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        if test_context "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
        then
            success=$(($success + 1))
        fi
        total=$(($total + 1))
    done    

done
echo "  INVALID : $success / $total"

total=0
success=0

for dossier in src/test/deca/context/valid/*
do 
    for cas_de_test in $dossier/*.deca
    do
        if ! test_context "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
        then
            success=$(($success + 1))
        fi
        total=$(($total + 1))
    done

done
echo "  VALID : $success / $total"

total=0
success=0

for cas_de_test in src/test/deca/sansObjet/*.deca
do 
    if ! test_context "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        success=$(($success + 1))
    fi
    total=$(($total + 1))

done
echo "  SANS OBJET : $success / $total"

# echo "\n-- CODE GEN --"

# total=0
# success=0

# for dossier in src/test/deca/codegen/valid/*
# do 
#     for cas_de_test in $dossier/*.deca
#     do
#         if ./../../../main/bin/decac "$cas_de_test" 2>&1 | grep "at"
#         then
#             success=$(($success + 1))
#         fi
#         total=$(($total + 1))
#     done

# done
# echo "  VALID : $success / $total"