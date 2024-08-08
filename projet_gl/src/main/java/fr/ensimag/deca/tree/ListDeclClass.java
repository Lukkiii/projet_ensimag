package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import org.apache.log4j.Logger;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass d: getList()) {
            d.verifyClass(compiler);
        }
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass d: getList()) {
            d.verifyClassMembers(compiler);
        }
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClassBody: start");
        for (AbstractDeclClass d: getList()) {
            d.verifyClassBody(compiler);
        }
        LOG.debug("verify listClassBody: end");
    }

    public void codeGenMethodTables(DecacCompiler compiler)
    {
        for (AbstractDeclClass classes : this.getList()) {
            classes.createMethodTables(compiler);
        }
    }

    public DVal codeGenInst(DecacCompiler compiler) {
        for (AbstractDeclClass classes : this.getList()) {
            classes.codeGenInst(compiler);
        }
        return null;    
    }

}


