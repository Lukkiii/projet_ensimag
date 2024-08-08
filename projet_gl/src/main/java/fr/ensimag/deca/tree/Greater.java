package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.SGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.Label;
/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        return this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    }

    // @Override
    // protected DVal codeGenInst(DecacCompiler compiler) {
    //     compiler.add(new Line(""));
    //     compiler.add(new Line(decompile()));
    //     this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    //     return compiler.getRegAlloc().getLastRegister();
    // }

    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {
        compiler.addInstruction(new SGT((GPRegister)locDest));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new CMP(source,(GPRegister)dest));
    }

    // utilisé lorsque l'on a uniquement if(x > y)
    @Override
    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BLE(label));
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }
}
