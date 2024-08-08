package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

public class DeclParams extends AbstractDeclParams{
    private AbstractIdentifier type;
    private AbstractIdentifier name;
    

    public DeclParams(AbstractIdentifier type, AbstractIdentifier name){
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.name.decompile(s);
    }

    @Override
    protected Type verifyDeclParamMember(DecacCompiler compiler) throws ContextualError {
        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Parameter type should not be void", getLocation());
        }
        return t;
    }
    
    @Override
    protected void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp envExp) throws ContextualError {
        Type paramType = this.type.verifyType(compiler);

        try {
            ParamDefinition paramDef = new ParamDefinition(paramType, this.getLocation());
            envExp.declare(this.name.getName(), paramDef);
            this.name.setDefinition(paramDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Parameter is redeclared.", getLocation());
        }

    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
    }

    public DVal codeGenInst(DecacCompiler compiler, int index) {
        name.getParamDefinition().setOperand(new RegisterOffset(index, Register.LB));

        return new RegisterOffset(index, Register.LB);
    } 
}
