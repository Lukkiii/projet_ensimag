package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    private ErrorMessage errorMessage = new ErrorMessage("Overflow during arithmetic operation", new Label("overflow_error"));

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    public void displayError(DecacCompiler compiler)
    {
        errorMessage.display(compiler);
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {
        if (expr.getType().isInt()) {
            compiler.addInstruction(new QUO(dVal(expr),(GPRegister)locDest)); 
        } else if (expr.getType() == compiler.environmentType.FLOAT) {
            compiler.addInstruction(new DIV(dVal(expr),(GPRegister)locDest));
        }
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }
    
    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        if (this.getType().isFloat()) {
            compiler.addInstruction(new DIV(source,(GPRegister)dest));
        } else {
            compiler.addInstruction(new QUO(source,(GPRegister)dest));            
        }
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
    }
}
