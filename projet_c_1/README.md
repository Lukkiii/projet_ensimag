# Notre encodeur JPEG à nous

Bienvenue sur la page d'accueil de _votre_ projet JPEG, un grand espace de liberté, sous le regard bienveillant de vos enseignants préférés.
Le sujet sera disponible dès le lundi 2 mai à l'adresse suivante : [https://formationc.pages.ensimag.fr/projet/jpeg/jpeg/](https://formationc.pages.ensimag.fr/projet/jpeg/jpeg/).


*Première étape : convertisseur PGM vers JPEG*

*->* choisir une Structure de Données pour les tableaux de pixels

*->* implémenter les fonctions : lecture de l'entête du PGM, lecture de la pixel map pour initialiser le(s) MCU(s)    (utilisation d'une struct qui contient les infos récupérées sur l'entête)   (pour le moment, pgm de taille 8x8 donc uniquement un MCU de taille 8x8), passage de RGB vers YCbCr (ici, uniquement Y), DCT en place sur un unique MCU donné, puis Zig-Zag sur un seul MCU pour obtenir un unique vecteur 64x1, puis étape de quantification avec la table de quantification donnée sous la forme 64x1.

**Voici un schéma qui résume les structures de données choisies, ainsi que le fonctionnement global de notre programme : **
![Image](schema_proj.jpg)

J : 1 
Y : parcours MCU 8x8 to vector 64x1  zigzag, quantification
V : DCT (02/05)  
M : lecture entête et initisalition CMU, convert RGB to YCbCr  

J2:  
V : Fin de Dct Ok et RLE Ok 
Y : Tests de Quantification et de ZigZag  
M : Reprendre MCU pour qu'il lise correctement le pgm  

J3:

V:Correction structure donne pour RLE etc...
M : Fin pgm to mcu
Y: fin zigzag

J4: 
 
V: Huffman + correction RLE + correction DCT 
M: entete jpeg + debut bitstream 
Y: recupération des commandes  

J5:
V: Correction RLE plus huffman nickel
M: Fin huffman plus bitstream
Y: Correction zigzag et fin recupération des commandes 

Week end :
On essaye de print invader car normalement toutes les fonctions marche,vico commence a taffer sur FDCT, et Yuxuan s'occcupe de traiter les images 320 par 240  

SAMEDI 07 : 
M : essayer de débuguer le tout -> première image obtenue mais l'obtention des huffman path reste laborieuse...

DIMANCHE 08 :
M : déboguer le tout -> nouvelle jpeg obtenu après correction de la gestion de "state" dans jpeg_final

MARDI 12 : 
M : débogage final pour la fonction de correction des images non 8x8 -> toutes les images en gris fonctionnent

SAMEDI 16 : 
M : début de l'obtention d'images en couleurs, restent des beug pour le resize

Dernière semaine :
M : tout est ok pour la couleur sauf pour thumbs, début de la réfléxion sur SUBSAMPLING, mais abandon
Y :  travail sur le module JPEG_WRITER.h -> pas de tests écrits ..
V : travail sur les modules BITSTREAM et HUFFMAN : manque de temps pour l'intégration de ceux-ci au projet, mais les tests fonctionnent.




 
Pour traiter l'image 320x240, il faut rajouter le traitement de l'enchainement des MCU et le downsampling 
Pour passer à bisou il faut prendre en compte les dépassements 
Et pour passer à complexité il faut avoir DCT 






Vous pouvez reprendre cette page d'accueil comme bon vous semble, mais elle devra au moins comporter les infos suivantes **avant la fin de la première semaine (vendredi 6 mai)** :

1. des informations sur le découpage des fonctionnalités du projet en modules, en spécifiant les données en entrée et sortie de chaque étape ;
2. (au moins) un dessin des structures de données de votre projet (format libre, ça peut être une photo d'un dessin manuscrit par exemple) ;
3. une répartition des tâches au sein de votre équipe de développement, comportant une estimation du temps consacré à chacune d'elle (là encore, format libre, du truc cracra fait à la main, au joli Gantt chart).

Rajouter **régulièrement** des informations sur l'avancement de votre projet est aussi **une très bonne idée** (prendre 10 min tous les trois chaque matin pour résumer ce qui a été fait la veille, établir un plan d'action pour la journée qui commence et reporter tout ça ici, par exemple).

# Liens utiles

- Bien former ses messages de commits : [https://www.conventionalcommits.org/en/v1.0.0/](https://www.conventionalcommits.org/en/v1.0.0/) ;
- Problème relationnel au sein du groupe ? Contactez [Pascal](https://fr.wikipedia.org/wiki/Pascal,_le_grand_fr%C3%A8re) !
- Besoin de prendre l'air ? Le [Mont Rachais](https://fr.wikipedia.org/wiki/Mont_Rachais) est accessible à pieds depuis la salle E301 !
- Un peu juste sur le projet à quelques heures de la deadline ? Le [Montrachet](https://www.vinatis.com/achat-vin-puligny-montrachet) peut faire passer l'envie à vos profs de vous mettre une tôle !
