package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.arm.Function;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Binary expressions.
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    /**
     * 
     * @return leftOperand of the binary expression
     */
    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    /**
     * 
     * @return rightOperand of the binary expression
     */
    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    /**
     * Set the leftOperand
     * @param leftOperand
     */
    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

    protected abstract void instrBin(DecacCompiler compiler, DVal source, DVal dest);



    public DVal codeExp(DVal location, DecacCompiler compiler)
    {
        if (dVal(this.getRightOperand()) != null && ! (this.getRightOperand() instanceof Identifier))
        {
            DVal regDest = this.getLeftOperand().codeGenInst(compiler);
            this.instr(compiler, getRightOperand(), regDest);
            return regDest;
        }
        else
        {
            DVal regDest = this.getLeftOperand().codeGenInst(compiler);
            compiler.getRegAlloc().allocate();
            DVal regSource = this.getRightOperand().codeGenInst(compiler);
            compiler.getRegAlloc().deallocate((GPRegister)regSource);
            this.instrBin(compiler, regSource, regDest);
            return regDest;
        }
    }

    @Override
    public void codeGenExprARM(DecacCompiler compiler, Register reg) {
        fr.ensimag.arm.pseudocode.Register rightReg = reg;
        this.getRightOperand().codeGenExprARM(compiler, rightReg);

        fr.ensimag.arm.pseudocode.Register leftReg = compiler.armAllocator.alloc();
        this.getLeftOperand().codeGenExprARM(compiler, leftReg);

        // Opérations arithmétiques
        if (this instanceof Plus) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.ADD(reg, leftReg, rightReg));
        } else if (this instanceof Minus) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.SUB(reg, leftReg, rightReg));
        } else if (this instanceof Multiply) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MUL(reg, leftReg, rightReg));
        } 
        // Opérations booléennes
        else if (this instanceof And) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.AND(reg, leftReg, rightReg));
        } else if (this instanceof Or) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.ORR(reg, leftReg, rightReg));
        } 
        // Opérations de comparaisons
        else if (this instanceof Greater) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVGT(reg,new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVLE(reg, new Immediate(0)));
        }else if (this instanceof GreaterOrEqual) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVGE(reg,new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVLT(reg, new Immediate(0)));
        }else if (this instanceof Lower) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVLT(reg, new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVGE(reg,new Immediate(0)));
        }else if (this instanceof LowerOrEqual) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVLE(reg, new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVGT(reg,new Immediate(0)));
        }else if (this instanceof Equals) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVEQ(reg,new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVNE(reg, new Immediate(0)));
        }else if (this instanceof NotEquals) {
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(leftReg, rightReg));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVNE(reg,new Immediate(255)));
            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOVEQ(reg, new Immediate(0)));
        } 
        // Problèmes
        else {
            throw new DecacInternalError("The code above me is not written carefully!");
        }
        compiler.armAllocator.deAlloc(leftReg);
    }
}
