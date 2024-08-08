#ifndef  _RLE_
#define  _RLE_

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdint.h>
#include "init_MCU.h"

int16_t get_indice(int nombre, int magnitude);

int notonly_zero(int16_t *tab, int i);

void fct_RLE(struct MCU *mcu);


#endif /* _RLE_H_ */