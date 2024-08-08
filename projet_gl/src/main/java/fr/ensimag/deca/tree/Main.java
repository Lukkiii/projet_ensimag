package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.directives.Section;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl41
 * @date 01/01/2024
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListDeclVar getListDeclVar() {
        return declVariables;
    }

    // private int numberOfVariables = declVariables.getList().size();

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        EnvironmentExp e = compiler.getEnvExp();
        
        this.declVariables.verifyListDeclVariable(compiler, e, null);

        this.insts.verifyListInst(compiler, e, null, compiler.getType("void"));
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        compiler.addComment("Variables declarations:");

        declVariables.codeGenListDeclVar(compiler);    

        insts.codeGenListInst(compiler);                
    }

    @Override
    protected void codeGenMainARM(DecacCompiler compiler) {
        compiler.armProgram.data.addComment("Variables declarations:");
        this.declVariables.codeGenListDeclVarARM(compiler);
        compiler.armProgram.main.addComment("Main instructions:");
        this.insts.codeGenListInstARM(compiler);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
