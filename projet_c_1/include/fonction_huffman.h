#ifndef _FONCTION_HUFFMAN_
#define _FONCTION_HUFFMAN_

#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include "init_MCU.h"
#include "huffman.h"
#include "htables.h"
#include <fonction_huffman.h>


void huffman_mcu_i(struct MCU *mcu, int *i, uint8_t *nb_bits, uint32_t *chemin, uint32_t *indice_magnitude, uint8_t *state, struct huff_table *htable);

#endif /* fonction_huffman_H */
