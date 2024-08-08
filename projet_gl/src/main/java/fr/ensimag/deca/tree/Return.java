package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Return
 *
 * @author yuxuan
 * @date 23/12/2023
 */
public class Return extends AbstractInst{
    private AbstractExpr expr;

    public Return(AbstractExpr expr){
        Validate.notNull(expr);
        this.expr = expr;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        if(returnType.isVoid()){
            throw new ContextualError("Return type should not be void.", this.getLocation());
        }
        try{
            this.expr.verifyRValue(compiler, localEnv, currentClass, returnType);
        } catch(ContextualError e) {
            throw new ContextualError("Expected type: " + expr.getType() + " but returned type: " + returnType, this.getLocation());
        }
    }
            
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        this.expr.decompile(s);
        s.print(";");
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }
    
    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.addComment("Instruction Return ligne " + getLocation());
        // On évalue le résultat de l'expression que l'on souhaite retourner
        DVal source = expr.codeGenInst(compiler);
       compiler.addInstruction(new LOAD(source, Register.R0));
        
        // Le résultat se trouve dans R0
        return Register.R0;
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }


}
