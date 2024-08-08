package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Immediate;
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
 *
 * @author gl41
 * @date 01/01/2024
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.getManagementLabel().addLabelExprWhile(getLocation().getLine(), getLocation().getPositionInLine());
        compiler.addLabel(compiler.getManagementLabel().getLabelWhileStart(getLocation().getLine(), getLocation().getPositionInLine()));
        codeGenCond(compiler);
        codeGenBody(compiler);
        return compiler.getRegAlloc().getLastRegister();
    }

    protected  Register codeGenInstARM(DecacCompiler compiler) {
        fr.ensimag.arm.pseudocode.Label start = new fr.ensimag.arm.pseudocode.Label("while_start_" + getLocation().getLine() + "_" + getLocation().getPositionInLine());
        fr.ensimag.arm.pseudocode.Label end = new fr.ensimag.arm.pseudocode.Label("while_end_" + getLocation().getLine() + "_" + getLocation().getPositionInLine());

        compiler.armProgram.main.addLabel(start);

        // Condition
        Register reg = compiler.armAllocator.alloc();
        condition.codeGenExprARM(compiler, reg);
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(reg, new Immediate(0)));
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BEQ(end));
        compiler.armAllocator.deAlloc(reg);

        // Body
        body.codeGenListInstARM(compiler);
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.B(start));

        compiler.armProgram.main.addLabel(end);

        return null;

    }
//    @Override
//    protected Register codeGenInstARM(DecacCompiler compiler) {
//        fr.ensimag.arm.pseudocode.Label start = new fr.ensimag.arm.pseudocode.Label("Start" + getLocation().getLine() + "_" + getLocation().getPositionInLine());
//        fr.ensimag.arm.pseudocode.Label end = new fr.ensimag.arm.pseudocode.Label("End" + getLocation().getLine() + "_" + getLocation().getPositionInLine());
//
//        compiler.armProgram.main.addLabel(start);
//
//        codeGenCondARM(compiler, start, end);
//        codeGenBodyARM(compiler, start, end);
//        return null;
//    }
    
    private void codeGenCondARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Label start, fr.ensimag.arm.pseudocode.Label end) {
        condition.codeGenInstARM(compiler);
        Register reg = compiler.armAllocator.alloc(); 
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.CMP(reg, new Immediate(0)));
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BEQ(end));
        compiler.armAllocator.deAlloc(reg);
    }
    
    private void codeGenBodyARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Label start, fr.ensimag.arm.pseudocode.Label end) {
        for (AbstractInst inst : body.getList())
        {
            inst.codeGenInstARM(compiler);
        }
        
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.B(start));
        compiler.armProgram.main.addLabel(end);
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        for (AbstractInst inst: this.body.getList()) {
            inst.verifyInst(compiler, localEnv, currentClass, returnType);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    public void codeGenCond(DecacCompiler compiler)
    {   
        if (condition instanceof AbstractOpBool){
            AbstractOpBool cond = (AbstractOpBool) condition; 
            cond.codeGenInst(compiler);
            compiler.addInstruction(new CMP(0, compiler.getRegAlloc().getLastRegister()));
            compiler.addInstruction(new BEQ(compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine())));
        }
        else if (condition instanceof BooleanLiteral)
        {
            BooleanLiteral cond = (BooleanLiteral) condition; 
            cond.codeGenInst(compiler);
            cond.instrBool(compiler, compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine()));
        }
        else if (condition instanceof AbstractOpCmp)
        {
            AbstractOpCmp cond = (AbstractOpCmp) condition; 
            cond.codeGenInst(compiler);
            cond.instrBool(compiler, compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine()));
        } 
        else if (condition instanceof Identifier)
        {   
            Identifier cond = (Identifier) condition;
            cond.codeGenInst(compiler);
            cond.instrCondBool(compiler, compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine()));
        }

        else if (condition instanceof Not)
        {
            Not cond = (Not) condition; 
            cond.codeGenInst(compiler);
            cond.instrBool(compiler, compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine()));
        }
    }

    public void codeGenBody(DecacCompiler compiler)
    {
        for (AbstractInst inst : body.getList())
        {
            inst.codeGenInst(compiler);
        }
        
        compiler.addInstruction(new BRA(compiler.getManagementLabel().getLabelWhileStart(getLocation().getLine(), getLocation().getPositionInLine())));
        compiler.addLabel(compiler.getManagementLabel().getLabelWhileEnd(getLocation().getLine(), getLocation().getPositionInLine()));
    }
}
