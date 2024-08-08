#ifndef _INIT_MCU_H_
#define _INIT_MCU_H_

#include <stdint.h>
#include <stdio.h>

#include "jpeg_writer.h"

struct MCU{
    int16_t *tab_ordonne_AC; /* De taille 63, il ne contient que les composants AC */
    int16_t *element_DC; /* Pointe vers l'élements DC du MCU */
    uint8_t *tab[8]; /*Pixel récupéré de l'image PGM */
    int16_t *tab_DCT[8]; /* De taille 64, contient les coefficients DCT */
    uint8_t *tab_RLE; /*De taille entre 1 et 126, contient les élements AC passé avec le codage RLE */
    int *taille_RLE; /*De taille entre 1 et 126, contient les tailles des élements AC passé avec le codage RLE */
    int16_t *DC_RLE;
    uint8_t *tab_magnitude; /*De taille entre 1 et 63, contient les différentes magnitudes dans l'ordre pour pouvoir effectuer le bitstream */
};

struct image_ppm{
    uint8_t p_type;
    uint32_t larg;
    uint32_t haut;
    uint8_t val_max;
    struct MCU *mcu_courant;
    uint32_t nb_mcu_larg; //nombre de mcu en largeur
    uint32_t nb_mcu_haut; //nombre de mcu en hauteur
    uint8_t *all_data;
};



struct MCU *mcu_create(void);
struct image_ppm *i_ppm_create(void);



/* dans la situation initiale où l'on souhaite convertir des images de taille 8x8 en pgm
vers jpeg, cette fonction s'occupe de lire le fichier .pgm pour créer une matrice
content les valeurs lues */

struct image_ppm *pgm_to_MCU(char *filename);
void free_MCU(struct MCU *matrix);

void print_matrice(struct MCU *matrice);
void print_matrice_dct(struct MCU *matrice);



#endif /* _INIT_MCU_H_ */