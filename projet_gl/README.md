# Projet Génie Logiciel, Ensimag.
gl41, 01/01/2024.

## Decac
Afin d'utiliser le compilateur Deca, il est nécessaire d'ajouter d'abord le chemin vers "ProjetGL/src/main/bin" dans le fichier de configuration du shell. Ensuite, il suffit de redémarrer le shell, et la commande "decac" sera prête à être utilisée.

La syntaxe d’utilisation de l’exécutable decac est: 
decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] [-arm]<fichier deca>...] | [-b]
avec <fichier deca> des chemins de la forme <répertoires/nom.deca> (le suffix .deca est obligatoire). Le résultat <répertoires/nom.ass> est dans même répertoire que le fichier source.
La commande decac, sans argument, affichera les options disponibles. On peut appeler la commande decac avec un ou plusieurs fichiers sources Deca.
Si un fichier apparaît plusieurs fois sur la ligne de commande, il n’est compilé qu’une seule fois
Une fois le fichier assembleur généré, il vous suffit d'utiliser la commande "ima" pour l'exécuter.
|                           |                    |                            |
|---------------------------|--------------------|----------------------------|
|-b|(banner)|affiche une bannière indiquant le nom de l'équipe|
|-p|(parse)|arrête decac après l'étape de construction de l'arbre, et affiche la décompilation de ce dernier (i.e. s'il n'y a qu'un fichier source à compiler, la sortie doit être |un programme deca syntaxiquement correct)|
|-v|(verification)|arrête decac après l'étape de vérifications (ne produit aucune sortie en l'absence d'erreur)|
|-n|(no check)|supprime les tests à l'exécution spécifiés dans les points 11.1 et 11.3 de la sémantique de Deca|
|-r X|(registers)|limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16|
|-d|(debug)|active les traces de debug. Répéter l'option plusieurs fois pour avoir plus de traces|
|-P|(parallel)|s'il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation)|
|-arm|(arm)|Compilation pour ARM|

## Extension ARM
### How to create the environment on Ubuntu
- `./arm-environment/scripts/ubuntu.sh <dest_dir>` (Deploy ARM environment)
- `source <dest_dir>/env.sh` (Initialize ARM environment)
### Using provided tools
Once sourced you should have access to the following tools:
- `compile-deca-arm <file>.s` (Compile an ASM file to an ELF executable)
- `qemu-deca-arm <file>` (Run the ELF file using qemu user mode emulation)

Example workflow given a file named *./code.deca*:
- `decac -arm ./code.deca` (Generate a *./code.s* file)
- `compile-deca-arm ./code.s` (Build *./code.s* to *./code*)
- `qemu-deca-arm ./code` (Run newly built executable using qemu)

