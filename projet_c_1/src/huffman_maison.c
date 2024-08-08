/* Pour trouver les formules nécessaires au calcul des chemins, nous avons reçu l'aide de Jérémy Carneau qui a pu
nous expliquer le principe algorithmique. */

// #include <stdint.h>
// #include "htables.h"
// #include <stdio.h>
// #include <stdlib.h>
// #include <math.h>
// #include "huffman_maison.h"


// int calcul_chemin(int pronfondeur, int nb_noeud, int indice_symbole)
// {
//     return pow(2, pronfondeur) - nb_noeud + indice_symbole;
// }
// int calcul_nb_noeud_p_1(int nb_noeud_p, int nb_feuille_p)
// {
//     return 2 * (+nb_noeud_p - nb_feuille_p);
// }

// struct table_huffman *creation_tab(uint8_t *nb_symb_per_lengths,
//                                               uint8_t *symbols,
//                                               uint8_t nb_symbols)
// {
//     /* on commence par créer le tableau du nombre de noeud par profondeur */
//     struct table_huffman *tab_huff = malloc(sizeof(struct table_huffman));
//     tab_huff->tab_symbols = calloc(500, sizeof(uint32_t));
//     int *nb_noeud_p = calloc(500, sizeof(int));
//     nb_noeud_p[0] = 1; /* pour la profondeur 0 on a 0 noeud sinon l'arbre n'existe pas */
//     for (int i = 1; i < 20; i++)
//     {
//         nb_noeud_p[i] = calcul_nb_noeud_p_1(nb_noeud_p[i - 1], htables_nb_symb_per_lengths[AC][Y][i - 1]);
//     }
//     /* on crée le tableau des chemin */10000000
//     /* affichons le tableau nb_noeud_p */

//     /* On crée le tableau des chemin */
//     uint32_t *tab_chemin = calloc(250, sizeof(uint32_t));

//     // /* on affiche symbols */
//     // for(int i=0;i<500;i++){
//     //     printf("%d\n",symbols[i]);

//     // }



//     int flag=0;
//     int j = 0;
//     for (int i = 0; i < 20; i++)
//     {
//         int compteur = 0;
//         while (compteur < nb_symb_per_lengths[i])
//         {
            
//             if (flag==0 | symbols[j]!=0){
//             tab_chemin[symbols[j]] = calcul_chemin(i, nb_noeud_p[i], compteur);
//             }
//             if (symbols[j]==0){
//                 flag=1;
//             }
            
//             compteur++;
//             j++;
//             }
//         }
    

//     tab_huff->tab_symbols = tab_chemin;
// }

// void affiche_tab_symbols(struct table_huffman *tab_huff)
// {
//     for (int i = 0; i < 300; i++)
//     {
//         printf("%d : %u\n", i, tab_huff->tab_symbols[i]);
//     }
// }

// uint32_t get_chemin_huff(struct table_huffman *tab_huff, uint8_t symbole, uint8_t *nb_bits)
// {
//     /* Calcul du nombre de bit nécessaire à l'écriture du chemin */
//     *nb_bits = floor(log2(tab_huff->tab_symbols[symbole])) + 1;
//     return tab_huff->tab_symbols[symbole];
// }

// void huffman_destroy(struct table_huffman *tab_huff)
// {
//     free(tab_huff->tab_symbols);
//     free(tab_huff);
// }