package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * new
 *
 * @author yuxuan
 * @date 09/01/2024
 */
public class New extends AbstractExpr{
    private AbstractIdentifier newClass;

    private ErrorMessage errorMessageHeapOverflow = new ErrorMessage("Heap Overflow", new Label("heap_overflow_error"));


    public New(AbstractIdentifier newClass){
        Validate.notNull(newClass);
        this.newClass = newClass;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
                Type returnType = this.newClass.verifyType(compiler);
                if(!returnType.isClass())
                    throw new ContextualError("Type error : should be a class type", this.getLocation());
                return returnType;
            }

        
    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        if (!compiler.isSetErrorMessage(0))
            compiler.setErrorMessage(0, errorMessageHeapOverflow);
        instr(compiler, null, null);
        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        this.newClass.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        newClass.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        newClass.prettyPrint(s, prefix, true);
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        TypeDefinition classCourante = compiler.getEnvType().defOfType(compiler.createSymbol(newClass.getName().getName()));
        int alloc = ((ClassDefinition) classCourante).getNumberOfFields()+1;
        compiler.addInstruction(new NEW(alloc, Register.getR(2)));
        compiler.addInstruction(new BOV(errorMessageHeapOverflow.getLabel()));
        DAddr dA = (DAddr) ((ClassDefinition) classCourante).getOperand();
        compiler.addInstruction(new LEA(dA, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0,Register.getR(2))));
        compiler.addInstruction(new PUSH(Register.getR(2)));
        compiler.addInstruction(new BSR(new Label("init."+newClass.getName().getName()))); // a changer
        compiler.addInstruction(new POP(Register.getR(2)));
       // DAddr adrNewObj = compiler.getStack().newGlobal();
        //((ClassDefinition) classCourante).setOperand(adrNewObj);
        //compiler.addInstruction(new STORE(Register.getR(2), adrNewObj));
    }   
}
