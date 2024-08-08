#ifndef  _ZIGZAG_
#define  _ZIGZAG_
#include <stdint.h>
#include <stdbool.h>
#include "init_MCU.h"


void trans_zigzag(struct MCU *matrix);

void quantification(struct MCU *matrix, uint8_t color_num);






#endif /* _ZIGZAG_H_ */