#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include <init_MCU.h>

int main(int argc, char *argv[]){
    char *file;
    if (argc == 2){
        file = argv[argc-1];
    } else {
        printf("Erreur sur le format : pas d'image spécifiée ... \n");
        return EXIT_FAILURE;
    }
    struct image_ppm *image = pgm_to_MCU(file);
    
    //affichage paramètres en-tête
    printf("type image : %c !\n", image->p_type);
    printf("haut image : %u !\n", image->haut);
    printf("largeur image : %u !\n", image->larg);
    printf("val max : %u !\n", image->val_max);

    //afichage content
    // for (int i = 0; i<8; i++){
    //     for(int j = 0; j<8; j++){
    //         printf("%c ", (image->data->tab)[i][j] == 0 ? ' ' : '#');
    //     } printf("\n");
    // }
    for (size_t i = 0; i< image->larg*image->haut; i++){
        printf("%x ", image->all_data[i]);
        if(i%8==1){
            printf("\n");
        }
    }

    printf("\n");
    return EXIT_SUCCESS;
}
