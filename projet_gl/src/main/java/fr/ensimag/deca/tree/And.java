package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        //return compiler.getRegAlloc().getLastRegister();
        return this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {        
       return this.codeExp(null,  compiler); 
    }

    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {        
        compiler.addInstruction(new CMP(dVal(expr),(GPRegister)locDest));    
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new CMP(source,(GPRegister)dest));    
    }

    @Override
    public void instrBool(DecacCompiler compiler, AbstractExpr expr, DVal locDest, Label label, int numberOfLabel)
    {        
        label.setName(label+"_AND_"+getLocation().getLine()+"."+numberOfLabel);
        instr(compiler, expr, locDest);   
        compiler.addInstruction(new BNE(label));
    }
    @Override
    protected void instrBinBool(DecacCompiler compiler, DVal source, DVal dest, Label label, int numberOfLabel) {
        label.setName(label+"_AND_"+getLocation().getLine()+"."+numberOfLabel);
        instrBin(compiler, source, dest);  
        compiler.addInstruction(new BNE(label));
    }

    public void bodyBranch(DecacCompiler compiler, Label start, Label end, int numberOfLabel)
    {
        start.setName("Start_AND_"+getLocation().getLine()+"."+numberOfLabel);
        end.setName("End_AND_"+getLocation().getLine()+"."+numberOfLabel);
        compiler.addInstruction(new LOAD(1, compiler.getRegAlloc().getLastRegister()));
        compiler.addInstruction(new BRA(end));
        compiler.addLabel(start);
        compiler.addInstruction(new LOAD(0, compiler.getRegAlloc().getLastRegister()));
        compiler.addLabel(end);
    }

    // public DVal codeExp(DecacCompiler compiler)
    // {
    //     if (dVal(this.getRightOperand()) != null)
    //     {
    //         DVal regDest = this.getLeftOperand().codeGenInst(compiler);
    //         this.instr(compiler, getRightOperand(), regDest);
            
        
    //         return regDest;
    //     }
    //     else
    //     {
    //         DVal regDest = this.getLeftOperand().codeGenInst(compiler);
    //         compiler.getRegAlloc().allocate();
    //         DVal regSource = this.getRightOperand().codeGenInst(compiler);
    //         compiler.getRegAlloc().deallocate((GPRegister)regSource);
    //         this.instrBin(compiler, regSource, regDest);
    //         return regDest;
    //     }
    // }

}


//     compiler.addInstruction(new LOAD(1, compiler.getRegAlloc().getLastRegister()));
            //     compiler.addInstruction(new BRA(compiler.getManagementLabel().getListExprBool().get(1)));
            //     compiler.addLabel(compiler.getManagementLabel().getListExprBool().get(0));
            //     compiler.addInstruction(new LOAD(0, compiler.getRegAlloc().getLastRegister()));
            //     compiler.addLabel(compiler.getManagementLabel().getListExprBool().get(1));
            //     compiler.getManagementLabel().setIsInListExprBool();
