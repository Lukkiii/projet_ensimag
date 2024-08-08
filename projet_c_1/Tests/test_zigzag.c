#include <stdio.h>
#include <stdlib.h>

#include "qtables.h"
#include "init_MCU.h"
#include "zig-zag.h"



int main(){

    int16_t testzigzagtab[8][8] = {
        {0x007b, 0x0000, 0xfee4, 0x0000, 0x0000, 0x0000, 0xffec, 0x0000},
	    {0xfffb, 0x0000, 0xfedb, 0x0000, 0x006a, 0x0000, 0xff7e, 0x0000}, 
	    {0xfeb3, 0x0000, 0xff8c, 0x0000, 0x0045, 0x0000, 0X0099, 0x0000}, 
	    {0xff37, 0x0000, 0xffa1, 0x0000, 0x0018, 0x0000, 0x0003, 0x0000}, 
	    {0x007f, 0x0000, 0x011c, 0x0000, 0x00fe, 0x0000, 0x0014, 0x0000}, 
	    {0xffa7, 0x0000, 0x0013, 0x0000, 0x007d, 0x0000, 0xfe1f, 0x0000}, 
	    {0xff76, 0x0000, 0x001a, 0x0000, 0xff5a, 0x0000, 0x00f4, 0x0000}, 
	    {0x00dc, 0x0000, 0xffa9, 0x0000, 0xffba, 0x0000, 0xff3d, 0x0000}, 
    };


    struct MCU *matrix = mcu_create();
    // uint8_t *vecteur;
    // uint8_t *tablequanti;
    int i;
    int j;

    for(i=0;i<8;i++){
        for(j=0;j<8;j++){
            matrix->tab_DCT[i][j] = testzigzagtab[i][j];
        }
    }

    printf("zigzag : \n");

    // vecteur = trans_zigzag(matrix);
    trans_zigzag(matrix);

    for(int i = 0 ; i < 8 ; i++){
        for(int j =0 ; j < 8 ; j++){
            printf("%04hx,", matrix->tab_DCT[i][j]);
        } printf("\n");
    }

    printf("\n");

    printf("quantification : \n");

    // tablequanti = quantification(matrix);
    quantification(matrix);
    
    for(i = 0 ; i < 8 ; i++){
        for(j = 0 ; j < 8 ; j++){
            printf("%04hx,", matrix->tab_DCT[i][j]);
        }printf("\n");
    }

    printf("element DC : \n");
    printf("%04hx", matrix->element_DC[0]);
    printf("\n");
    printf("tab ordonne AC : \n");
    for(int i = 0 ; i < 63 ; i++){
        printf("%04hx, ", matrix->tab_ordonne_AC[i]);
    }printf("\n");

    return 0;
}



