package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }    

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        return this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {   
        compiler.addInstruction(new SEQ((GPRegister)locDest));     
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new MUL(source,(GPRegister)dest)); 
    }
    
    // utilisé lorsque l'on a uniquement if(x == y)
    @Override
    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BNE(label));
    }
}
