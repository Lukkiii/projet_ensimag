#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>

#include "jpeg_final.h"
#include "init_MCU.h"
#include "jpeg_writer.h"

char *opt_arg;
static const char *shortopts = "ho:s:";

struct option longopts[] =
    {
        {"help", no_argument, NULL, 'h'},
        {"outfile", required_argument, NULL, 'o'},
        {"sample", required_argument, NULL, 's'},
        {0, 0, 0, 0},
};

void help()
{
    printf("--help : pour afficher la liste des options acceptées ;\n");
    printf("--outfile=sortie.jpg : pour redéfinir le nom du fichier de sortie ;\n");
    printf("--sample=h1xv1, h2xv2, h3xv3 : pour définir les facteurs d'échantillonnage hxv des trois composantes de couleur.\n");
    printf("Le mode débogage est desactivé par défaut : pour y accéder, commenter les '#define debug' en haut des fichiers src/init_MCU.c, src/jpeg_final.c & src/RLE.c\n");
}

int main(int argc, char *argv[])
{
    char*helpp = "--help";
    if (argc <= 1 || argc > 4)
    {
        printf("erreur \n");
        help();
        return EXIT_FAILURE;
    }

    else if (!strcmp(argv[1], helpp))
    {
        help();
        return EXIT_SUCCESS;
    }
    else if (argc == 2)
    {
        convertPpm((char *)argv[1], NULL);
        return EXIT_SUCCESS;
    }

    
    int c;
    while ((c = getopt_long(argc, argv, shortopts, longopts, NULL)) != -1)
    {
        switch (c)
        {
        case 'h':
        printf("inonpn\n");
            help();
            break;

        case 'o':;
            char *egal = "=";
            char *point = ".";
            char *new_name = strtok(argv[1], egal);
            new_name = strtok(NULL, egal);
            // new_name = strtok(new_name, point);
            printf("\nImage enregistrée dans : %s\n", new_name);
            convertPpm((char *)argv[2], new_name);
            break;

        case 's':
            opt_arg = optarg;

            char *c_hCb;
            char *c_vCb;
            char *c_hCr;
            char *c_vCr;
            char *c_hY;
            char *c_vY;
            char *delim1 = ",";
            char *delim2 = "x";
            char *hvC;
            char *hvY;
            char *hvCb;
            char *hvCr;

            uint8_t hY;
            uint8_t vY;
            uint8_t hCb;
            uint8_t vCb;
            uint8_t hCr;
            uint8_t vCr;

            hvY = strtok(opt_arg, delim1);
            while ((hvC = strtok(NULL, delim1)))
            {
                hvCb = hvC;
                hvCr = strtok(NULL, delim1);
            }

            c_hY = strtok(hvY, delim2);
            c_vY = strtok(NULL, delim2);

            c_hCb = strtok(hvCb, delim2);
            c_vCb = strtok(NULL, delim2);

            c_hCr = strtok(hvCr, delim2);
            c_vCr = strtok(NULL, delim2);

            hY = (uint8_t)(*c_hY - '0');
            printf("%u\n", hY);
            vY = (uint8_t)(*c_vY - '0');
            printf("%u\n", vY);
            hCb = (uint8_t)(*c_hCb - '0');
            printf("%u\n", hCb);
            vCb = (uint8_t)(*c_vCb - '0');
            printf("%u\n", vCb);
            hCr = (uint8_t)(*c_hCr - '0');
            printf("%u\n", hCr);
            vCr = (uint8_t)(*c_vCr - '0');
            printf("%u\n", vCr);

            // convertPpm((char *)argv[2],NULL, hCb, vCb, hCr, vCr);
            break;
        }
    }

    return EXIT_SUCCESS;
}