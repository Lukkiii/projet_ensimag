package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl41
 * @date 01/01/2024
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        this.setType(compiler.environmentType.INT);
        return this.getType();
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        instr(compiler, this, Register.getR(2));
        return compiler.getRegAlloc().getLastRegister();
    }


    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        instr(compiler, this, compiler.getRegAlloc().getLastRegister());
        return compiler.getRegAlloc().getLastRegister();
    }

    public void codeGenExprARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, new Immediate(this.value)));
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Unimplemented method 'codeGenInstARM'");
    }

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest)
    {
        IntLiteral value = (IntLiteral) expr;
        compiler.addInstruction(new LOAD(value.getValue(), (GPRegister)locDest));
    }


}
