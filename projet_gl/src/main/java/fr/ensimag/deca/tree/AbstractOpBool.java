package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * 
     * @param compiler
     * @param localEnv
     * @param currentClass
     * 
     * @author heuzec
     * @date 21/12/2023
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // Left part
            AbstractExpr leftPart = this.getLeftOperand();
            Type leftType = leftPart.verifyExpr(compiler, localEnv, currentClass);
        
        // Right part
			AbstractExpr rightPart = this.getRightOperand();
			Type rightType = rightPart.verifyExpr(compiler, localEnv, currentClass);

        // 3.33.2
            if(!leftType.isBoolean() || !rightType.isBoolean())
                throw new ContextualError("The type is not boolean", this.getLocation());

            setType(compiler.environmentType.BOOLEAN);

        return leftType;
    }

    public abstract void instrBool(DecacCompiler compiler, AbstractExpr expr, DVal locDest, Label label, int numberOfLabel);

    protected abstract void instrBinBool(DecacCompiler compiler, DVal source, DVal dest, Label label, int numberOfLabel);

    public DVal codeExp(DVal location, DecacCompiler compiler)
    {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        
        compiler.getManagementLabel().addLabelExprBool(getLocation().getLine());

        int numberOfExpr = compiler.getManagementLabel().getNumberOfLabelBool();

        Label start = new Label("Start");
        Label end   = new Label("End");
        
        // a || b

        // Left Part : a
        DVal regDest = this.getLeftOperand().codeGenInst(compiler);
        this.instrBool(compiler, new IntLiteral(1), regDest, start, numberOfExpr);

        if (dVal(this.getRightOperand()) != null)
        {   
            // Right Part : b
                compiler.getRegAlloc().allocate();

                DVal regSource = this.getRightOperand().codeGenInst(compiler);
                this.instrBool(compiler, new IntLiteral(1), regSource, start, numberOfExpr);

                compiler.getRegAlloc().deallocate((GPRegister)regSource);
        }
        else
        {
            compiler.getRegAlloc().allocate();

            DVal regSource = this.getRightOperand().codeGenInst(compiler);
            compiler.getRegAlloc().deallocate((GPRegister)regSource);

            this.instrBool(compiler, new IntLiteral(1), regSource, start, numberOfExpr);
        }

        bodyBranch(compiler, start, end, numberOfExpr);
        
        return regDest;
    }

    public abstract void bodyBranch(DecacCompiler compiler, Label start, Label end, int numberOfLabel);

    // public void instrCondBool(DecacCompiler compiler)
    // {
    //     //if (getLeftOperand() instanceof BooleanLiteral)


    //     compiler.addInstruction(new CMP(0, compiler.getRegAlloc().getLastRegister()));
    //     compiler.addInstruction(new BEQ(compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(), compiler.getManagementLabel().getNumberOfLabelIfThenElse())));
    // }
}
