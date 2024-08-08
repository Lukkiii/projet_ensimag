package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Label;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl41
 * @date 01/01/2024
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.getManagementLabel().addLabelExprIfThenElse(getLocation().getLine(), getLocation().getPositionInLine());
        codeGenCond(compiler);
        codeGenThenBranch(compiler);
        codeGenElseBranch(compiler);

        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.println("} else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }

    public void codeGenCond(DecacCompiler compiler)
    {
        if (condition instanceof AbstractOpBool){
            AbstractOpBool cond = (AbstractOpBool) condition; 
            cond.codeGenInst(compiler);
            compiler.addInstruction(new CMP(0, compiler.getRegAlloc().getLastRegister()));
            compiler.addInstruction(new BEQ(compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(), getLocation().getPositionInLine())));
        }
        else if (condition instanceof BooleanLiteral)
        {
            BooleanLiteral cond = (BooleanLiteral) condition; 
            cond.codeGenInst(compiler);
            cond.instrBool(compiler, compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(),  getLocation().getPositionInLine()));
        }
        else if (condition instanceof AbstractOpCmp)
        {
            AbstractOpCmp cond = (AbstractOpCmp) condition; 
            cond.codeGenInst(compiler);
            cond.instrBool(compiler, compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(), getLocation().getPositionInLine()));
        } 
        else if (condition instanceof Identifier)
        {   
            Identifier cond = (Identifier) condition;
            cond.codeGenInst(compiler);
            cond.instrCondBool(compiler, compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(), getLocation().getPositionInLine()));
        }
    }

    public void codeGenThenBranch(DecacCompiler compiler)
    {
        for (AbstractInst inst : thenBranch.getList())
        {
            inst.codeGenInst(compiler);
        }
        compiler.addInstruction(new BRA(compiler.getManagementLabel().getLabelIfThenElseEnd(getLocation().getLine(), getLocation().getPositionInLine())));
        compiler.addLabel(compiler.getManagementLabel().getLabelIfThenElseStart(getLocation().getLine(), getLocation().getPositionInLine()));        
    }

    public void codeGenElseBranch(DecacCompiler compiler)
    {
        for (AbstractInst inst : elseBranch.getList())
        {
            inst.codeGenInst(compiler);
        }
        compiler.addLabel(compiler.getManagementLabel().getLabelIfThenElseEnd(getLocation().getLine(), getLocation().getPositionInLine()));
    }


    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        compiler.getManagementLabel().addLabelExprIfThenElse(getLocation().getLine(), getLocation().getPositionInLine());

        Label eelse = new Label("ite_else" + getLocation().getLine() + "_" + getLocation().getPositionInLine());
        Label end = new Label("ite_end" + getLocation().getLine() + "_" + getLocation().getPositionInLine());

        // Condition
        Register reg = compiler.armAllocator.alloc();
        condition.codeGenExprARM(compiler, reg);

        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(reg, new Immediate(0)));
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BEQ(eelse));
        compiler.armAllocator.deAlloc(reg);

        // Then
        thenBranch.codeGenListInstARM(compiler);
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.B(end));
        compiler.armProgram.main.addLabel(eelse);

        // Else
        elseBranch.codeGenListInstARM(compiler);
        compiler.armProgram.main.addLabel(end);

        return null;
    }
}
