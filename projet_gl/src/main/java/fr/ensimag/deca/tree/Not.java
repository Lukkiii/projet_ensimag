package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        AbstractExpr expr = this.getOperand();
        
        // Verification if the expression is a condition or not 
            expr.verifyCondition(compiler, localEnv, currentClass);

        Type type = compiler.environmentType.BOOLEAN;

        this.setType(type);

        return type;
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) {
        DVal val = codeExp(compiler.getRegAlloc().getLastRegister(), compiler);
        return val;
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        DVal val = codeExp(compiler.getRegAlloc().getLastRegister(), compiler);
        return val;
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        // !a = 1 - a
        compiler.addInstruction(new SUB(dVal(new IntLiteral(1)),(GPRegister)locDest));
        compiler.addInstruction(new OPP(locDest,(GPRegister)locDest));
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest){
        // !a = 1 - a
        compiler.addInstruction(new SUB(dVal(new IntLiteral(1)),(GPRegister)dest));
        compiler.addInstruction(new OPP(dest,(GPRegister)dest));
    };

    // utilisé lorsque l'on a uniquement if(!(x < y))
    public void instrBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BEQ(label));
    }
}
