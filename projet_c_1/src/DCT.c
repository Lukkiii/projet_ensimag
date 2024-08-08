#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "init_MCU.h"

// float C_inter(int epsilon)
// {
//     /* C_inter est la constante de quantification de l'intermediaire */
//     if (epsilon == 0)
//     {
//         matrice_dct[i] = calloc(8, sizeof(float));
//     }
//     /* Initialisation de la matrice de travail */
//     for (size_t i = 0; i < 8; i++)
//     {
//         for (size_t j = 0; j < 8; j++)
//         {
//             matrice_dct[i][j] = matrice->tab[i][j] - 128;
//         }
//     }
//     else
//     {
//         return 1;
//     }
// }

// int16_t phi(int i, int j, struct MCU *matrice)
// {
//     /* calcul la somme phi du cosinus discret */
//     float C_i = C_inter(i);
//     float C_j = C_inter(j);
//     int x;
//     int y;
//     float somme = 0;
//     for (x = 0; x < 8; x++)
//     {
//         for (y = 0; y < 8; y++)
//         {
//             int val_courante = matrice->tab[x][y] - 128;

//             somme = somme + val_courante* cosf(((2 * x + 1) * i * 3.14) / 16) * cosf(((2 * y + 1) * j * 3.14) / 16);
//         }
//     }
//     somme = somme * C_i * C_j * (0.25);
//     return (somme);
// }

// void fct_dct(struct MCU *matrice)
// {
//     /* fonction qui calcule les coefficients DCT  et les mets dans la nouvelle matrice DCT de int16*/
//     int i;
//     int j;

//     for (i = 0; i < 8; i++)
//     {
//         for (j = 0; j < 8; j++)
//         {
//             matrice->tab_DCT[i][j] = phi(i, j, matrice);
//         }
//     }
// }

/*Version plus rapide de l'algorithme de DCT qui s'inspire de l'algortihme de Arai, Agui, Nakajima, 1984*/

/* https://www.nayuki.io/res/fast-discrete-cosine-transform-algorithms/fast-dct-8.c */

/* fonction qui calcule les coefficients DCT d'un vecteur de 8 et qui les mets dans un nouveau vecteur de 8 */
/* https://web.stanford.edu/class/ee398a/handouts/lectures/07-TransformCoding.pdf#page=30 */

static float S[] = {
    0.353553390593273762200422,
    0.254897789552079584470970,
    0.270598050073098492199862,
    0.300672443467522640271861,
    0.353553390593273762200422,
    0.449988111568207852319255,
    0.653281482438188263928322,
    1.281457723870753089398043,
};

static float A[] = {
    0,
    0.707106781186547524400844,
    0.541196100146196984399723,
    0.707106781186547524400844,
    1.306562964876376527856643,
    0.382683432365089771728460,
};
/* On applique d'abord l'algorithme de DCT sur les lignes puis sur les colonnes */
void fct_dct(struct MCU *matrice)
{
    /* Initialisation de la matrice de travail */
    float *matrice_dct[8];
    *matrice_dct = calloc(8, sizeof(float *));
    for (size_t i = 0; i < 8; i++)
    {
        matrice_dct[i] = calloc(8, sizeof(float));
    }
    /* Initialisation de la matrice de travail */
    for (size_t i = 0; i < 8; i++)
    {
        for (size_t j = 0; j < 8; j++)
        {
            matrice_dct[i][j] = matrice->tab[i][j] - 128;
        }
    }
    /* On applique l'algorithme de DCT sur les lignes */
    for (size_t i = 0; i < 8; i++)
    {
        float v0 = matrice_dct[i][0] + matrice_dct[i][7];
        float v1 = matrice_dct[i][1] + matrice_dct[i][6];
        float v2 = matrice_dct[i][2] + matrice_dct[i][5];
        float v3 = matrice_dct[i][3] + matrice_dct[i][4];
        float v4 = matrice_dct[i][3] - matrice_dct[i][4];
        float v5 = matrice_dct[i][2] - matrice_dct[i][5];
        float v6 = matrice_dct[i][1] - matrice_dct[i][6];
        float v7 = matrice_dct[i][0] - matrice_dct[i][7];

        float v8 = v0 + v3;
        float v9 = v1 + v2;
        float v10 = v1 - v2;
        float v11 = v0 - v3;
        float v12 = -v4 - v5;
        float v13 = (v5 + v6) * A[3];
        float v14 = v6 + v7;

        float v15 = v8 + v9;
        float v16 = v8 - v9;
        float v17 = (v10 + v11) * A[1];
        float v18 = (v12 + v14) * A[5];

        float v19 = -v12 * A[2] - v18;
        float v20 = v14 * A[4] - v18;

        float v21 = v17 + v11;
        float v22 = v11 - v17;
        float v23 = v13 + v7;
        float v24 = v7 - v13;

        float v25 = v19 + v24;
        float v26 = v23 + v20;
        float v27 = v23 - v20;
        float v28 = v24 - v19;

        matrice_dct[i][0] = S[0] * v15;
        matrice_dct[i][1] = S[1] * v26;
        matrice_dct[i][2] = S[2] * v21;
        matrice_dct[i][3] = S[3] * v28;
        matrice_dct[i][4] = S[4] * v16;
        matrice_dct[i][5] = S[5] * v25;
        matrice_dct[i][6] = S[6] * v22;
        matrice_dct[i][7] = S[7] * v27;
    }

    /* On applique l'algorithme de DCT sur les colonnes */
    for (size_t j = 0; j < 8; j++)
    {
        float v0 = matrice_dct[0][j] + matrice_dct[7][j];
        float v1 = matrice_dct[1][j] + matrice_dct[6][j];
        float v2 = matrice_dct[2][j] + matrice_dct[5][j];
        float v3 = matrice_dct[3][j] + matrice_dct[4][j];
        float v4 = matrice_dct[3][j] - matrice_dct[4][j];
        float v5 = matrice_dct[2][j] - matrice_dct[5][j];
        float v6 = matrice_dct[1][j] - matrice_dct[6][j];
        float v7 = matrice_dct[0][j] - matrice_dct[7][j];

        float v8 = v0 + v3;
        float v9 = v1 + v2;
        float v10 = v1 - v2;
        float v11 = v0 - v3;
        float v12 = -v4 - v5;
        float v13 = (v5 + v6) * A[3];
        float v14 = v6 + v7;

        float v15 = v8 + v9;
        float v16 = v8 - v9;
        float v17 = (v10 + v11) * A[1];
        float v18 = (v12 + v14) * A[5];

        float v19 = -v12 * A[2] - v18;
        float v20 = v14 * A[4] - v18;

        float v21 = v17 + v11;
        float v22 = v11 - v17;
        float v23 = v13 + v7;
        float v24 = v7 - v13;

        float v25 = v19 + v24;
        float v26 = v23 + v20;
        float v27 = v23 - v20;
        float v28 = v24 - v19;

        matrice_dct[0][j] = S[0] * v15;
        matrice_dct[1][j] = S[1] * v26;
        matrice_dct[2][j] = S[2] * v21;
        matrice_dct[3][j] = S[3] * v28;
        matrice_dct[4][j] = S[4] * v16;
        matrice_dct[5][j] = S[5] * v25;
        matrice_dct[6][j] = S[6] * v22;
        matrice_dct[7][j] = S[7] * v27;
    }
    /* On peut maintenant remplacer la DCT dans la MCU
     * par la matrice de DCT, "matrice_dct" */
    for (size_t i = 0; i < 8; i++)
    {
        for (size_t j = 0; j < 8; j++)
        {
            matrice->tab_DCT[i][j] = (int16_t)matrice_dct[i][j];
        }
    }
}
