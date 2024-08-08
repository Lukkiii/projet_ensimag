#include <stdio.h>
#include <stdlib.h>

#include "qtables.h"
#include "init_MCU.h"

void trans_zigzag(struct MCU *matrix){

    // if(matrix->tab == NULL){
    //     return;
    // }
    // int maxrows = 8;    /*sizeof(matrix.tab)/sizeof(matrix.tab[0])*/
    // if(maxrows <= 0){
    //     return;
    // }

    // int maxcols = 8;    /*sizeof(matrix.tab[0])/sizeof(matrix.tab[0][0])*/
    // int lenght = maxrows * maxcols;

    int n = 8;
    int sum = 0;
    int index = 0;
    int16_t vecteur[64];

    for(sum = 0 ; sum < 2*n-1 ; sum++){
        if(sum < n){
            if(sum%2 != 0){
                for(int i = 0 ; i <= sum ; i++){
                    vecteur[index++] = matrix->tab_DCT[i][sum-i];
                }
            }
            else{
                for(int i = 0 ; i <= sum ; i++){
                    vecteur[index++] = matrix->tab_DCT[sum-i][i];
                }
            }
        }
        else{
            if(sum%2 != 0){
                for(int i = n-1 ; i >= sum-n+1 ; i--){
                    vecteur[index++] = matrix->tab_DCT[sum-i][i];
                }                
            }
            else{
                for(int i = n-1 ; i >= sum-n+1 ; i--){
                    vecteur[index++] = matrix->tab_DCT[i][sum-i];
                }
            }
        }
    }

    for(int i = 0 ; i < 8 ; i++){
        for(int j = 0 ; j < 8 ; j++){
            matrix->tab_DCT[i][j] = vecteur[8 * i + j];
        }
    }
}


void quantification(struct MCU *matrix, uint8_t color_num){

    // uint8_t *vecteur = trans_zigzag(matrix);

    // uint8_t *tableY = quantification_table_Y;
    // uint8_t *tableCbCr = quantification_table_CbCr;
    // uint8_t *tablequanti;
    // int i;
    
    // tablequanti = (int16_t *)malloc(sizeof(int16_t)*64);

    // for(i = 0 ; i < 64 ; i++){
    //     matrix->tab_DCT[i][j] = vecteur[i]/tableY[i];
    // }
    // si on a color_num qui vaut 4 (image grise) ou color_num qui vaut 0 (composante Y d'une image en couleurs)
    if (color_num ==4 || color_num == 0){
        for(int i = 0 ; i < 8 ; i++){
            for(int j = 0 ; j < 8 ; j++){
                matrix->tab_DCT[i][j] = matrix->tab_DCT[i][j] / quantification_table_Y[8 * i + j];
            }
        }
    }
    else {
        for(int i = 0 ; i < 8 ; i++){
            for(int j = 0 ; j < 8 ; j++){
                matrix->tab_DCT[i][j] = matrix->tab_DCT[i][j] / quantification_table_CbCr[8 * i + j];
            }
        }
    }

    matrix->element_DC = (int16_t *)malloc(sizeof(int16_t)*1);

    matrix->element_DC[0] = matrix->tab_DCT[0][0];

    matrix->tab_ordonne_AC = (int16_t *)malloc(sizeof(int16_t)*63);
    int index = 0;

    for(int i = 0 ; i < 8 ; i++){
        if(i == 0){
            for(int j = 1 ; j < 8 ; j++){
                matrix->tab_ordonne_AC[index++] = matrix->tab_DCT[i][j];
            }
        }
        else{
            for(int j = 0 ; j < 8 ; j++){
                matrix->tab_ordonne_AC[index++] = matrix->tab_DCT[i][j];
            }
        }
        
    }
}
