package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Deca Identifier
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Identifier extends AbstractIdentifier {
    
    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
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
     * @throws ContextualError No Definition
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        ExpDefinition definition = localEnv.get(this.name);

        if (definition == null)
            throw new ContextualError("No definition for this identifier: " + this.name, this.getLocation());
        
        this.setType(definition.getType());

        this.setDefinition(definition);

        //This ourThis;
        //if (getDefinition() instanceof FieldDefinition)
        //{
        //    ourThis
        //}

        return definition.getType();
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     * 
     * @throws ContextualError No declaration of this type
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
    
        Type type = compiler.getType(name.getName());

        if (type == null)
            throw new ContextualError("No declaration of this type identifier: " + name.getName(), this.getLocation());
        
        this.setType(type);

        this.setDefinition(compiler.getDefinition(type));

        return type;
    }

    @Override
    public DVal codeGenPrint(DecacCompiler compiler)
    {
        DAddr dAddr;
        if (this.getDefinition() instanceof FieldDefinition)
        {
            if(!isImplicit())
            {
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), compiler.getRegAlloc().getLastRegister()));
            }
            dAddr = new RegisterOffset(this.getFieldDefinition().getIndex(), compiler.getRegAlloc().getLastRegister());
        }
        else
        {
            dAddr = ((ExpDefinition)getDefinition()).getOperand();
        }
        compiler.addInstruction(new LOAD(dAddr, compiler.getRegAlloc().getLastRegister()));
        return compiler.getRegAlloc().getLastRegister();
    }

    @Override
    public DVal codeGenInst(DecacCompiler compiler)
    {
        DAddr dAddr;
        if (this.getDefinition() instanceof FieldDefinition)
        {
            if(!isImplicit())
            {
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), compiler.getRegAlloc().getLastRegister()));
            }
            dAddr = new RegisterOffset(this.getFieldDefinition().getIndex(), compiler.getRegAlloc().getLastRegister());
        }
        else
        {
            dAddr = ((ExpDefinition)getDefinition()).getOperand();
        }
        compiler.addInstruction(new LOAD(dAddr, compiler.getRegAlloc().getLastRegister()));
        return compiler.getRegAlloc().getLastRegister();
    }

    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    @Override
    public void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest)
    {
        // nothing to do
    }

    
    // utilisé lorsque l'on a uniquement if(x)
    public void instrCondBool(DecacCompiler compiler, Label label)
    {
        compiler.addInstruction(new BEQ(label));
    }

    @Override
    public ParamDefinition getParamDefinition() {
        try {
            return (ParamDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    @Override
    public void setParamDefinition(Definition definition) {
        throw new UnsupportedOperationException("Unimplemented method 'setParamDefinition'");
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        // fr.ensimag.arm.pseudocode.Register reg = compiler.armAllocator.alloc();
        // //for now we only got variable identifier
        // fr.ensimag.arm.pseudocode.Label lab = (fr.ensimag.arm.pseudocode.Label)((ExpDefinition)this.getDefinition()).getOperandARM();
        // compiler.armProgram.main.addInstruction(fr.ensimag.arm.pseudocode.instructions.LDR(reg, lab));
        // return reg;
        fr.ensimag.arm.pseudocode.Register reg = compiler.armAllocator.alloc();
        return null;
    }

    public void codeGenExprARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        // Strong assumption for now.        
        fr.ensimag.arm.pseudocode.Label lab = (fr.ensimag.arm.pseudocode.Label)((ExpDefinition)this.getDefinition()).getOperandARM();
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, lab)); // Load address
        compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(reg, reg)); // Load value
    }

}
