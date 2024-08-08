#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#include <init_MCU.h>
#include <DCT.h>
#include <RLE.h>
#include <zig-zag.h>
#include <fonction_huffman.h>
#include <jpeg_final.h>
#include <math.h>

#include "jpeg_writer.h"
#include <qtables.h>
#include <bitstream.h>
#include <htables.h>

#define debug

//Cette fonction se charge de la gestion globale du paramétrage de l'entête jpeg, puis de l'écriture du binary content et enfin du footer.
void convertPpm(char *image_name, char *new_name)
{

    // on récupère les data du fichier binaire .ppm : on stocke tout à la suite dans image->all_data ()
    struct image_ppm *image = pgm_to_MCU(image_name);

    // colored indique si l'image est de type P6 (colorée) ou P5 (non colorée).
    bool colored = false;

    if (image->p_type == 6)
    {
        colored = true;
    }

    uint64_t *no_mcu_cour = 0;
#ifndef debug
    printf("\nParamétrage de l'entête jpeg en cours \n");
#endif

    struct jpeg *jpeg_inv = jpeg_create();

    /* mise à jour du nom de struct jpeg*/
    char *pgm = ".pgm";
    char *ppm = ".ppm";
    char *jpg = ".jpg";
    char *space = "";
    char *point = ".";
    char *raw_name = strtok(image_name, point);
    // char *extension = ((new_name == NULL)? jpg : strtok(new_name, jpg));
    // printf("Extension : %s\n", extension);
    jpeg_set_jpeg_filename(jpeg_inv, strcat((new_name == NULL) ? image_name : new_name, (new_name == NULL) ? jpg : space));
    jpeg_set_ppm_filename(jpeg_inv, strcat(raw_name, colored ? ppm : pgm));

#ifndef debug
    printf("hauteur : %u", image->haut);
#endif
    /* màj tailleS de struct jpeg*/
    jpeg_set_image_height(jpeg_inv, image->haut);
    jpeg_set_image_width(jpeg_inv, image->larg);
    /* màj du nombre de composantes de couleurs (1, 2 ou 3) */
    if (colored)
    {
        jpeg_set_nb_components(jpeg_inv, 3);
    }
    else
    {
#ifndef debug
        printf("On a une seule composante\n");
#endif
        jpeg_set_nb_components(jpeg_inv, 1);
    }
    /* màj du sampling factor */
    jpeg_set_sampling_factor(jpeg_inv, Y, H, 1);
    jpeg_set_sampling_factor(jpeg_inv, Y, V, 1);

    if (colored)
    {
        jpeg_set_sampling_factor(jpeg_inv, Cb, H, 1);
        jpeg_set_sampling_factor(jpeg_inv, Cb, V, 1);

        jpeg_set_sampling_factor(jpeg_inv, Cr, H, 1);
        jpeg_set_sampling_factor(jpeg_inv, Cr, V, 1);
    }

    /*màj des tables de huffman à utiliser : pour DC puis AC */
    /* table AC - Y */

    // Récupération des tables de Huffman utilisées, pour pouvoir les écrires dans l'en-tête
    struct huff_table *htable_DC_Y = huffman_table_build(htables_nb_symb_per_lengths[DC][Y], htables_symbols[DC][Y], htables_nb_symbols[DC][Y]);
    struct huff_table *htable_AC_Y = huffman_table_build(htables_nb_symb_per_lengths[AC][Y], htables_symbols[AC][Y], htables_nb_symbols[AC][Y]);

    jpeg_set_huffman_table(jpeg_inv, DC, Y, htable_DC_Y);
    jpeg_set_huffman_table(jpeg_inv, AC, Y, htable_AC_Y);

    // if (colored){
    struct huff_table *htable_DC_Cb = huffman_table_build(htables_nb_symb_per_lengths[DC][Cb], htables_symbols[DC][Cb], htables_nb_symbols[DC][Cb]);
    struct huff_table *htable_AC_Cb = huffman_table_build(htables_nb_symb_per_lengths[AC][Cb], htables_symbols[AC][Cb], htables_nb_symbols[AC][Cb]);

    struct huff_table *htable_DC_Cr = huffman_table_build(htables_nb_symb_per_lengths[DC][Cr], htables_symbols[DC][Cr], htables_nb_symbols[DC][Cr]);
    struct huff_table *htable_AC_Cr = huffman_table_build(htables_nb_symb_per_lengths[AC][Cr], htables_symbols[AC][Cr], htables_nb_symbols[AC][Cr]);

    jpeg_set_huffman_table(jpeg_inv, DC, Cb, htable_DC_Cb);
    jpeg_set_huffman_table(jpeg_inv, AC, Cb, htable_AC_Cb);

    jpeg_set_huffman_table(jpeg_inv, DC, Cr, htable_DC_Cr);
    jpeg_set_huffman_table(jpeg_inv, AC, Cr, htable_AC_Cr);
    // }

    /* màj de la table de quantification à utiliser pour chaque
    composante de couleur cc */

    jpeg_set_quantization_table(jpeg_inv, Y, quantification_table_Y);

    if (colored)
    {
        jpeg_set_quantization_table(jpeg_inv, Cb, quantification_table_CbCr);
        jpeg_set_quantization_table(jpeg_inv, Cr, quantification_table_CbCr);
    }

    /* écriture de l'en-tête jpeg à partir du struct jpeg paramétré
    correctement */
    jpeg_write_header(jpeg_inv);
#ifndef debug
    printf("L'en-tête a correctement été écrite. \n");
#endif

    struct bitstream *stream_raw = jpeg_get_bitstream(jpeg_inv);
#ifndef debug
    printf("Le bitstream pour l'écriture du contenu binaire a été correctement créé\n");
#endif

    int *compt = calloc(1, sizeof(int *));
    *compt = 0;
    uint8_t *nb_bits = calloc(1, sizeof(uint8_t *));
    uint32_t *chemin = calloc(1, sizeof(uint32_t *));
    uint32_t *indice_magnitude = calloc(1, sizeof(uint32_t *));
    uint8_t *state = calloc(1, sizeof(uint8_t *));

    // GESTION DES ETATS POUR L'ECRITURE :

    // classique => state = 0
    // seize_zero => state = 160
    // end => state = 1

    *state = 160;

    // POUR Y : On garde de côté le dernier coefficient DC, et on enregistre la diff avec le dernier DC, à encoder RLE :
    int16_t *last_DC = calloc(3, sizeof(int16_t));
    int16_t diff_with_last_DC = 0;
    image->mcu_courant->DC_RLE = calloc(2, sizeof(uint8_t));

    uint32_t ind_max = colored ? 3 * (image->nb_mcu_haut * image->nb_mcu_larg) : (image->nb_mcu_haut * image->nb_mcu_larg);
#ifndef debug
    // Ecriture de tous les MCU
    printf("Nb de mcu à écrire : %u\n", ind_max / 3);
#endif

    for (uint64_t i = 0; i < ind_max; i++)
    {

        // màj du numéro de MCU en cours de traitement
        no_mcu_cour = colored ? (i / 3) : i;

        // col_num vaut 4 (arbitraire) si l'image est grise, ou la valeur de la couleur (cf enum) si l'image est colorée
        uint8_t color_num = colored ? (i % 3) : 4;
#ifndef debug
        printf("COLOR NUM : %u\n", color_num);
#endif

        /* l'appel à load_next_tab va permettre de charger un nouveau tableau 8x8 dans image->mcu_courant, avec la conversion RGB->YCbCr */
        load_next_tab(image, no_mcu_cour, color_num); // on màj image->mcu_courant;
#ifndef debug
        printf("\nEncodage du MCU n°%lu\n", i);
#endif

        image = data_encoder(image, color_num); // on réalise les traitements d'encodage successifs sur mcu_courant;

        int place_in_last_DC = colored ? (i % 3) : 0;

        diff_with_last_DC = *image->mcu_courant->element_DC - last_DC[place_in_last_DC]; // màj de la différence avec le DC du MCU précédent.
#ifndef debug
        printf("Nouveau DC : %i, ancien DC : %i -> nouvelle valeur de diff_with_last_DC : %i\n", *image->mcu_courant->element_DC, last_DC[i % 3], diff_with_last_DC);
#endif
        last_DC[place_in_last_DC] = *image->mcu_courant->element_DC;

        *state = 1;
#ifndef debug
        printf("valeur de compt : %d \n", *compt);
        printf("State : %u \n", *state);
#endif

        /* on traite l'élément DC */
        image->mcu_courant->DC_RLE = calloc(2, sizeof(int16_t));

        // printf("double : %f", (double)(abs(diff_with_last_DC) + 1));

        int16_t magnitude = (int16_t)floor(log2((double)(abs(diff_with_last_DC))) + 1);
        int16_t indice = get_indice(diff_with_last_DC, magnitude);
#ifndef debug
        printf("\nPour DC : Indice : %i, magnitude : %i\n", indice, magnitude);
#endif

        image->mcu_courant->DC_RLE[0] = magnitude;
        image->mcu_courant->DC_RLE[1] = indice;
#ifndef debug
        printf("\nDC.mag = %u, Dc.index = %u\n", image->mcu_courant->DC_RLE[0], image->mcu_courant->DC_RLE[1]);
#endif

        // Gestion des écritures pour DC avec appel des bonnes tables
        if (!colored || color_num == 0)
        {
            *chemin = huffman_table_get_path(htable_DC_Y, image->mcu_courant->DC_RLE[0], nb_bits);
        }
        else if (color_num == 1)
        {
            *chemin = huffman_table_get_path(htable_DC_Cb, image->mcu_courant->DC_RLE[0], nb_bits);
        }
        else if (color_num == 2)
        {
            *chemin = huffman_table_get_path(htable_DC_Cr, image->mcu_courant->DC_RLE[0], nb_bits);
        }
#ifndef debug
        printf("\nBinaire pour DC : \n");
#endif

        bitstream_write_bits(stream_raw, *chemin, *nb_bits, false);
        // printf("Wrote %u (path) on %u nb_bits\n", *chemin, *nb_bits);
        bitstream_write_bits(stream_raw, image->mcu_courant->DC_RLE[1], image->mcu_courant->DC_RLE[0], false);
        // printf("Wrote %u (index) on %u nb_bits\n", image->mcu_courant->DC_RLE[1], image->mcu_courant->DC_RLE[0]);

        // j va permettre un parcours sur le tableau contenant le nb de bits sur lequel écrire l'amplitude
        int j = 0;
#ifndef debug
        printf("taille RLE : %i\n", *image->mcu_courant->taille_RLE);
        printf("Binaire pour AC : \n");
#endif
        *compt = 0;
        while (*compt != *(image->mcu_courant->taille_RLE))
        {
            // printf("Valeur compt = %i VS taille_RLE = %i\n", *compt, *image->mcu_courant->taille_RLE);
            uint8_t *nb_bits_index = calloc(sizeof(uint8_t *), 1);
            *nb_bits_index = image->mcu_courant->tab_magnitude[j];

            // màj des pointeurs par huffman_mcu_i(...) qui est appelée avec les bonnes tables en argument
            if (!colored || color_num == 0)
            {
                huffman_mcu_i(image->mcu_courant, compt, nb_bits, chemin, indice_magnitude, state, htable_AC_Y);
            }
            else if (color_num == 1)
            {
                huffman_mcu_i(image->mcu_courant, compt, nb_bits, chemin, indice_magnitude, state, htable_AC_Cb);
            }
            else if (color_num == 2)
            {
                huffman_mcu_i(image->mcu_courant, compt, nb_bits, chemin, indice_magnitude, state, htable_AC_Cr);
            }

            if (*state == 0)
            {
                bitstream_write_bits(stream_raw, *chemin, *nb_bits, false);
#ifndef debug
                printf("Wrote %u (path) on %u nb_bits\n", *chemin, *nb_bits);
#endif
                *compt += 2;

                uint8_t *nb_bits_index = calloc(sizeof(uint8_t *), 1);

                *nb_bits_index = image->mcu_courant->tab_magnitude[j];

                bitstream_write_bits(stream_raw, *indice_magnitude, *nb_bits_index, false);
#ifndef debug
                printf("Wrote %u (index) on %u nb_bits\n", *indice_magnitude, *nb_bits_index);
#endif
                j = j + 1;
            }
            else if (*state == 160)
            {
                *compt += 1;
                bitstream_write_bits(stream_raw, *chemin, *nb_bits, false);
#ifndef debug
                printf("Wrote %u (path) on %u nb_bits\n", chemin, *nb_bits);
#endif
            }
            else if (*state == 1)
            {
                bitstream_write_bits(stream_raw, *chemin, *nb_bits, false);
#ifndef debug
                printf("Wrote %u (path) on %u nb_bits\n", *chemin, *nb_bits);
                printf("Final state : %u\n", *state);
#endif

                break;
            }
            // printf("\n");
        }
#ifndef debug
        printf("Fin de l'écriture du bloc %li\n\n", i);
#endif
    }
    /* écriture de la fin de l'image, balise EOI */
    jpeg_write_footer(jpeg_inv);

    /* flush le bitstream pour procéder à l'écriture dans le fichier .jpg */
    bitstream_flush(stream_raw);

    huffman_table_destroy(htable_DC_Y);
    huffman_table_destroy(htable_AC_Y);

    // if(colored){
    huffman_table_destroy(htable_DC_Cb);
    huffman_table_destroy(htable_AC_Cb);

    huffman_table_destroy(htable_DC_Cr);
    huffman_table_destroy(htable_AC_Cr);

    /* fin de l'écriture du contenu brut */

    // /* une fois que le jpeg a été écrit, on supprimer le struct */
    jpeg_destroy(jpeg_inv);
    free_MCU(image->mcu_courant);
#ifndef debug
    printf("\nFin de la génération du .jpg \n ");
#endif
}

// TODO changer le code de data_to_encode, pour qu'il appelle les fonctions sur mcu_courant

/* data_encoder travaille sur le MCU chargé dans image->mcu_courant qui a été préalablement
 chargé par load_next_MCU */
struct image_ppm *data_encoder(struct image_ppm *image, uint8_t color_num)
{

    // on calcule la DCT sur la matrice 8x8 de uint8_t que l'on stocke
    // dans mcu-> tab_DCT : tableau de 8 pointeurs allant chacun vers un tableau de 8 int16_t
    fct_dct(image->mcu_courant);
#ifndef debug
    printf("\nAffichage matrice à la suite de la DCT \n");
    print_matrice_dct(image->mcu_courant);
#endif

    // on fait le parcours en zig-zag des coeff + quantification
    trans_zigzag(image->mcu_courant);
#ifndef debug
    printf("\nAffichage matrice à la suite de zig-zag \n");
    print_matrice_dct(image->mcu_courant);
#endif

    // on fait la quantification à l'issue du zig-zag
    quantification(image->mcu_courant, color_num);
#ifndef debug
    printf("\nAffichage matrice à la suite de la quantification \n");
    print_matrice_dct(image->mcu_courant);
#endif

    // on fait le parcours des 63 coeff AC pour faire le codage RLE que l'on sotocke
    // dans image->data->tab_ordonne_AC.
    fct_RLE(image->mcu_courant);
#ifndef debug
    printf("\nAffichage matrice à la suite de RLE :\n");

    printf("\nFin du traitement du MCU : passage de la main au générateur de .jpg\n\n");
#endif

    return (image);
}

void load_next_tab(struct image_ppm *image, uint64_t *no_mcu_cour, uint8_t color_num)
{
#ifndef debug
    printf("n°mcu : %hhu\n", no_mcu_cour);
#endif

    if (color_num > 2)
    {
        // printf("On fait des attributions pour les images gris\n");
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                uint32_t indice_courant = (uint32_t)64 * image->nb_mcu_larg * ((uint32_t)no_mcu_cour / image->nb_mcu_larg) + 8 * ((uint32_t)no_mcu_cour % image->nb_mcu_larg) + j + i * 8 * image->nb_mcu_larg;
#ifndef debug
                printf("%02x ", image->all_data[indice_courant]);
#endif
                image->mcu_courant->tab[i][j] = image->all_data[indice_courant];
            }
#ifndef debug
            printf("\n");
#endif
        }
    }
    else
    {
        // printf("On fait des attributions pour les images couleur : %u \n", color_num);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                uint32_t indice_courant = (uint32_t)3 * 64 * image->nb_mcu_larg * ((uint32_t)no_mcu_cour / image->nb_mcu_larg) + 8 * 3 * ((uint32_t)no_mcu_cour % image->nb_mcu_larg) + 3 * j + i * 3 * 8 * image->nb_mcu_larg;
                uint8_t *pixel_courant = calloc(3, sizeof(uint8_t));

                pixel_courant[0] = image->all_data[indice_courant];
                pixel_courant[1] = image->all_data[indice_courant + 1];
                pixel_courant[2] = image->all_data[indice_courant + 2];

                if (color_num == 0)
                {
                    image->mcu_courant->tab[i][j] = 0.299 * pixel_courant[0] + 0.587 * pixel_courant[1] + 0.114 * pixel_courant[2];
                }
                else if (color_num == 1)
                {
                    image->mcu_courant->tab[i][j] = (uint8_t)(-0.1687 * pixel_courant[0] - 0.3313 * pixel_courant[1] + 0.5 * pixel_courant[2] + 128);
                }
                else if (color_num == 2)
                {
                    image->mcu_courant->tab[i][j] = (uint8_t)(0.5 * pixel_courant[0] - 0.4187 * pixel_courant[1] - 0.0813 * pixel_courant[2] + 128);
                }
#ifndef debug
                printf("%02x ", image->mcu_courant->tab[i][j]);
#endif
                free(pixel_courant);
            }
#ifndef debug
            printf("\n");
#endif
        }
    }
}
