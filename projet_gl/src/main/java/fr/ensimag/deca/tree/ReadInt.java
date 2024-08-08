package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class ReadInt extends AbstractReadExpr {


    private ErrorMessage errorMessage = new ErrorMessage("Input/Output error", new Label("io_error"));


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = compiler.environmentType.INT;

        this.setType(type);

        return type;
    }

    @Override
    public DVal codeGenInst(DecacCompiler compiler)
    {
        if (!compiler.isSetErrorMessage(1))
            compiler.setErrorMessage(1, errorMessage);
        instr(compiler, null, null);
        return Register.R1;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
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
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        compiler.addInstruction(new RINT());
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }

}
