package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateBoolean;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractExpr extends AbstractInst {
    boolean impl;
    
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return this.impl;
    }

    void setImplicit()
    {
        this.impl = true;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
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
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type returnedType = this.verifyExpr(compiler, localEnv, currentClass);
        
        if (returnedType.isClass()) {
            ClassType t = (ClassType) expectedType;

            ClassType otherType = (ClassType) returnedType;

            if (t.isSubClassOf(otherType)) {
                throw new ContextualError("Expected type: " + expectedType + " but returned type: " + returnedType, getLocation());
            }  
        } else {
            if (!returnedType.sameType(expectedType)) {
                throw new ContextualError("Expected type: " + expectedType + " but returned type: " + returnedType, getLocation());
            }            
        }

        if(!(compiler.getEnvType().assign_compatible(expectedType, returnedType))){
            throw new ContextualError("Error : compatibility for assignment", this.getLocation());
        }
        
        setType(returnedType);
        return this;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type conditionType = this.verifyExpr(compiler, localEnv, currentClass);
        if (!conditionType.isBoolean()) {
            throw new ContextualError("Condition expected a boolean type but returned: " + conditionType, getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected DVal codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected DVal codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void codeGenExprARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        fr.ensimag.arm.pseudocode.Register reg = compiler.armAllocator.alloc();
        codeGenExprARM(compiler, reg);
        compiler.armAllocator.deAlloc(reg);
        return null;
    }


    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        char c = ';';
        s.print(c);
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
    
    
    public void choiceInstr(DecacCompiler compiler, DVal reg, boolean isPrintHex)
    {   

        if (getType().isInt())
        {
            compiler.addInstruction(new LOAD(reg, Register.R1), "L'entier doit être lu dans R1");
            compiler.addInstruction(new WINT());
        }
        else if (getType().isBoolean()) {
            compiler.addInstruction(new LOAD(reg, Register.R1), "Le booléen doit être lu dans R1");      
            compiler.addInstruction(new WINT());
        }
        
        else if (getType().isFloat())
        {
            compiler.addInstruction(new LOAD(reg, Register.R1), "Le floattant doit être lu dans R1");
            if (isPrintHex)
                compiler.addInstruction(new WFLOATX());
            else
                compiler.addInstruction(new WFLOAT());
        }
    }


    public DVal dVal(AbstractExpr expr) 
    {
        if (expr instanceof IntLiteral)
        {
            IntLiteral lit = (IntLiteral) expr;
            return new ImmediateInteger(lit.getValue());

        }
        else if (expr instanceof BooleanLiteral)
        {
            BooleanLiteral lit = (BooleanLiteral) expr;
            return new ImmediateBoolean(lit.getValue());
        }
        else if (expr instanceof FloatLiteral)
        {
            FloatLiteral lit = (FloatLiteral) expr;
            return new ImmediateFloat(lit.getValue());
        }
        else if (expr instanceof Identifier)
        {
            Identifier ident = (Identifier) expr;
            DAddr dAddr = ((ExpDefinition) ident.getDefinition()).getOperand();
            return dAddr;
        }
        return null;
    }




    protected abstract void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest);


}
