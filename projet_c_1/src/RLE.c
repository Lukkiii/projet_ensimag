#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdint.h>
#include "init_MCU.h"

#define debug

int16_t get_indice(int nombre, int magnitude)
{
    /*renvoie l'indice de la classe de la magnitude */
    int puis_magnitude = pow(2, magnitude);
    if (nombre < 0)
    {
        return nombre + puis_magnitude - 1;
    }
    else
    {
        return nombre;
    }
}

int notonly_zero(int16_t *tab, int i)
{
    /* vérifie si il n'y pas que des zeros après */
    int j;
    for (j = i; j < 63; j++)
    {
        if (tab[j] != 0)
        {
            return 1;
        }
    }
    return 0;
}

void fct_RLE(struct MCU *mcu)
{
    int i;
    uint8_t *total_res = calloc(126, sizeof(uint8_t *));
    uint8_t *tab_magnitude_courant = calloc(63, sizeof(uint8_t *));
    int compteur = 0;
    int compteur_magnitude = 0;
    int16_t *tab_a_encoder = mcu->tab_ordonne_AC;

    int compteur_total = 0;

    for (i = 0; i < 63; i++)
    {
        uint8_t magnitude = floor(log2(abs(tab_a_encoder[i])) + 1);

        if (tab_a_encoder[i] == 0)
        {
            if (compteur == 15 && notonly_zero(tab_a_encoder, i))
            {
                compteur = 0;
                total_res[compteur_total] = 255;
                compteur_total++;
            }
            else
            {
                compteur = compteur + 1;
            }
        }
        else
        {
            if (compteur == 0)
            {
                total_res[compteur_total] = magnitude;
                compteur_total++;
                uint8_t indice = get_indice(tab_a_encoder[i], magnitude);
                total_res[compteur_total] = indice;
                compteur_total++;
                tab_magnitude_courant[compteur_magnitude] = magnitude;
                compteur_magnitude++;
            }
            else
            {

                compteur = compteur << 4;
                total_res[compteur_total] = compteur + magnitude;
                compteur_total++;

                uint8_t indice = get_indice(tab_a_encoder[i], magnitude);
                total_res[compteur_total] = indice;
                compteur_total++;
                tab_magnitude_courant[compteur_magnitude] = magnitude;
                compteur_magnitude++;
                compteur = 0;
            }
        }
        // printf("Dernier ajouté, incide %i : %u \n",i, total_res[i-1]);
    }

    if (compteur != 0)
    {
        total_res[compteur_total] = 0;
        compteur_total++;
    }
/*supprime les 10 dernier éléments du tableau total_res */
/*affiche la valeur de compteur_total */
#ifndef debug
    printf("\nNombre de symboles : DC + AC %d\n", compteur_total);
#endif
    /*affiche le tableau tab_magnitude */
    // for (i = 0; i < 10; i++)
    // {
    //     printf("%u ", tab_magnitude[i]);
    // }

    /* on remplace la valeur de mcu->tab_RLD par le tableau total_res */

    /* on traite l'élément DC */
    uint8_t magnitude = floor(log2(abs(*mcu->element_DC)) + 1);
    uint8_t indice = get_indice(*mcu->element_DC, magnitude);

    mcu->DC_RLE[0] = magnitude;
    mcu->DC_RLE[1] = indice;
#ifndef debug
    printf("\nDC.mag = %u, Dc.index = %u\n", mcu->DC_RLE[0], mcu->DC_RLE[1]);
#endif

    /* on traite remplace tab_RLE par le tableau total_res */

    mcu->tab_RLE = total_res;

    *(mcu->taille_RLE) = compteur_total;
    mcu->tab_magnitude = tab_magnitude_courant;
}