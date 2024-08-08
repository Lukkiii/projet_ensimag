package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.codegen.arm.Function;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;  

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl41
 * @date 01/01/2024
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) throws ContextualError {
        super(operand);
        this.verifyExpr(null, null, null);
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        this.instr(compiler, this, compiler.getRegAlloc().getLastRegister());
        return compiler.getRegAlloc().getLastRegister();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }    

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        ConvFloat value = (ConvFloat)expr;

        DVal reg = value.getOperand().codeGenInst(compiler);
        compiler.addInstruction(new FLOAT(reg, (GPRegister)locDest));
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        return this.getType();
    }
}
