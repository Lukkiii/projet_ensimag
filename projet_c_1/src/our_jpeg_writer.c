// #include <stdlib.h>
// #include <stdio.h>
// #include <string.h>

// #include "our_jpeg_writer.h"
// // #include "huffman_maison.h"
// #include "qtables.h"
// #include "huffman.h"
// #include "htables.h"
// #include <bitstream.h>

// // #define writebyte(b) fputc((b), f)
// // #define writeword(w) writebyte((w)/256); writebyte((w)%256); \


// /* Alloue et retourne une nouvelle structure jpeg. */
// struct jpeg *jpeg_create(void){
//     struct jpeg *jpg = malloc(sizeof(struct jpeg));
//     // jpg->bitstream_j = malloc(sizeof(struct bitstream));
//     jpg->ppm_filename = malloc(sizeof(char*));
//     jpg->jpeg_filename = malloc(sizeof(char*));
// jpeg_set_sampling_factor (jpg=0x555555563160, cc=Y, dir=H, sampling_factor=1 '\001') at src/jpeg_writer.c:430
//     jpg->image_height = 0;
//     jpg->image_width = 0;
//     jpg->nb_components = 0;
//     jpg->huffman_table = NULL;
//     jpg->qtableY = NULL;
//     jpg->qtableC = NULL;
//     jpg->SOF0info = malloc(sizeof(struct SOF0));
//     // jpg->SOF0info->HVY = calloc(1, sizeof(int));
//     jpg->SOF0info->IdY = 1;
//     jpg->SOF0info->IdCb = 2;
//     jpg->SOF0info->IdCr = 3;
//     jpg->SOF0info->QTY = 0;
//     jpg->SOF0info->QTCb = 1;
//     jpg->SOF0info->QTCr = 1;
//     jpg->SOF0info->HVY = 0;
//     jpg->SOF0info->HVCb = 0;
//     jpg->SOF0info->HVCr = 0;
// }

// /*
//     Détruit une structure jpeg. 
//     Toute la mémoire qui lui est associée est libérée.
// */
// void jpeg_destroy(struct jpeg *jpg){
//     free(jpg);
//     if(! jpg->bitstream_j){
//         free(jpg->bitstream_j);
//     }
//     if(! jpg->ppm_filename){
//         free(jpg->ppm_filename);
//     }
//     if(! jpg->jpeg_filename){
//         free(jpg->jpeg_filename);
//     }
//     if(! jpg->huffman_table){
//         free(jpg->huffman_table);
//     }
//     if(! jpg->qtableY){
//         free(jpg->qtableY);
//     }
//     if(! jpg->qtableC){
//         free(jpg->qtableC);
//     }
// }

// /*
//     Ecrit tout l'en-tête JPEG dans le fichier de sortie à partir des
//     informations contenues dans la structure jpeg passée en paramètre. 
//     En sortie, le bitstream est positionné juste après l'écriture de 
//     l'en-tête SOS, à l'emplacement du premier octet de données brutes à écrire.
// */

// void jpeg_write_header(struct jpeg *jpg){
    
//     FILE * f = fopen(jpg->jpeg_filename, "wb");

//     //SOI : FFD8
//     int16_t SOI_marker = 0xFFD8;
//     writeword(SOI_marker);

//     //APP0 : marker(FFE0), length(16), JFIFsignature[5], versionhi, versionlo, 
//     //      xyunits, xdensity, ydensity, thumbnwidth, thumbnheight
//     int16_t APP0_marker = 0xFFE0;
//     int16_t APP0_len = 16;
//     char APP0_versionhi, APP0_versionlo = 1;
//     char APP0_xyunits = 0;
//     int16_t APP0_xdensity, APP0_ydensity = 0;
//     char APP0_thumbnwidth, APP0_thumbnheight = 0;

//     writeword(APP0_marker);
//     writeword(APP0_len);
//     writebyte('J');
//     writebyte('F');
//     writebyte('I');
//     writebyte('F');
//     writebyte(0);
//     writebyte(APP0_versionhi); // Version JFIF (1.1)
//     writebyte(APP0_versionlo); // Version JFIF (1.1)
//     writebyte(APP0_xyunits);
//     writeword(APP0_xdensity);
//     writeword(APP0_ydensity);
//     writebyte(APP0_thumbnwidth);
//     writebyte(APP0_thumbnheight);
    
//     //COM 
//     // int16_t COM_marker = 0xFFFE;
//     // writeword(COM_marker);

//     //DQT : marker(FFDB), length, QTY, Ytable[64], QTC, Ctable[64]
//     int16_t DQT_marker = 0xFFDB;
//     writeword(DQT_marker);
//     char DQT_QTYinfo = 0;

//     if(jpg->qtableC = NULL){

//         int16_t DQT_len = 2 + (1 + 64*1)*1;
//         writeword(DQT_len);
//         writebyte(DQT_QTYinfo);

//         for (int i = 0; i < 64; i++)
//             writebyte(jpg->qtableY[i]);
//     }
    
//     //color
//     if(jpg->qtableC != NULL){
//         int16_t DQT_len = 2 + (1 + 64*1)*2;
//         writeword(DQT_len);
//         writebyte(DQT_QTYinfo);

//         char DQT_QTCinfo = 1;
//         writebyte(DQT_QTCinfo);

//         for (int i = 0; i < 64; i++)
//             writebyte(jpg->qtableY[i]);
//         for (int i = 0; i < 64; i++)
//             writebyte(jpg->qtableC[i]);
//     }
    
    
//     //SOF0 : marker(FFC0), length(17), precision, height, width, nrofcomponents, 
//     //      IdY, HVY, QTY, IdCb, HVCb, QTCb, IdCr, HVCr, QTCr
//     int32_t SOF0_height = jpeg_get_image_height(jpg);
//     int32_t SOF0_width = jpeg_get_image_width(jpg);
//     uint8_t SOF0_nb_components = jpeg_get_nb_components(jpg);
//     int16_t SOF0_marker = 0xFFC0;
//     int16_t SOF0_len = 8 + SOF0_nb_components*3;
//     char SOF0_precision = 8;
    
//     writeword(SOF0_marker);
//     writeword(SOF0_len);
//     writebyte(SOF0_precision);
//     writeword(SOF0_height);
//     writeword(SOF0_width);
//     writebyte(SOF0_nb_components);
//     writebyte(jpg->SOF0info->IdY);
//     writebyte(jpg->SOF0info->HVY);
//     writebyte(jpg->SOF0info->QTY);
//     //color
//     if(SOF0_nb_components == 3){
//         writebyte(jpg->SOF0info->IdCb);
//         writebyte(jpg->SOF0info->HVCb);
//         writebyte(jpg->SOF0info->QTCb);
//         writebyte(jpg->SOF0info->IdCr);
//         writebyte(jpg->SOF0info->HVCr);
//         writebyte(jpg->SOF0info->QTCr);
//     }
    
    
//     //DHT(huffman) : marker(FFC4),length
//     int16_t DHT_marker = 0xFFC4;
//     uint8_t DHT_nb_components = jpeg_get_nb_components(jpg);

//     uint8_t n_Y_DC = htables_nb_symbols[DC][Y];
//     uint8_t n_Y_AC = htables_nb_symbols[AC][Y];
//     uint8_t n_Cb_DC = htables_nb_symbols[DC][Cb];
//     uint8_t n_Cb_AC = htables_nb_symbols[AC][Cb];
//     uint8_t n_Cr_DC = htables_nb_symbols[DC][Cr];
//     uint8_t n_Cr_AC = htables_nb_symbols[AC][Cr];
//     uint8_t YDC_values[n_Y_DC];
//     uint8_t YAC_values[n_Y_AC];
//     uint8_t CbDC_values[n_Cb_DC];
//     uint8_t CbAC_values[n_Cb_AC];
//     uint8_t CrDC_values[n_Cr_DC];
//     uint8_t CrAC_values[n_Cr_AC];

//     int16_t DHT_len;

//     char HT_YDCinfo = 0;
//     uint8_t YDC_codes[16];
//     char HT_YACinfo = 0x10;
//     uint8_t YAC_codes[16];
    
//     char HT_CbDCinfo = 1;
//     uint8_t CbDC_codes[16];
    
//     char HT_CbACinfo = 0x11;
//     uint8_t CbAC_codes[16];
   
//     char HT_CrDCinfo = 2;
//     uint8_t CrDC_codes[16];
    
//     char HT_CrACinfo = 0x12;
//     uint8_t CrAC_codes[16];

//     for (int i = 0; i < 16; i++){
//         YDC_codes[i] = htables_nb_symb_per_lengths[DC][Y][i];
//         YAC_codes[i] = htables_nb_symb_per_lengths[AC][Y][i];
//         CbDC_codes[i] = htables_nb_symb_per_lengths[DC][Cb][i];
//         CbAC_codes[i] = htables_nb_symb_per_lengths[AC][Cb][i];
//         CrDC_codes[i] = htables_nb_symb_per_lengths[DC][Cr][i];
//         CrAC_codes[i] = htables_nb_symb_per_lengths[AC][Cr][i];
//     }
        
//     for (int i = 0; i < n_Y_DC; i++)
//         YDC_values[i] = htables_symbols[DC][Y][i];
//     for (int i = 0; i < n_Y_AC; i++)
//         YAC_values[i] = htables_symbols[AC][Y][i];
//     for (int i = 0; i < n_Cb_DC; i++)
//         CbDC_values[i] = htables_symbols[DC][Cb][i];
//     for (int i = 0; i < n_Cb_AC; i++)
//         CbAC_values[i] = htables_symbols[AC][Cb][i];
//     for (int i = 0; i < n_Cr_DC; i++)
//         CrDC_values[i] = htables_symbols[DC][Cr][i];
//     for (int i = 0; i < n_Cr_AC; i++)
//         CrAC_values[i] = htables_symbols[AC][Cr][i];

//     writeword(DHT_marker);
//     if(DHT_nb_components == 1){
//         DHT_len = 2 + 17 + n_Y_DC + 17 +n_Y_AC;
//         writeword(DHT_len);
//         writebyte(HT_YDCinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(YDC_codes);        
//         for (int i = 0; i < n_Y_DC; i++)
//             writebyte(YDC_values);
//         //YAC
//         writebyte(HT_YACinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(YAC_codes);
//         for (int i = 0; i < n_Y_AC; i++)
//             writebyte(YAC_values);
//     }
//     if (DHT_nb_components == 3)
//     {
//         DHT_len = 2 + 17 + n_Y_DC + 17 + n_Y_AC + 17 + n_Cb_DC + 17 +n_Cb_AC + 17 + n_Cr_DC + 17 + n_Cr_AC;
//         writeword(DHT_len);
//         // YDC
//         writebyte(HT_YDCinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(YDC_codes);
//         for (int i = 0; i < n_Y_DC; i++)
//             writebyte(YDC_values);
//         // YAC
//         writebyte(HT_YACinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(YAC_codes);
//         for (int i = 0; i < n_Y_AC; i++)
//             writebyte(YAC_values);
//         // CbDC
//         writebyte(HT_CbDCinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(CbDC_codes);
//         for (int i = 0; i < n_Cb_DC; i++)
//             writebyte(CbDC_values);
//         // CbAC
//         writebyte(HT_CbACinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(CbAC_codes);
//         for (int i = 0; i < n_Cb_AC; i++)
//             writebyte(CbAC_values);
//         // CrDC
//         writebyte(HT_CrDCinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(YDC_codes);
//         for (int i = 0; i < n_Cr_DC; i++)
//             writebyte(YDC_values);
//         // CrAC
//         writebyte(HT_CrACinfo);
//         for (int i = 0; i < 16; i++)
//             writebyte(CrAC_codes);
//         for (int i = 0; i < n_Y_AC; i++)
//             writebyte(CrAC_values);
//     }

//     // SOS : marker(FFDA), length, nrofcomponents, IdY, HTY, IdCb, HTCb, IdCr, HTCr,
//     //       (Ss, Se, Bf)
//     int16_t SOS_marker = 0xFFDA;
//     uint8_t SOS_nb_components = jpeg_get_nb_components(jpg);
//     int16_t SOS_len = 6 + 2 * SOS_nb_components;
//     char SOS_IdY = 1;
//     char SOS_HTY = 0;
//     char SOS_IdCb = 2;
//     char SOS_HTCb = 0x11;
//     char SOS_IdCr = 3;
//     char SOS_HTCr = 0x11;
//     char SOS_Ss = 0;
//     char SOS_Se = 0x3F;
//     char SOS_Bf = 0;

//     writeword(SOS_marker);
//     writeword(SOS_len);
//     writebyte(SOS_nb_components);

//     if (SOS_nb_components == 1)
//     {
//         writebyte(SOS_IdY);
//         writebyte(SOS_HTY);
//     }
//     //color
//     if(SOS_nb_components == 3){
//         writebyte(SOS_IdY);
//         writebyte(SOS_HTY);
//         writebyte(SOS_IdCb);
//         writebyte(SOS_HTCb);
//         writebyte(SOS_IdCr);
//         writebyte(SOS_HTCr);
//         writebyte(SOS_Ss);
//         writebyte(SOS_Se);
//         writebyte(SOS_Bf);
//     }
    
// }

// /* Ecrit le footer JPEG (marqueur EOI) dans le fichier de sortie. */
// void jpeg_write_footer(struct jpeg *jpg){
//     //EOI : marker(FFD9)
//     FILE *f = fopen(jpg->jpeg_filename, "wb");
//     int16_t EOI_marker = 0xFFD9;

//     writeword(EOI_marker);
// }

// /*
//     Retourne le bitstream associé au fichier de sortie enregistré 
//     dans la structure jpeg.
// */
// struct bitstream *jpeg_get_bitstream(struct jpeg *jpg){
//     return jpg->bitstream_j;
// }

// /****************************************************/
// /* Gestion des paramètres de l'encodeur via le jpeg */
// /****************************************************/

// /* Ecrit le nom de fichier PPM ppm_filename dans la structure jpeg. */
// void jpeg_set_ppm_filename(struct jpeg *jpg, const char *ppm_filename_j){
//     int len = strlen(ppm_filename_j);
// 	// char *name = malloc(sizeof(char)*(len+1));

//     // strcpy(name, ppm_filename_j);
// 	jpg->ppm_filename = malloc(sizeof(char*));
//     jpg->ppm_filename = ppm_filename_j;
// }

// /* Retourne le nom de fichier PPM lu dans la structure jpeg. */
// char *jpeg_get_ppm_filename(struct jpeg *jpg){
//     return jpg->ppm_filename;
// }

// /* Ecrit le nom du fichier de sortie jpeg_filename dans la structure jpeg. */
// void jpeg_set_jpeg_filename(struct jpeg *jpg, const char *jpeg_filename){
//     int len = strlen(jpeg_filename);
// 	char *name = malloc(sizeof(char)*(len+1));
//     jpg->bitstream_j = bitstream_create(jpeg_filename);

//     strcpy(name, jpeg_filename);
// 	jpg->jpeg_filename = name;
// }

// /* Retourne le nom du fichier de sortie lu depuis la structure jpeg. */
// char *jpeg_get_jpeg_filename(struct jpeg *jpg){
//     return jpg->jpeg_filename;
// }

// /*
//     Ecrit la hauteur de l'image traitée, en nombre de pixels,
//     dans la structure jpeg.
// */
// void jpeg_set_image_height(struct jpeg *jpg, uint32_t image_height){
//     jpg->image_height = image_height;
// }
// struct jpeg;
// /*
//     Retourne la hauteur de l'image traitée, en nombre de pixels,
//     lue dans la structure jpeg.
// */
// uint32_t jpeg_get_image_height(struct jpeg *jpg){
//     return jpg->image_height;
// }

// /*
//     Ecrit la largeur de l'image traitée, en nombre de pixels,
//     dans la structure jpeg.
// */
// void jpeg_set_image_width(struct jpeg *jpg, uint32_t image_width){
//     jpg->image_width = image_width;
// }

// /*
//     Retourne la largeur de l'image traitée, en nombre de pixels,
//     lue dans la structure jpeg.
// */
// uint32_t jpeg_get_image_width(struct jpeg *jpg){
//     return jpg->image_width;
// }

// /*
//     Ecrit le nombre de composantes de couleur de l'image traitée
//     dans la structure jpeg.
// */
// void jpeg_set_nb_components(struct jpeg *jpg, uint8_t nb_components){
//     jpg->nb_components = nb_components;
// }

// /*
//     Retourne le nombre de composantes de couleur de l'image traitée 
//     lu dans la structure jpeg.
// */
// uint8_t jpeg_get_nb_components(struct jpeg *jpg){
//     return jpg->nb_components;
// }

// /*
//     Ecrit dans la structure jpeg le facteur d'échantillonnage sampling_factor
//     à utiliser pour la composante de couleur cc et la direction dir.
// */
// void jpeg_set_sampling_factor(struct jpeg *jpg, enum color_component cc, enum direction dir, uint8_t sampling_factor){
//     if(cc == Y && dir == H){
//         jpg->SOF0info->HVY = sampling_factor << 4;
//     }
//     if(cc == Y && dir == V){
//         jpg->SOF0info->HVY += sampling_factor;
//     }
//     if(cc == Cb && dir == H){
//         jpg->SOF0info->HVCb = sampling_factor << 4;
//     }
//     if(cc == Cb && dir == V){
//         jpg->SOF0info->HVCb += sampling_factor;
//     }
//     if(cc == Cr && dir == H){
//         jpg->SOF0info->HVCr = sampling_factor << 4;
//     }
//     if(cc == Cr && dir == V){
//         jpg->SOF0info->HVCr += sampling_factor;
//     }
// }

// /*
//     Retourne le facteur d'échantillonnage utilisé pour la composante 
//     de couleur cc et la direction dir, lu dans la structure jpeg.
// */
// uint8_t jpeg_get_sampling_factor(struct jpeg *jpg, enum color_component cc, enum direction dir){
//     if(cc == Y && dir == H){
//         return jpg->SOF0info->HVY;
//     }
//     if(cc == Y && dir == V){
//         return jpg->SOF0info->HVY;
//     }
//     if(cc == Cb && dir == H){
//         return jpg->SOF0info->HVCb;
//     }
//     if(cc == Cb && dir == V){
//         return jpg->SOF0info->HVCb;
//     }
//     if(cc == Cr && dir == H){
//         return jpg->SOF0info->HVCr;
//     }
//     if(cc == Cr && dir == V){
//         return jpg->SOF0info->HVCr;
//     }
// }



// /*
//     Ecrit dans la structure jpeg la table de Huffman huff_table à utiliser
//     pour encoder les données de la composante fréquentielle acdc, pour la
//     composante de couleur cc.
// */
// void jpeg_set_huffman_table(struct jpeg *jpg, enum sample_type acdc, enum color_component cc, struct huff_table *htable)
// {
//     jpg->huffman_table = huffman_table_build(htables_nb_symb_per_lengths[acdc][cc], htables_symbols[acdc][cc], htables_nb_symbols[acdc][cc]);
// }

// /*
//     Retourne un pointeur vers la table de Huffman utilisée pour encoder
//     les données de la composante fréquentielle acdc pour la composante
//     de couleur cc, lue dans la structure jpeg.
// */
// struct huff_table *jpeg_get_huffman_table(struct jpeg *jpg, enum sample_type acdc, enum color_component cc)
// {
//     return jpg->huffman_table;
// }


// /*
//     Ecrit dans la structure jpeg la table de quantification à utiliser
//     pour compresser les coefficients de la composante de couleur cc.
// */
// void jpeg_set_quantization_table(struct jpeg *jpg, enum color_component cc, uint8_t *qtable){
//     if(cc == Y){
//        for(int i = 0; i < 63; i++){
//             jpg->qtableY[i] = qtable[i];
//         } 
//     }
//     if(cc == Cb || cc == Cr){
//         for(int i = 0; i < 63; i++){
//             jpg->qtableC[i] = qtable[i];
//         } 
//     }
// }

// /*
//     Retourne un pointeur vers la table de quantification associée à la 
//     composante de couleur cc, lue dans a structure jpeg.
// */
// uint8_t *jpeg_get_quantization_table(struct jpeg *jpg, enum color_component cc){
//     if(cc == Y){
//         return jpg->qtableY;
//     }
//     if(cc == Cb || cc == Cr){
//         return jpg->qtableC;
//     }
// }