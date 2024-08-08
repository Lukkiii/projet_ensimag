package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 * @author gl41
 * @date 01/01/2024
 */
public class Multiply extends AbstractOpArith {
    private ErrorMessage errorMessage = new ErrorMessage("Overflow during arithmetic operation", new Label("overflow_error"));

    
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) 
    {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        compiler.addInstruction(new MUL(dVal(expr),(GPRegister)locDest));

        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new MUL(source,(GPRegister)dest));

        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }
}
