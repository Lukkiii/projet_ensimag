package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl41
 * @date 01/01/2024
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getParsingOnly()
    {
        return parsingOnly;
    }

    public boolean getVerificationOnly()
    {
        return verificationOnly;
    }

    public boolean getARM() {return arm;}

    public boolean getNoCheck()
    {
        return noCheck;
    }

    public boolean getWarning()
    {
        return warnings;
    }

    public int getRegisters()
    {
        return nbRegisters;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }


    private int debug = QUIET;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parsingOnly = false;
    private boolean verificationOnly = false;
    private boolean noCheck = false;
    private int nbRegisters = 15;
    private boolean warnings = false;
    private boolean arm = false;
    private List<File> sourceFiles = new ArrayList<File>();


    public void openDecaFile(String pathname)
    {
        if (pathname.endsWith(".deca")) 
        {
            File source = new File(pathname);
            sourceFiles.add(source);
        }
    }
    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.  
        
        Iterator<String> it = Arrays.stream(args).iterator();
        while(it.hasNext()) {
            String parameter = it.next();
            switch(parameter) {
                case "-b":
                    printBanner = true;
                    break;
                case "-p":
                    parsingOnly = true;
                    break;
                case "-v":
                    verificationOnly = true;
                    break;
                case "-n":
                    noCheck = true;
                    break;
                case "-r":
                    if (!it.hasNext()) {
                        throw new CLIException("Nombre de registre non spécifié pour -r!");
                    }
                    try {
                        nbRegisters = Integer.parseInt(it.next());
                    } catch(NumberFormatException e) {
                        throw new CLIException("Format du nombre de registre incorrect!");
                    }
                    if (nbRegisters < 4 || nbRegisters > 16) {
                        throw new CLIException("Le nombre de registre doit être entre [4..16]");
                    }
                    break;
                case "-d":
                    debug = INFO;
                    break;
                case "-dd":
                    debug = DEBUG;
                    break;
                case "-ddd":
                    debug = TRACE;
                    break;
                case "-P":
                    break;
                case "-w":
                    warnings = true;
                    break;
                case "-arm":
                    arm = true;
                default:
                    openDecaFile(parameter);
                    break;
            }
        }
        if (this.parsingOnly && this.verificationOnly) throw new CLIException("L'option -p et -v sont incompatibles!");

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }


    public void displayBanner()
    {
        System.out.println(
            " ██████╗ ██╗██╗  ██╗ ██╗    ████████╗███████╗ █████╗ ███╗   ███╗\n" + //
            "██╔════╝ ██║██║  ██║███║    ╚══██╔══╝██╔════╝██╔══██╗████╗ ████║\n" + //
            "██║  ███╗██║███████║╚██║       ██║   █████╗  ███████║██╔████╔██║\n" + //
            "██║   ██║██║╚════██║ ██║       ██║   ██╔══╝  ██╔══██║██║╚██╔╝██║\n" + //
            "╚██████╔╝███████╗██║ ██║       ██║   ███████╗██║  ██║██║ ╚═╝ ██║\n" + //
            " ╚═════╝ ╚══════╝╚═╝ ╚═╝       ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝     ╚═╝");
    }   


    protected void displayUsage() {
        System.out.println("USAGE :");
        System.out.println("decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println("Avec : \n");
        System.out.println("-b (banner)       : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("-p (parse)        : arrête decac après l'étape de construction de\n" + //
                           "                    l'arbre, et affiche la décompilation de ce dernier\n" + //
                           "                    (i.e. s'il n'y a qu'un fichier source à\n" + //
                           "                    compiler, la sortie doit être un programme\n" + //
                           "                    deca syntaxiquement correct)");
        System.out.println("-v (verification) : arrête decac après l'étape de vérifications\n" + //
                           "                    (ne produit aucune sortie en l'absence d'erreur)");
        System.out.println("-n (no check)     : supprime les tests à l'exécution spécifiés dans\n" + //
                           "                    les points 11.1 et 11.3 de la sémantique de Deca");
        System.out.println("-r X (registers)  : limite les registres banalisés disponibles à\n" + //
                           "                    R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("-d (debug)        : active les traces de debug. Répéter\n" + //
                           "                    l'option plusieurs fois pour avoir plus de\n" + //
                           "                    traces");
        System.out.println("-P (parallel) :     s'il y a plusieurs fichiers sources,\n" + //
                           "                    lance la compilation des fichiers en\n" + //
                           "                    parallèle (pour accélérer la compilation)");
        System.out.println("-w (warnings) :     autorisant l'affichage de messages d'avertissement \n" + //
                           "                    en cours de compilation");
        System.out.println("-arm (arm)        : génére du code pour ARM.");
    }
}
