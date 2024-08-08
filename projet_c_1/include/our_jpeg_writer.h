// #ifndef OUR_JPEG_WRITER_H
// #define OUR_JPEG_WRITER_H

// #include <stdlib.h>
// #include <stdio.h>
// #include <stdint.h>
// #include <bitstream.h>

// // #include "jpeg_writer.h"
// // #include "qtables.h"
// // #include "huffman.h"
// // #include "htables.h"

// enum color_component
// {
//     Y,
//     Cb,
//     Cr,
//     NB_COLOR_COMPONENTS,
// };

// /*
//     Type énuméré représentant les types de composantes fréquentielles (DC ou AC).
// */
// enum sample_type
// {
//     DC,
//     AC,
//     NB_SAMPLE_TYPES,
// };

// /*
//     Type énuméré représentant la direction des facteurs d'échantillonnage (H
//     pour horizontal, V pour vertical).
// */
// enum direction
// {
//     H,
//     V,
//     NB_DIRECTIONS,
// };

// #define writebyte(b) fputc((b), f) // sur 1 octet
// #define writeword(w)      \
//     writebyte((w) / 256); \
//     writebyte((w) % 256); // sur 2 octet

// struct jpeg
// {
//     struct bitstream *bitstream_j;
//     char *ppm_filename;
//     char *jpeg_filename;
//     uint32_t image_height;
//     uint32_t image_width;
//     uint8_t nb_components;
//     // TODO remettre la version perso
//     // struct table_huffman *huffman_table;
//     struct huff_table *huffman_table;
//     uint8_t *qtableY;
//     uint8_t *qtableC;
//     struct SOF0 *SOF0info; // sampling factor
// };

// /* les informations nécéssaire dans la section SOF0 pour l'en-tête */
// struct SOF0
// {
//     // Identifiant de composante Y
//     uint8_t IdY;
//     // Facteur d'échantillonnage
//     uint8_t HVY;
//     // Table de quantification Y
//     uint8_t QTY;
//     // Identifiant de composante Cb
//     uint8_t IdCb;
//     // Facteur d'échantillonnage
//     uint8_t HVCb;
//     // Table de quantification Cb
//     uint8_t QTCb;
//     // Identifiant de composante Cr
//     uint8_t IdCr;
//     // Facteur d'échantillonnage
//     uint8_t HVCr;
//     // Table de quantification Cr
//     uint8_t QTCr;
// };

// /* Alloue et retourne une nouvelle structure jpeg. */
// struct jpeg *jpeg_create(void);

// /*
//     Détruit une structure jpeg.
//     Toute la mémoire qui lui est associée est libérée.
// */
// void jpeg_destroy(struct jpeg *jpg);

// /*
//     Ecrit tout l'en-tête JPEG dans le fichier de sortie à partir des
//     informations contenues dans la structure jpeg passée en paramètre.
//     En sortie, le bitstream est positionné juste après l'écriture de
//     l'en-tête SOS, à l'emplacement du premier octet de données brutes à écrire.
// */
// void jpeg_write_header(struct jpeg *jpg);

// /* Ecrit le footer JPEG (marqueur EOI) dans le fichier de sortie. */
// void jpeg_write_footer(struct jpeg *jpg);

// /*
//     Retourne le bitstream associé au fichier de sortie enregistré
//     dans la structure jpeg.
// */
// struct bitstream *jpeg_get_bitstream(struct jpeg *jpg);

// /****************************************************/
// /* Gestion des paramètres de l'encodeur via le jpeg */
// /****************************************************/

// /* Ecrit le nom de fichier PPM ppm_filename dans la structure jpeg. */
// void jpeg_set_ppm_filename(struct jpeg *jpg, const char *ppm_filename_j);

// /* Retourne le nom de fichier PPM lu dans la structure jpeg. */
// char *jpeg_get_ppm_filename(struct jpeg *jpg);

// /* Ecrit le nom du fichier de sortie jpeg_filename dans la structure jpeg. */
// void jpeg_set_jpeg_filename(struct jpeg *jpg, const char *jpeg_filename_j);

// /* Retourne le nom du fichier de sortie lu depuis la structure jpeg. */
// char *jpeg_get_jpeg_filename(struct jpeg *jpg);

// /*
//     Ecrit la hauteur de l'image traitée, en nombre de pixels,
//     dans la structure jpeg.
// */
// void jpeg_set_image_height(struct jpeg *jpg, uint32_t image_height_j);

// /*
//     Retourne la hauteur de l'image traitée, en nombre de pixels,
//     lue dans la structure jpeg.
// */
// uint32_t jpeg_get_image_height(struct jpeg *jpg);

// /*
//     Ecrit la largeur de l'image traitée, en nombre de pixels,
//     dans la structure jpeg.
// */
// void jpeg_set_image_width(struct jpeg *jpg, uint32_t image_width_j);

// /*
//     Retourne la largeur de l'image traitée, en nombre de pixels,
//     lue dans la structure jpeg.
// */
// uint32_t jpeg_get_image_width(struct jpeg *jpg);

// /*
//     Ecrit le nombre de composantes de couleur de l'image traitée
//     dans la structure jpeg.
// */
// void jpeg_set_nb_components(struct jpeg *jpg, uint8_t nb_components_j);

// /*
//     Retourne le nombre de composantes de couleur de l'image traitée
//     lu dans la structure jpeg.
// */
// uint8_t jpeg_get_nb_components(struct jpeg *jpg);

// /*
//     Ecrit dans la structure jpeg le facteur d'échantillonnage sampling_factor
//     à utiliser pour la composante de couleur cc et la direction dir.
// */
// void jpeg_set_sampling_factor(struct jpeg *jpg, enum color_component cc, enum direction dir, uint8_t sampling_factor);

// /*
//     Retourne le facteur d'échantillonnage utilisé pour la composante
//     de couleur cc et la direction dir, lu dans la structure jpeg.
// */
// uint8_t jpeg_get_sampling_factor(struct jpeg *jpg, enum color_component cc, enum direction dir);

// /*
//     Ecrit dans la structure jpeg la table de Huffman huff_table à utiliser
//     pour encoder les données de la composante fréquentielle acdc, pour la
//     composante de couleur cc.
// */
// void jpeg_set_huffman_table(struct jpeg *jpg, enum sample_type acdc, enum color_component cc, struct huff_table *htable);

// /*
//     Retourne un pointeur vers la table de Huffman utilisée pour encoder
//     les données de la composante fréquentielle acdc pour la composante
//     de couleur cc, lue dans la structure jpeg.
// */
// struct huff_table *jpeg_get_huffman_table(struct jpeg *jpg, enum sample_type acdc, enum color_component cc);

// /*
//     Ecrit dans la structure jpeg la table de quantification à utiliser
//     pour compresser les coefficients de la composante de couleur cc.
// */
// void jpeg_set_quantization_table(struct jpeg *jpg, enum color_component cc, uint8_t *qtable);

// /*
//     Retourne un pointeur vers la table de quantification associée à la
//     composante de couleur cc, lue dans a structure jpeg.
// */
// uint8_t *jpeg_get_quantization_table(struct jpeg *jpg, enum color_component cc);

// #endif /* OUR_JPEG_WRITER_H */