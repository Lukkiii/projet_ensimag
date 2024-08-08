package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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

        // // 3.33.1.1
        //     if (!leftType.sameType(rightType))
        //         throw new ContextualError("The types are not the same", getLocation());

        // 3.33.1.2
            if (!(leftType.isInt() || leftType.isFloat() || leftType.isBoolean()))
                throw new ContextualError("The type is not integer or float", getLocation());
        
        if (leftType.isFloat() && !rightType.isFloat())
            setRightOperand(new ConvFloat(rightPart));
        else if (!leftType.isFloat() && rightType.isFloat())
            setLeftOperand(new ConvFloat(leftPart));

        setType(compiler.environmentType.BOOLEAN);

        return compiler.environmentType.BOOLEAN;
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

    public DVal codeExp(DVal location, DecacCompiler compiler)
    {
        if (dVal(this.getRightOperand()) != null)
        {
            DVal regDest = this.getLeftOperand().codeGenInst(compiler);

            compiler.addInstruction(new SUB(dVal(this.getRightOperand()), (GPRegister)regDest));
            
            this.instr(compiler, null, regDest);

            return compiler.getRegAlloc().getLastRegister();
        }
        else
        {
            DVal regDest = this.getLeftOperand().codeGenInst(compiler);

            compiler.getRegAlloc().allocate();
            
            DVal regSource = this.getRightOperand().codeGenInst(compiler);
            
            compiler.addInstruction(new SUB(regSource, (GPRegister)regDest));

            compiler.getRegAlloc().deallocate((GPRegister)regSource);

            this.instr(compiler, null, regDest);

            return compiler.getRegAlloc().getLastRegister();
        }
    }

    public abstract void instrBool(DecacCompiler compiler, Label label);
}
