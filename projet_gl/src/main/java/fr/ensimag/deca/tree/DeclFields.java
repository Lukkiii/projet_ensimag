package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;

public class DeclFields extends AbstractDeclFields{
    private AbstractIdentifier type;
    private AbstractIdentifier field;
    private AbstractInitialization init;
    private Visibility v;

    public DeclFields(AbstractIdentifier type, AbstractIdentifier field, AbstractInitialization init, Visibility v){
        Validate.notNull(type);
        Validate.notNull(field);
        Validate.notNull(init);
        this.type = type;
        this.field = field;
        this.init = init;
        this.v = v;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(this.v == Visibility.PROTECTED){
           s.print("protected ");
        }
        this.type.decompile(s);
        s.print(" ");
        this.field.decompile(s);
        if (this.init instanceof Initialization)
        {
            s.print(" = ");
            this.init.decompile(s);
        }
        s.println(";");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    @Override
    protected void verifyDeclFieldMember(DecacCompiler compiler, ClassDefinition classDef)
            throws ContextualError {
        // SymbolTable.Symbol fieldSymbol = compiler.createSymbol(field.getName().getName());
        
        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Field: " + field.getName() + " should not be of type void", getLocation());
        }
        ExpDefinition parentDef = classDef.getSuperClass().getMembers().get(field.getName());
        if (parentDef != null && !parentDef.isField()) {
            throw new ContextualError("Field: " + field.getName() + " is declared in its superclass and not declared as field type", getLocation());
        }
        classDef.incNumberOfFields();
        FieldDefinition fieldDef = new FieldDefinition(t, getLocation(), v, classDef, classDef.getNumberOfFields());
        try {
            classDef.getMembers().declare(field.getName(), fieldDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Field is redeclared", getLocation());
        }
        field.setDefinition(fieldDef);
        field.setType(t);
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    @Override
    protected void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef) throws ContextualError {
        Type type = this.type.verifyType(compiler);
        this.init.verifyInitialization(compiler, type, envExp, classDef);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        field.iter(f);
        init.iter(f);
    }


    public void codeGenDeclField(DecacCompiler compiler)
    {
        compiler.addComment("Initialisation de " + field.getName());

        // LOAD #VALUE, R0
        DVal reg = this.init.codeGenInitField(compiler, Register.R0);
        
        // LOAD -2(LB)
        int i = compiler.getRegAlloc().allocate();
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(i)));
        
        DVal regDest = compiler.getRegAlloc().getLastRegister();
        compiler.getRegAlloc().deallocate(regDest);



        // STORE R0, INDEX(R1)
        RegisterOffset regDestFinal = new RegisterOffset(field.getFieldDefinition().getIndex(), (Register)regDest);

        compiler.addInstruction(new STORE((Register)reg, regDestFinal));

        field.getFieldDefinition().setOperand(regDestFinal);  
        //field.getFieldDefinition().setOperandOffset(field.getFieldDefinition().getIndex());   
    }  
    
    protected String prettyPrintNode(){
        return "[visibility = " + this.v.toString().toUpperCase() + "]" + " DeclField";
    }
    
}
