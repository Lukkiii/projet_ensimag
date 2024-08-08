#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <jpeg_writer.h>
// #include <our_jpeg_writer.h>

#include <init_MCU.h>
#define debug

/* Ce module va se charger d'initialiser un tab[][] de taille 8x8
qui contiendra les valeurs lues dans le fichier .pgm;
pour le moment, on ne se préoccupe pas des valeurs */

/* créer un struct mcu et initialise les valeurs de son tableau à 0*/
struct MCU *mcu_create(void)
{
    struct MCU *matrice = malloc(sizeof(struct MCU));

    *matrice->tab = calloc(8, sizeof(uint8_t *));
    matrice->tab_ordonne_AC = calloc(63, sizeof(uint8_t *));
    *matrice->tab_DCT = calloc(8, sizeof(int16_t *));
    matrice->tab_RLE = calloc(126, sizeof(uint8_t *));
    matrice->DC_RLE = calloc(2, sizeof(uint8_t *));
    matrice->tab_magnitude = calloc(63, sizeof(uint8_t *));
    matrice->element_DC = (int16_t *)malloc(sizeof(int16_t) * 1);
    matrice->taille_RLE = malloc(sizeof(int));
    /* initialise les valeurs de tab mcu à 0 */
    for (size_t i = 0; i < 8; i++)
    {
        matrice->tab[i] = (uint8_t *)calloc(8, sizeof(uint8_t));
    }

    /* initialise les valeurs de tab_DCT à 0 */
    for (size_t i = 0; i < 8; i++)
    {
        matrice->tab_DCT[i] = (int16_t *)calloc(8, sizeof(int16_t));
    }

    return matrice;
}

struct image_ppm *i_ppm_create(void)
{
    struct image_ppm *image = malloc(sizeof(struct image_ppm));
    image->larg = 0;
    image->haut = 0;
    image->val_max = 0;
    image->mcu_courant = mcu_create();
    return image; //
}

/* ouvre un fichier .pgm et transfert le contenu de la bitmap dans un tableau 1D */

struct image_ppm *pgm_to_MCU(char *filename)
{
    FILE *f = fopen(filename, "r");

#ifndef debug
    printf("Ouverture du fichier %s \n", filename);
#endif

    struct image_ppm *image = i_ppm_create();

    fscanf(f, "P%u", &(image->p_type));
    fscanf(f, " %u %u ", &(image->larg), &(image->haut));
    fscanf(f, "%hhu ", &(image->val_max));

#ifndef debug
    printf("Hauteur init : %u, larg init : %u\n", image->haut, image->larg);
#endif

    // Cas image grise
    if (image->p_type == 5)
    {
#ifndef debug
        printf("P type : %u\n", image->p_type);
#endif
        if (image->haut % 8 != 0 || image->larg % 8 != 0)
        {

            uint32_t ajout_lignes = 8 - image->haut % 8;
            uint32_t ajout_cols = 8 - image->larg % 8;

            uint64_t nouv_size = (image->haut + ajout_lignes) * (image->larg + ajout_cols);

            image->all_data = calloc(nouv_size, sizeof(uint8_t));

            // Complétion des lignes déjà existantes
            for (int compt_line = 0; compt_line < image->haut; compt_line++)
            {
                fread(&(image->all_data[compt_line * (image->larg + ajout_cols)]), sizeof(uint8_t), image->larg, f);
                uint32_t index = image->larg + compt_line * (ajout_cols + image->larg);
                uint8_t to_repeat = image->all_data[index - 1];
                for (int ajout = 0; ajout < ajout_cols; ajout++)
                {
                    image->all_data[index + ajout] = to_repeat;
                }
            }
            // Création des lignes en bas des existantes
            for (int compt_line = 0; compt_line < ajout_lignes; compt_line++)
            {
                int base = (image->haut + compt_line) * (image->larg + ajout_cols);
                for (int i = 0; i < image->larg + ajout_cols; i++)
                {
                    image->all_data[base + i] = image->all_data[(base + i) - (image->larg + ajout_cols)];
                }
            }
            // màj des attributs du struct après redimensionnement
            image->haut = image->haut + ajout_lignes;
            image->larg = image->larg + ajout_cols;
            image->nb_mcu_haut = (image->haut + ajout_lignes) / 8;
            image->nb_mcu_larg = (image->larg + ajout_cols) / 8;
        }
        else
        {
            image->all_data = calloc(image->haut * image->larg, sizeof(uint8_t));

            fread(image->all_data, sizeof(uint8_t), image->haut * image->larg, f);

            image->nb_mcu_haut = image->haut / 8;
            image->nb_mcu_larg = image->larg / 8;
        }
    }
    // cas image en couleurs
    else
    {
#ifndef debug
        printf("P type : %u\n", image->p_type);
#endif
        if (image->haut % 8 != 0 || image->larg % 8 != 0)
        {
// Cas redimensionnement de l'image
#ifndef debug
            printf("Redimensionnement en cours ... \n");
#endif
            uint32_t ajout_lignes = (image->haut % 8 > 0) ? 8 - image->haut % 8 : 0;
            uint32_t ajout_cols = (image->larg % 8 > 0) ? 8 - image->larg % 8 : 0;

            uint64_t nouv_size = (image->haut + ajout_lignes) * (image->larg + ajout_cols);

            image->all_data = calloc(3 * nouv_size, sizeof(uint8_t));

            // Complétion des lignes déjà existantes
            for (int compt_line = 0; compt_line < image->haut; compt_line++)
            {

                fread(&(image->all_data[compt_line * 3 * (image->larg + ajout_cols)]), sizeof(uint8_t), 3 * image->larg, f);

                uint32_t index = 3 * (image->larg + compt_line * (ajout_cols + image->larg));

                uint8_t *to_repeat[3];
                to_repeat[0] = image->all_data[index - 3];
                to_repeat[1] = image->all_data[index - 2];
                to_repeat[2] = image->all_data[index - 1];

                for (int ajout = 0; ajout < ajout_cols; ajout++)
                {
                    image->all_data[index + 3 * ajout] = to_repeat[0];
                    image->all_data[index + 3 * ajout + 1] = to_repeat[1];
                    image->all_data[index + 3 * ajout + 2] = to_repeat[2];
                }
            }
            // Création des lignes en bas des existantes
            for (int compt_line = 0; compt_line < ajout_lignes; compt_line++)
            {

                int base = 3 * (image->haut + compt_line) * (image->larg + ajout_cols);

                for (int i = 0; i < 3 * (image->larg + ajout_cols); i++)
                {

                    image->all_data[base + i] = image->all_data[(base + i) - 3 * (image->larg + ajout_cols)];
                }
            }
            // màj des attributs du struct après redimensionnement
            image->haut = image->haut + ajout_lignes;
            image->larg = image->larg + ajout_cols;

            image->nb_mcu_haut = image->haut / 8;
            image->nb_mcu_larg = image->larg / 8;
        }
        else
        {
            // cas image couleur sans redimensionnement
            image->all_data = calloc(3 * image->haut * image->larg, sizeof(uint8_t));

            fread(image->all_data, sizeof(uint8_t), 3 * image->haut * image->larg, f);

            image->nb_mcu_haut = image->haut / 8;
            image->nb_mcu_larg = image->larg / 8;
        }
    }
#ifndef debug
    printf("image->nb_mcu_haut : %u, image->nb_mcu_larg : %u\n", image->nb_mcu_haut, image->nb_mcu_larg);
    printf("image->haut: %u, image->larg : %u\n", image->haut, image->larg);
    printf("Les données de l'image ont bien été stockées. \n");
#endif
    return image;
}

void free_MCU(struct MCU *matrix)
{
    // free(matrix->tab);
    free(matrix);
}

void print_matrice(struct MCU *matrice)
{

    int i;
    int j;
    for (i = 0; i < 8; i++)
    {
        for (j = 0; j < 8; j++)
        {
            printf("%d ", matrice->tab[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}

void print_matrice_dct(struct MCU *matrice)
{

    int i;
    int j;
    for (i = 0; i < 8; i++)
    {
        for (j = 0; j < 8; j++)
        {
            printf("%04x ", matrice->tab_DCT[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}