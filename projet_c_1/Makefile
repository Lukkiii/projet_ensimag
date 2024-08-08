CC = gcc
LD = gcc

# -O0 désactive les optimisations à la compilation
# C'est utile pour débugger, par contre en "production"
# on active au moins les optimisations de niveau 2 (-O2).
CFLAGS = -Wall -Wextra -std=c99 -Iinclude/ -O0 -g -pg

# Par défaut, on compile tous les fichiers source (.c) qui se trouvent dans le
# répertoire src/
SRC_FILES=$(wildcard src/*.c)
TESTS_DIR=Tests

# Par défaut, la compilation de src/toto.c génère le fichier objet obj/toto.o
OBJ_FILES=$(patsubst src/%.c,obj/%.o,$(SRC_FILES))

# Fichiers objet "prof". Lorsque vous aurez implémenté un de ces modules, il
# faudra le retirer de cette liste pour lier ppm2jpeg à votre implémentation du
# module en question. Le module htables_prof.o, qui contient la déclaration des tables
# de Huffman génériques sous forme de constantes, n'est pas à ré-implémenter.
OBJ_PROF_FILES = obj_prof/huffman_prof.o     \
				 obj_prof/jpeg_writer_prof.o \
				 obj_prof/bitstream_prof.o \
				 obj_prof/htables_prof.o


#  obj_prof/jpeg_writer_prof.o \#
				

all: ppm2jpeg

ppm2jpeg: $(OBJ_FILES) $(OBJ_PROF_FILES)
	$(LD) $(OBJ_FILES) $(OBJ_PROF_FILES) $(LDFLAGS) -o $@ -lm
	

debug:$(OBJ_FILES) $(OBJ_PROF_FILES)
	$(LD) $(OBJ_FILES) $(OBJ_PROF_FILES) $(LDFLAGS) -o $@ -lm -Ddebug

obj/%.o: src/%.c
	$(CC) -c $(CFLAGS) $< -o $@

.PHONY: clean

clean:
	rm -rf ppm2jpeg debug $(OBJ_FILES)

# realclean:
# 	rm -rf ppm2jpeg debut $(OBJ_FILES) images/%.jpg

test_init_MCU: obj/init_MCU.o | $(TESTS_DIR)/obj
	$(CC) $(TESTS_DIR)/test_init_MCU.c $(CFLAGS) src/init_MCU.c -o $(TESTS_DIR)/obj/$@ 

$(TESTS_DIR)/obj:
	mkdir -p $@

# test_ppm:
# 	$(CC) src/ppm2jpeg.c $(CFLAGS)

bla:
	gcc src/ppm2jpeg.c src/jpeg_final.c $(OBJ_PROF_FILES) -o src/init_MCU.o $(CFLAGS) -lm
