package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl41
 * @date 01/01/2024
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    /**
     * 
     * @param compiler
     * @param localEnv
     * @param currentClass
     * 
     * @throws ContextualError The type is not integer or float
     * 
     * @author heuzec
     * @date 24/12/2023
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // Operand 
            Type operandType = this.getOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!operandType.isFloat() && !operandType.isInt())
            throw new ContextualError("The type is not integer or float", this.getLocation());
        
        this.setType(operandType);        

        return operandType;
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        // this.codeExp(2, compiler);

        return Register.getR(2);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        DVal val = codeExp(compiler.getRegAlloc().getLastRegister(), compiler);
        return (GPRegister) val;
    }



    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        compiler.addInstruction(new OPP(dVal(expr),(GPRegister)locDest));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        compiler.addInstruction(new OPP(source,(GPRegister)dest));
    }
}
