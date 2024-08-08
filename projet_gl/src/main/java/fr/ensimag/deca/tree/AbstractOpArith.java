package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    private ErrorMessage errorMessage = new ErrorMessage("Overflow during arithmetic operation", new Label("overflow_error"));

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
        //         throw new Con    textualError("The types are not the same", getLocation());

        // 3.33.1.2
            if (!(leftType.isInt() || leftType.isFloat())){
                throw new ContextualError("The type is not integer or float", getLocation());
            }
        
        if (leftType.isFloat() && !rightType.isFloat()) 
        {
            setRightOperand(new ConvFloat(rightPart));
            setType(compiler.environmentType.FLOAT);
        } 
        else if (!leftType.isFloat() && rightType.isFloat()) 
        {
            setLeftOperand(new ConvFloat(leftPart));
            setType(compiler.environmentType.FLOAT);
        } 
        else if (leftType.isInt() && rightType.isInt()) 
        {
            setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;
        }
        else{
            setType(compiler.environmentType.FLOAT);
        }
        
        return compiler.environmentType.FLOAT;
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        if (!compiler.isSetErrorMessage(3))
            compiler.setErrorMessage(3, errorMessage);
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        if (!compiler.isSetErrorMessage(3))
            compiler.setErrorMessage(3, errorMessage);
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        this.codeExp( compiler.getRegAlloc().getLastRegister(), compiler);
        return compiler.getRegAlloc().getLastRegister();
    }

}
