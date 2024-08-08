package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl41
 * @date 01/01/2024
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // String is not in env type predef....
        this.setType(compiler.environmentType.STRING);
        return this.getType();
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));

        return null;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("\"");
        s.print(value);
        s.print("\"");
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
        return "StringLiteral (" + value + ")";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void codeGenExprARM(DecacCompiler compiler, Register reg) {
        fr.ensimag.arm.pseudocode.Label label = compiler.armProgram.addStringPool(this.value);
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, label));
    }
}
