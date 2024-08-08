package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.deca.codegen.arm.Function;
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
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Cast : (type) (expr)
 *
 * @author yuxuan
 * @date 09/01/2024
 */
public class Cast extends AbstractExpr{
    private AbstractIdentifier type;
    private AbstractExpr expr;

    private ErrorMessage errorMessageStackOverflow = new ErrorMessage("Stack Overflow", new Label("stack_overflow_error"));

    public Cast(AbstractIdentifier type, AbstractExpr expr){
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
                Type castType = this.type.verifyType(compiler);
                Type exprType = this.expr.verifyExpr(compiler, localEnv, currentClass);

                if(!(compiler.getEnvType().cast_compatible(exprType, castType))){
                    throw new ContextualError("Error : Compatibility for conversion", this.getLocation());
                }

                this.type.setDefinition(new TypeDefinition(castType, getLocation()));
                this.setType(castType);
                return this.getType();
            }

    @Override     
    protected DVal codeGenPrint(DecacCompiler compiler) {
        return codeGenInst(compiler);
    }
        
    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        if (!compiler.isSetErrorMessage(4))
            compiler.setErrorMessage(4, errorMessageStackOverflow);

        // Conversion d'un FLOAT en INT
        if (type.getType().isInt() && expr.getType().isFloat() ) {
            DVal reg = expr.codeGenInst(compiler);
            compiler.addInstruction(new INT(reg, compiler.getRegAlloc().getLastRegister()));            
            return compiler.getRegAlloc().getLastRegister();
        } 
        // Converion d'un INT en FLOAT
        else if (type.getType().isFloat() && expr.getType().isInt()) {
            DVal reg = expr.codeGenInst(compiler);
            compiler.addInstruction(new FLOAT(reg, compiler.getRegAlloc().getLastRegister()));
            return compiler.getRegAlloc().getLastRegister();
        } 
        // Transtypage entre deux classes
        else if (type.getType().isClass() && expr.getType().isClass()){
            AbstractIdentifier classToCast = (AbstractIdentifier) expr;
            
            DVal addrClass = expr.codeGenInst(compiler);
            
            compiler.addComment("Cast de "+ classToCast.getName().getName()+ " en "+type.getName().getName());
            updateField(compiler, type, addrClass);

            return addrClass;
        }
        
        return null;
    }


    public void updateField(DecacCompiler compiler, AbstractIdentifier finalClass, DVal dAddr)
    {
        // on alloue une nouvelle structure dans le tas avec un new
        //ListDeclFields listFields = finalClass.getClassDefinition().getListDeclFields();
        
        TypeDefinition classCourante = compiler.getEnvType().defOfType(compiler.createSymbol(finalClass.getName().getName()));
        int alloc = ((ClassDefinition) classCourante).getNumberOfFields()+1;
        compiler.addInstruction(new NEW(alloc, Register.getR(2)));
        compiler.addInstruction(new BOV(errorMessageStackOverflow.getLabel()));
        DAddr dA = (DAddr) ((ClassDefinition) classCourante).getOperand();
        compiler.addInstruction(new LEA(dA, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0,Register.getR(2))));
        compiler.addInstruction(new PUSH(Register.getR(2)));
        compiler.addInstruction(new POP(Register.getR(2)));
    }
     
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        this.type.decompile(s);
        s.print(")");
        s.print(" (");
        this.expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void codeGenExprARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        Function.call(compiler, ARMProgram.LibDeca.conv_float, reg, this.expr);
    }
}
