package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
/**
 * @author gl41
 * @date 01/01/2024
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    private ErrorMessage errorMessage = new ErrorMessage("Overflow during arithmetic operation", new Label("overflow_error")); 

    @Override
    public String getOperatorName() {
        return "+";
    }
    
    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {
        compiler.addInstruction(new ADD(dVal(expr),(GPRegister)locDest));
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new ADD(source,(GPRegister)dest));
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }
}
