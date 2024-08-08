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
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl41
 * @date 01/01/2024
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        this.setType(compiler.environmentType.FLOAT);
        return this.getType();
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
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
        s.print(java.lang.Float.toHexString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        FloatLiteral value = (FloatLiteral) expr;
        compiler.addInstruction(new LOAD(dVal(value), (GPRegister)locDest));
    }

}
