#include <stdint.h>
#include "htables.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "huffman.h"
#include "huffman_maison.h"

/*on affiche la table des symboles pour AC et Y*/

void main(void)
{

    /* on compare les valeurs du chemin venant de la fonction get_chemin_huff et de la fonction huffman_table_get_path */

    struct table_huffman *tab_huff = creation_tab(htables_nb_symb_per_lengths[AC][Y], htables_symbols[AC][Y], htables_nb_symbols[AC][Y]);
    struct huff_table *tab_huff_prof = huffman_table_build(htables_nb_symb_per_lengths[AC][Y], htables_symbols[AC][Y], htables_nb_symbols[AC][Y]);
    uint8_t *nbbits = calloc(1, sizeof(uint8_t *));
    printf("Le chemin de 0 est %d",get_chemin_huff(tab_huff,0,nbbits));
    for (uint8_t i = 0; i < 250; i++)
    {
        if (get_chemin_huff(tab_huff, i, nbbits) != 0)
        {
            printf("%u : (%d,%d)", i, get_chemin_huff(tab_huff, i, nbbits), huffman_table_get_path(tab_huff_prof, i, nbbits));
            get_chemin_huff(tab_huff, i, nbbits);
            printf("nombre de bit écriture : %d\n", *nbbits);
            printf("\n");
            if (get_chemin_huff(tab_huff, i, nbbits) != huffman_table_get_path(tab_huff_prof, i, nbbits))
            {
                printf("ERREUR\n");
            }
        }
    }
    /* affiche la valeur du chemin pour le symbole 162 */
    printf("%u : %u\n", 162, get_chemin_huff(tab_huff, 150, nbbits));
    printf("job done");
    huffman_destroy(tab_huff);
    huffman_table_destroy(tab_huff_prof);
    printf("\n");
    /* affiche la valeur du chemin pour le symbole 0 */
    // printf("%u : %u\n", 0, get_chemin_huff(tab_huff, 0, nbbits));
    // printf("nombre de bit écriture : %d\n", *nbbits);
    // printf("\n");
    // /* affiche la valeur du chemin pour le symbole 162 */
    // printf("%u : %u\n", 162, get_chemin_huff(tab_huff, 150, nbbits));
    // printf("nombre de bit écriture : %d\n", *nbbits);
}