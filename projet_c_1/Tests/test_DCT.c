#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <init_MCU.h>
#include <DCT.h>


void main()
{
    struct MCU *mat_dct = mcu_create();
    /*remplis le tableau AC de mcu avec des 1 */

    uint8_t mat_dct_invader[8][8] = {
        {0, 0, 0, 255, 255, 0, 0, 0},
        {0, 0, 255, 255, 255, 255, 0, 0},
        {0, 255, 255, 255, 255, 255, 255, 0},
        {255, 255, 0, 255, 255, 0, 255, 255},
        {255, 255, 255, 255, 255, 255, 255, 255},
        {
            0,
            0,
            255,
            0,
            0,
            255,
            0,
            0,
        },
        {0, 255, 0, 255, 255, 0, 255, 0},
        {255, 0, 255, 0, 0, 255, 0, 255}};

    /* test de phi */

    int test_valeur_courante = mat_dct_invader[0][2] - 128;
    printf("valeur de la matrice : de %d ",mat_dct_invader[0][2]);
    printf("\n");
    printf("valeur courante : %d \n", test_valeur_courante);



    int16_t test_phi = phi(0, 0, *mat_dct);
    printf("%d\n", test_phi);

    mat_dct->tab=mat_dct_invader;
    print_matrice(mat_dct);

    fct_dct(mat_dct);
    print_matrice_dct(mat_dct);
}
