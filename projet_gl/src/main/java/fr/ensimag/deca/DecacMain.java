package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl41
 * @date 01/01/2024
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }

        switch(options.getDebug()) {
            case CompilerOptions.INFO:
                LOG.setLevel(Level.INFO);
                break;
            case CompilerOptions.DEBUG:
                LOG.setLevel(Level.DEBUG);
                break;
            case CompilerOptions.TRACE:
                LOG.setLevel(Level.TRACE);
                break;
        }
        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            throw new UnsupportedOperationException("Parallel build not yet implemented");
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (options.getParsingOnly()) {
                    if (compiler.decompile()) {
                        error = true;
                    };
                }
                else {
                    if(options.getARM()) {
                        if (compiler.compileARM())
                            error = true;
                    } else {
                        if (compiler.compile())
                            error = true;
                    }
                }
            }
        }

        if (options.getPrintBanner()) {
            options.displayBanner();
            System.exit(0);
        }

        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
            System.exit(1);
        }
        
        if (options.getVerificationOnly()) {
            // BLABLABLAH
        }
        
        System.exit(error ? 1 : 0);
    }
}
