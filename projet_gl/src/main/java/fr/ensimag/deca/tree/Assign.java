package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     * 
     * @author heuzec
     * @date 24/12/2023
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // Left part
            AbstractExpr leftPart = this.getLeftOperand();
            Type leftType = leftPart.verifyExpr(compiler, localEnv, currentClass);
        
        // Right part
			AbstractExpr rightPart = this.getRightOperand();
			rightPart.verifyRValue(compiler, localEnv, currentClass, leftType);
        
        this.setType(leftType);

        return leftType;
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    protected DVal codeGenInst(DecacCompiler compiler)
    {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));
        
        if (getLeftOperand() instanceof Identifier && getRightOperand() instanceof Identifier)
        {
            Identifier leftPart = (Identifier) getLeftOperand();
            Identifier rightPart = (Identifier) getRightOperand();

            DAddr dAddrDest = ((ExpDefinition) leftPart.getDefinition()).getOperand();
            DAddr dAddrSource = ((ExpDefinition) rightPart.getDefinition()).getOperand();
            
            getRightOperand().codeGenInst(compiler);

            compiler.addInstruction(new LOAD(dAddrSource, Register.getR(2)));

            compiler.addInstruction(new STORE(Register.getR(2), dAddrDest));

            return Register.getR(2);
        }
        else if (getLeftOperand() instanceof Identifier)
        {            
            Identifier leftPart = (Identifier) getLeftOperand();
            DAddr dAddr = ((ExpDefinition) leftPart.getDefinition()).getOperand();
            DVal regDest = getRightOperand().codeGenInst(compiler);
            
            compiler.addInstruction(new STORE((Register)regDest, dAddr));

            return regDest;
        }
        else if (getLeftOperand() instanceof Selection)
        {
            Selection leftPart = (Selection) getLeftOperand();
            compiler.getRegAlloc().allocate();

            DVal registreSrc = leftPart.getObject().codeGenInst(compiler);

            leftPart.instr(compiler, null, registreSrc);

            compiler.getRegAlloc().allocate();
            DVal regDest = getRightOperand().codeGenInst(compiler);
            compiler.getRegAlloc().deallocate(regDest);
            
            if ( !(getRightOperand() instanceof New)) {
                compiler.addInstruction(new STORE((Register)regDest, new RegisterOffset(leftPart.getField().getFieldDefinition().getIndex(), (Register)registreSrc)));           
            }
            compiler.getRegAlloc().deallocate(registreSrc);
            return regDest;
        }

        return null;
    }

    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        fr.ensimag.arm.pseudocode.Register regDst = compiler.armAllocator.alloc();
        codeGenExprARM(compiler, regDst);
        compiler.armAllocator.deAlloc(regDst);
        return null;
    }

    public void codeGenExprARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        // Strong assumption for now.
        if (this.getLeftOperand() instanceof Identifier) {
            fr.ensimag.arm.pseudocode.Addr addr = ((ExpDefinition)((Identifier) this.getLeftOperand()).getDefinition()).getOperandARM();
            // Should always be true
            if (addr instanceof fr.ensimag.arm.pseudocode.Label) {
                fr.ensimag.arm.pseudocode.Label lab = (fr.ensimag.arm.pseudocode.Label)addr;
                getRightOperand().codeGenExprARM(compiler, reg);
                fr.ensimag.arm.pseudocode.Register regDst = compiler.armAllocator.alloc();
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(regDst, lab));
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.STR(reg, new fr.ensimag.arm.pseudocode.RegisterAddr(regDst)));
                compiler.armAllocator.deAlloc(regDst);
            } else {
                throw new DecacInternalError("The ARM operand of an identifier must be an fr.ensimag.arm.pseudocode.Label!");
            }
        }
    }


    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void instrBin(DecacCompiler compiler, DVal source, DVal dest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public DVal codeExp(AbstractExpr expr, int registerNumber, DecacCompiler compiler)
    {
        return null;
    }
}
