package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.SNE;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        return this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
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
        // // A =?= B
        //     int numberOfJump = compiler.getLabel();
        //     Label equal = new Label("egal" + numberOfJump);
        //     Label end = new Label("fin" + numberOfJump);

        // // C = A - B
        //     compiler.addInstruction(new CMP(dVal(expr), (GPRegister)locDest));
        //     compiler.addInstruction(new BNE(equal));
            
        // // Sinon => false
        //     compiler.addInstruction(new LOAD(0, (GPRegister)locDest));
        //     compiler.addInstruction(new BRA(end));

        // // C == 0 => true
        //     compiler.addLabel(equal);
        //     compiler.addInstruction(new LOAD(1, (GPRegister)locDest));

        // // End of comparison
        //     compiler.addLabel(end);      
        compiler.addInstruction(new SNE((GPRegister)locDest));    
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new MUL(source,(GPRegister)dest)); 
    }
    // utilisé lorsque l'on a uniquement if(x != y)
    @Override
    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BEQ(label));
    }
}
