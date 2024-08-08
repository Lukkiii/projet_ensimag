#ifndef _BITSTREAM_MAISON_
#define _BITSTREAM_MAISON_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "stdint.h"
#include <stdbool.h>

/*
    Type opaque représentant le flux d'octets à écrire dans le fichier JPEG de
    sortie (appelé bitstream dans le sujet).
*/
struct bitstream{
    FILE *file;
    uint8_t octet;
    uint8_t index_in_octet;

};
/* Retourne un nouveau bitstream prêt à écrire dans le fichier filename. */
struct bitstream *bitstream_create(const char *filename);

/*
    Ecrit nb_bits bits dans le bitstream. La valeur portée par cet ensemble de
    bits est value. Le paramètre is_marker permet d'indiquer qu'on est en train
    d'écrire un marqueur de section dans l'entête JPEG ou non (voir section
    "Encodage dans le flux JPEG -> Byte stuffing" du sujet).
*/
void bitstream_write_bits(struct bitstream *stream,
                                 uint32_t value,
                                 uint8_t nb_bits,
                                 bool is_marker);

/*
    Force l'exécution des écritures en attente sur le bitstream, s'il en
    existe.
*/
void bitstream_flush(struct bitstream *stream);

/*
    Détruit le bitstream passé en paramètre, en libérant la mémoire qui lui est
    associée.
*/
void bitstream_destroy(struct bitstream *stream);

#endif /* _BITSTREAM_MAISON_ */
