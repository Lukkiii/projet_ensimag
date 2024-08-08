#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include "init_MCU.h"
#include "huffman.h"
#include "htables.h"

void main(void)
{
    struct MCU *mcu = mcu_create();
    /* remplis le tableau AC de mcu avec des nombres entre 10 et 200 */
    for (int i = 0; i < 63; i++)
    {
        mcu->tab_ordonne_AC[i] = rand() % 200 + 10;
    }
    /* affiche le tableau AC de mcu */
    for (int i = 0; i < 63; i++)
    {
        printf("%d\n", mcu->tab_ordonne_AC[i]);
    }
    huffman_mcu(mcu);
    /* affiche le tableau AC de mcu */
    for (int i = 0; i < 63; i++)
    {
        printf("%d\n", mcu->tab_ordonne_AC[i]);
    }
    return EXIT_SUCCESS;
}
