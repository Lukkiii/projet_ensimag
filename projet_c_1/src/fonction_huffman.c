
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include "init_MCU.h"
#include "huffman.h"
#include "htables.h"
#include <fonction_huffman.h>

void huffman_mcu_i(struct MCU *mcu, int *i, uint8_t *nb_bits, uint32_t *chemin, uint32_t *indice_magnitude, uint8_t *state, struct huff_table *htable)
{
    /* la fonction fait une màj des pointeurs vers chemin, nb bist et magnitude à partir de la valeur à coder, issue de RLE */

    // struct huff_table *table_h = huffman_table_build(htables_nb_symb_per_lengths[AC][Y], htables_symbols[AC][Y], htables_nb_symbols[AC][Y]);

    if (mcu->tab_RLE[*i] == 255) 
    {
        /* renvoie simplement le chemin de la valeur -1  et le nombre de bit */
        *state = 160;
        *chemin = huffman_table_get_path(htable, 240, nb_bits);

    }
    else if (mcu->tab_RLE[*i] == 0)
    {   
        *state = 1;
        *chemin = huffman_table_get_path(htable, 0, nb_bits);
        /* return END et le chemin du 0 et son nb de bit*/
    }
    else
    {
        /* renvoie le chemin de la valeur, le nombre de bit et le nouvel indice i */
        /* renvoie aussi l'indice de magnitude et son nombre de bit*/
        *state = 0;
        *chemin = huffman_table_get_path(htable, mcu->tab_RLE[*i], nb_bits);
        *indice_magnitude = mcu->tab_RLE[*i + 1];
    }
    // huffman_table_destroy(table_h);
    // // printf("Chemin de huffman : %u \n", *chemin);
}

