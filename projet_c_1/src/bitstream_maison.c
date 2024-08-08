// #include <stdio.h>
// #include <stdlib.h>
// #include <string.h>
// #include "stdint.h"
// #include <stdbool.h>
// #include "bitstream_maison.h"

// /*
//     Retourne un nouveau bitstream prêt à écrire dans le
//     fichier filename.
// */

// struct bitstream *bitstream_create(const char *filename) {
//     struct bitstream *stream;
//     FILE *file=fopen(filename,"wb");
//     if(file==NULL){
//         perror("Error opening file ");
//         return NULL;
//     }
//     stream=malloc(sizeof(struct bitstream));
//     if(stream==NULL){
//         perror("Error allocating memory ");
//         return NULL;
//     }
//     stream->file=file;
//     stream->octet=0;
//     stream->index_in_octet=0;



//     return stream;
// }



// void bitstream_flush(struct bitstream *stream) {
//     /*si l'octet est déjà vide on ne fait rien*/
//     if(stream->index_in_octet==0){
//         return;
//     }
//     /* sinon on complète avec des 0 à droite */
//     else{
//         stream->octet=stream->octet<<(8-stream->index_in_octet);
//         fwrite(&stream->octet,1,1,stream->file);
//         stream->octet=0;
//         stream->index_in_octet=0;
//     }
// }


// void bitstream_write_bits(struct bitstream *stream,uint32_t value,uint8_t nb_bits,bool is_marker){
    

//     while (nb_bits>0) {
//        /* on récupère le MSB de value */
//        uint8_t bit=value>>(nb_bits-1);
//          bit=bit&1;
//             /* on écrit le bit dans le buffer */
//             stream->octet=stream->octet<<1;
//             stream->octet=stream->octet|bit;
//             /* on affiche stream->octet|bit */

//             stream->index_in_octet++;
//             /* on met à jour le nombre de bits restant à écrire */
//             nb_bits--;
//             /* si le buffer est plein on l'écrit dans le fichier */
 
    
//         /* si l'octet est plein, on l'écrit dans le fichier */
//         if(stream->index_in_octet==8){
//             fwrite(&stream->octet,1,1,stream->file);

//         /*on prendre en compte le byte stuffing*/
//         if(is_marker==false){
//             if(stream->octet==0xff){
//                 stream->octet=0;
//                 fwrite(&stream->octet,1,1,stream->file);
//                 stream->index_in_octet=0;
//             }
//             stream->octet=0;
//             stream->index_in_octet=0;
//         }

// }

// }

// }

// void bitstream_destroy(struct bitstream *stream) {
//     if (stream != NULL) {
//         // free(stream->bytes_buffer);
//         fclose(stream->file);
//         free(stream);
//     }

// }





// // int main(void)
// // {
// //     /* On test la fonction bitstream_create */

// //     struct bitstream *stream = bitstream_create("test.jpeg");
// //     /* On test la focntion bitstream_write_bits */
// //     bool is_marker = false;
// //     bitstream_write_bits(stream, 32725, 15, is_marker);
// //     affiche_buffer(stream);
 
// //     affiche_buffer(stream);
// //     printf("%ld\n", stream->buffer_index);
// //     bitstream_flush(stream);
// // }