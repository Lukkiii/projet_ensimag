package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

public class This extends AbstractExpr {
    boolean impl;

    public This(boolean impl){
        this.impl = impl;
    }

    @Override
    boolean isImplicit() {
        return impl;
    }

    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
                if(currentClass == null)
                    throw new ContextualError("'This' method should be inside a class", this.getLocation());
                Type returnType = currentClass.getType();
                return returnType;
            }
            
    @Override
    public void decompile(IndentPrintStream s) {
        if(this.impl != true){
            s.print("this");
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    String prettyPrintNode() {
        return "This : " + (impl ? "true" : "false");
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) { 
        setImplicit();
        instr(compiler, null, null);        
        return compiler.getRegAlloc().getLastRegister();  
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) { 
        setImplicit();
        instr(compiler, null, null);        
        return compiler.getRegAlloc().getLastRegister();  
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), compiler.getRegAlloc().getLastRegister()));
    }

}
