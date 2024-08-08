#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdint.h>
#include "init_MCU.h"
#include "RLE.h"

void main(void)
{

    /*Test de la fonction RLE*/
    struct MCU *mcu = mcu_create();

    /* on remplis le tableau AC avec des valeurs randoms */
    for (size_t i = 0; i < 63; i++)
    {
        if (i<20 || i>40)
        {
            mcu->tab_ordonne_AC[i] = 0;
        }
        else{
            mcu->tab_ordonne_AC[i] = rand() % 256;
        }
    }
    /* on remplis le tableau DC avec des valeurs randoms */

    mcu->element_DC[0]=5;



    /* on affiche le tableau AC */
    for (size_t i = 0; i < 63; i++)
    {
        printf("%d ", mcu->tab_ordonne_AC[i]);
    }
    fct_RLE(mcu);
    printf("\n");

    /* on affiche le tableau RLE */
    for (size_t i = 0; i < *mcu->taille_RLE; i++)
    {
        printf("0x%x ", mcu->tab_RLE[i]);
    }
    printf("\n");
<<<<<<< HEAD
    int j;
    /* on affiche le tableau de DC_RLE */
    printf("%u,%u",mcu->DC_RLE[0],mcu->DC_RLE[1])
}
=======
    /* on affiche le tableau des magnitudes */
    printf("ceci est le tableau des magnitudes : \n");
    for (size_t i = 0; i < 63; i++)
    {
        printf("%d ", mcu->tab_magnitude[i]);
    }
    printf("\n");
}
>>>>>>> ed4e1e56446c9bd33aeb2b44d4814d7ea413e484
