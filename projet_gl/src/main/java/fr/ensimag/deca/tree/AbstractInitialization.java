package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * Initialization (of variable, field, ...)
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractInitialization extends Tree {
    
    /**
     * Implements non-terminal "initialization" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param t corresponds to the "type" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     */
    protected abstract void verifyInitialization(DecacCompiler compiler,
            Type t, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    //public abstract AbstractExpr getExpression();

    public abstract void codeGenInit(DecacCompiler compiler, DAddr dval);
    public abstract void codeGenInitARM(DecacCompiler compiler, Register reg);

    public abstract DVal codeGenInitField(DecacCompiler compiler, DVal dval);
}
