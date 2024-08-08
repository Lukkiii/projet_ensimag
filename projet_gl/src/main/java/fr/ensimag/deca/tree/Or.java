package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        return this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        DVal reg = this.codeExp(null,  compiler);
        return reg;
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
        label.setName(label+"_OR_"+getLocation().getLine()+"."+numberOfLabel);
        instr(compiler, expr, locDest);    
        compiler.addInstruction(new BNE(label));
    }
    @Override
    protected void instrBinBool(DecacCompiler compiler, DVal source, DVal dest, Label label, int numberOfLabel) {
        label.setName(label+"_OR_"+getLocation().getLine()+"."+numberOfLabel);
        instrBin(compiler, source, dest);   
        compiler.addInstruction(new BNE(label));
    }

    public void bodyBranch(DecacCompiler compiler, Label start, Label end, int numberOfLabel)
    {
        start.setName("Start_OR_"+getLocation().getLine()+"."+numberOfLabel);
        end.setName("End_OR_"+getLocation().getLine()+"."+numberOfLabel);
        compiler.addInstruction(new LOAD(0, compiler.getRegAlloc().getLastRegister()));
        compiler.addInstruction(new BRA(end));
        compiler.addLabel(start);
        compiler.addInstruction(new LOAD(1, compiler.getRegAlloc().getLastRegister()));
        compiler.addLabel(end);
    }

    public DVal codeExp(DVal location, DecacCompiler compiler)
    {
        compiler.getManagementLabel().addLabelExprBool(getLocation().getLine());

        int numberOfExprOr = compiler.getManagementLabel().getNumberOfLabelBool();

        Label start = new Label("Start");
        Label end   = new Label("End");

        // ! ( ! C1 && ! C2)
        
        // ! C1
            Not notLeft = new Not(getLeftOperand());
            DVal regDest = notLeft.codeGenInst(compiler);
            this.instrBool(compiler, new IntLiteral(1), regDest, start, numberOfExprOr);

        // ! C2
            compiler.getRegAlloc().allocate();

            Not notRight= new Not(getRightOperand());
            DVal regSource = notRight.codeGenInst(compiler);
            this.instrBool(compiler, new IntLiteral(1), regSource, start, numberOfExprOr);

            compiler.getRegAlloc().deallocate((GPRegister)regSource);

        // ! C1 && ! C2
            this.instrBinBool(compiler, regSource, regDest, start, numberOfExprOr);

        // ! (! C1 && ! C2)
            Not not = new Not(this);
            not.instrBin(compiler, regDest, regDest);

        //if (first)
            bodyBranch(compiler, start, end, numberOfExprOr);

        return regDest;
    }
}

