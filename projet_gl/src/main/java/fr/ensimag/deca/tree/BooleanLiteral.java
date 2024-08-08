package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;

import java.io.PrintStream;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
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
        this.setType(compiler.environmentType.BOOLEAN);
        return this.getType();
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        instr(compiler, this, compiler.getRegAlloc().getLastRegister());
        return compiler.getRegAlloc().getLastRegister();
    }


    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        instr(compiler, this, compiler.getRegAlloc().getLastRegister());
        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        fr.ensimag.arm.pseudocode.Register reg = compiler.armAllocator.alloc();
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, new Immediate(this.value)));
        return reg;
    }

    @Override
    public void codeGenExprARM(DecacCompiler compiler, Register reg) {
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, new Immediate(this.value)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
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
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        BooleanLiteral value = (BooleanLiteral) expr;
        if (value.getValue()) {
            compiler.addInstruction(new LOAD(dVal(new IntLiteral(1)), (GPRegister)locDest));            
        } else {
            compiler.addInstruction(new LOAD(dVal(new IntLiteral(0)), (GPRegister)locDest));           
        }
    }


    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new CMP(0, compiler.getRegAlloc().getLastRegister()));
        compiler.addInstruction(new BEQ(label));
    }
}
