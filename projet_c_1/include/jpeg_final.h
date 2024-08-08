#ifndef _JPEG_FINAL_H_
#define _JPEG_FINAL_H_

#include <stdint.h>
#include <stdio.h>
#include <init_MCU.h>
#include <stdbool.h>

#include "jpeg_writer.h"

struct image_ppm * data_encoder(struct image_ppm *image, uint8_t color);

void convertPpm(char *image_name, char* new_name);

void load_next_tab(struct image_ppm *image, uint64_t *no_mcu_cour, uint8_t color_num);

#endif /* _JPEG_FINAL_ */