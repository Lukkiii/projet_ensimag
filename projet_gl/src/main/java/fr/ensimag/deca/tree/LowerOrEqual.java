package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.SLE;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<=";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        compiler.addInstruction(new SLE((GPRegister)locDest));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    // utilisé lorsque l'on a uniquement if(x <= y)
    @Override
    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BGT(label));
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }
}
