package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
/**
 * Select
 *
 * @author yuxuan
 * @date 23/12/2023
 */

public class Selection extends AbstractLValue{
    private AbstractExpr obj;
    private AbstractIdentifier field;

    public Selection(AbstractExpr obj, AbstractIdentifier field){
        Validate.notNull(obj);
        Validate.notNull(field);
        this.obj = obj;
        this.field = field;
    }

    public AbstractExpr getObject()
    {
        return obj;
    }

    public AbstractIdentifier getField()
    {
        return field;
    }


    private ErrorMessage errorMessage = new ErrorMessage("Object is null", new Label("null_dereferencing"));
        
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass) throws ContextualError {

        Type leftType = this.obj.verifyExpr(compiler, localEnv, currentClass);
        ClassDefinition class2 = leftType.asClassType("Type error : should be a class type", this.getLocation()).getDefinition();
        
        EnvironmentExp envExpField = class2.getMembers();
        Type fieldType = this.field.verifyExpr(compiler, envExpField, class2);

        FieldDefinition fieldDef = this.field.getDefinition().asFieldDefinition("Type error : should be a field", this.getLocation());
        Visibility visibility = fieldDef.getVisibility();
        

        if(visibility == Visibility.PUBLIC){
            this.setType(fieldType);
            Type returnType = this.getType();
            return returnType;
        }else if(visibility == Visibility.PROTECTED){
            if (currentClass == null) {
                throw new ContextualError("Error: is not a subtype", getLocation());
            }
            if(!(compiler.getEnvType().subType(leftType, currentClass.getType())) || 
               !(compiler.getEnvType().subType(currentClass.getType(), fieldType))){
               throw new ContextualError("Error : is not a subtype", this.getLocation());
            }
            this.setType(fieldType);
            Type returnType = this.getType();
            return returnType;
        }

        if (obj instanceof This)
        {
            This object = (This) obj;
            object.setImplicit();
        }
        
        this.setType(fieldType);
        Type returnType = this.getType();
        return returnType;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.obj.decompile(s);
        s.print(".");
        this.field.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        obj.iter(f);
        field.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    protected DVal codeGenPrint(DecacCompiler compiler) { 
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessage);
        
        field.setImplicit();   

        DVal regDest = obj.codeGenInst(compiler);
    
        instr(compiler, null, regDest);
        
        DVal regD = field.codeGenInst(compiler);

        //compiler.addInstruction(new LOAD(regDest, Register.R0));
        
        return regD;   
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) { 
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessage);

        field.setImplicit();        

        DVal regDest = obj.codeGenInst(compiler);
        
        instr(compiler, null, regDest);
        
        DVal regD = field.codeGenInst(compiler);

        //compiler.addInstruction(new LOAD(regDest, Register.R0));
        
        return regD;    
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        compiler.addInstruction(new CMP(new NullOperand(), (GPRegister)locDest));
        compiler.addInstruction(new BEQ(errorMessage.getLabel()));
    }
}
