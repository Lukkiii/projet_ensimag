package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // Left part
            AbstractExpr leftPart = this.getLeftOperand();
            Type leftType = leftPart.verifyExpr(compiler, localEnv, currentClass);
        // Right part
            AbstractExpr rightPart = this.getRightOperand();
            Type rightType = rightPart.verifyExpr(compiler, localEnv, currentClass);

        // 3.33.4
            if (!leftType.isInt() || !rightType.isInt())
                throw new ContextualError("The type is not integer", getLocation());

        return leftType;
    }


    @Override
    protected String getOperatorName() {
        return "%";
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
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        compiler.addInstruction(new REM(dVal(expr),(GPRegister)locDest));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new REM(source,(GPRegister)dest));
    }
}
