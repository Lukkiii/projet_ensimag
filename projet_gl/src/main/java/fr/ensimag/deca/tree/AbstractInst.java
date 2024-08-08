package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * Instruction
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractInst extends Tree {
    
    /**
     * Implements non-terminal "inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to the "return" attribute (void in the main bloc).
     */    
    protected abstract void verifyInst(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError;

    /**
     * Generate assembly code for the instruction.
     * 
     * @param compiler
     */
    protected abstract DVal codeGenInst(DecacCompiler compiler);

    // We assume register for now.
    protected abstract Register codeGenInstARM(DecacCompiler compiler);


    /**
     * Decompile the tree, considering it as an instruction.
     *
     * In most case, this simply calls decompile(), but it may add a semicolon if needed
     */
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
    }

    /**
     * Desplay an error
     * @param compiler
     */
    public void displayError(DecacCompiler compiler)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
