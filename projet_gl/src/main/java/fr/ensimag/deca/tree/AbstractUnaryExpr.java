package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public AbstractExpr getOperand() {
        return operand;
    }
    private AbstractExpr operand;
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }


    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName() + " (");
        getOperand().decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }
    
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest){
        throw new UnsupportedOperationException("not yet implemented");
    };


    public DVal codeExp(DVal location, DecacCompiler compiler)
    {
        if (dVal(this.getOperand()) != null)
        {
            DVal regDest = this.getOperand().codeGenInst(compiler);
            this.instr(compiler, getOperand(), regDest);
            return regDest;        
        }
        else
        {
            DVal regSource = this.getOperand().codeGenInst(compiler);
            this.instrBin(compiler, regSource, regSource);
            return regSource;  
        }    
    }
    
    @Override
    public void codeGenExprARM(DecacCompiler compiler, Register reg) {

        this.getOperand().codeGenExprARM(compiler, reg);

        // Opérations arithmétiques
        if (this instanceof UnaryMinus) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.RSB(reg, reg, new Immediate(0)));
        }
        // Opérations booléennes
        else if (this instanceof Not) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MVN(reg, reg));
        }
        // Problèmes
        else {
            throw new DecacInternalError("The code above me is not written carefully!");
        }
    }
}
