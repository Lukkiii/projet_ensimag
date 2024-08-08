package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

public class DeclMethods extends AbstractDeclMethods{
    private AbstractIdentifier type;
    private AbstractIdentifier name;
    private ListDeclParams listParams;
    private AbstractMethodBody methodbody;

    public DeclMethods(AbstractIdentifier type, AbstractIdentifier name, ListDeclParams listParams, AbstractMethodBody methodbody){
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(listParams);
        Validate.notNull(methodbody);
        this.type = type;
        this.name = name;
        this.listParams = listParams;
        this.methodbody = methodbody;
    }
    private ErrorMessage errorMessageNull = new ErrorMessage("Object is null", new Label("null_dereferencing"));
    private ErrorMessage errorMessageStackOverflow = new ErrorMessage("Stack Overflow", new Label("stack_overflow_error"));


    public AbstractIdentifier getNameIdentifier()
    {
        return name;
    }
        
    /**
    * 
    * 
    * @author yuxuan
    * @date 15/01/2024
    */
    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.name.decompile(s);
        s.print("(");
        this.listParams.decompile(s);
        s.print(")");
        this.methodbody.decompile(s);
    }

    @Override
    protected void verifyDeclMethodMember(DecacCompiler compiler, ClassDefinition classDef)
            throws ContextualError {
        // SymbolTable.Symbol methodSymbol = compiler.createSymbol(name.getName().getName());
        //System.out.println(methodSymbol.getName());
        Type t = type.verifyType(compiler);
        Signature signature = listParams.verifyListDeclParamsMember(compiler);
        ExpDefinition parentDef = classDef.getSuperClass().getMembers().get(name.getName());
        int index;
        if (parentDef != null) {
            if (!parentDef.isMethod()) {
                throw new ContextualError("Method name declared in superclass and not declared as method type", getLocation());
            }
            if (!((MethodDefinition) parentDef).getSignature().equals(signature)){
                throw new ContextualError("Method is declared in superclass and has different signature from its superclass", getLocation());
            }
            if (t.isClass()) {
                if (!parentDef.getType().isClass()) {
                    // if parent method is not class type but current method is class type
                    throw new ContextualError("Method return type is not compatible with the redeclared method return type", getLocation());
                }
                if (!(t.sameType(parentDef.getType()) || (((ClassType) (t)).isSubClassOf((ClassType) (parentDef.getType()))))) {
                    // if both method are of class type, check if they are child method is subtype or same type of parent method
                    throw new ContextualError("Method return type is not compatible with the redeclared method return type", getLocation());
                }
            } else {
                // return type of current method is not a class
                if (!t.sameType(parentDef.getType())) {
                    throw new ContextualError("Method return type is not compatible with the redeclared method return type", getLocation());
                }
            }
            // get index of parent method is method is redeclared
            index = ((MethodDefinition) parentDef).getIndex();
            
        } else {
            if (this.name.getName().getName().equals("equals")) {
                // Method equals always the first
                index = 0;
            } else {
                // only increase index number for method if not in superclass
                classDef.incNumberOfMethods();
                index = classDef.getNumberOfMethods();
            }
        }
        MethodDefinition methodDef = new MethodDefinition(t, getLocation(), signature, index);

        try {
            classDef.getMembers().declare(name.getName(), methodDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Method is redeclared", getLocation());
        }
        name.setDefinition(methodDef);
        name.setType(t);
    }
    
    @Override
    protected void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef) throws ContextualError {
        Type returnType = this.type.verifyType(compiler);
        EnvironmentExp envExpParams = this.listParams.verifyListDeclParamBody(compiler);

        this.methodbody.verifyMethodBody(compiler, envExp, envExpParams, classDef, returnType);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        listParams.prettyPrint(s, prefix, false);
        methodbody.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        listParams.iter(f);
        methodbody.iter(f);
    }

    @Override
    public void setLabelAndOperand(DecacCompiler compiler, DeclClass classe)
    {
        // Set Label Before
        Label labelStart = new Label("code." + classe.getNameString() + "." + name.getName());
        name.getMethodDefinition().setLabel(labelStart);
        DAddr dAddr =  compiler.getStack().newGlobal();
        name.getMethodDefinition().setOperand(dAddr);

    }


    @Override
    public void codeGenDeclMethod(DecacCompiler compiler, DeclClass classe)
    {

        compiler.addComment("---------- Code de la methode "+ name.getName().getName() +" dans la classe "+classe.getNameString()+" ligne "+getLocation().getLine());
  
        //this.initialization.codeGenInit(compiler, dAddr);
        compiler.addLabel(name.getMethodDefinition().getLabel());
        
        // test si assez de place dans pile 
        compiler.addInstruction(new TSTO(2));
        if (!compiler.isSetErrorMessage(4))
            compiler.setErrorMessage(4, errorMessageStackOverflow);
        compiler.addInstruction(new BOV(errorMessageStackOverflow.getLabel()));

        compiler.addComment("Sauvegarde des registres");
        codeGenSaveRegister(compiler);

        // SetOperand pour les paramètres
        int index = -3;
        for (AbstractDeclParams param : listParams.getList()) {
            DeclParams parametre = (DeclParams) param;
            parametre.codeGenInst(compiler, index);
            index--;
        }
        
        if (name.getName().toString() == "equals")
            codeGenEquals(compiler);
        else
            methodbody.codeGenMethodBody(compiler, this, classe);
        
        compiler.addLabel(new Label("fin." + classe.getNameString() + "." + name.getName().getName()));

        compiler.addComment("Restauration des registres");
        codeGenRestoreRegister(compiler);

        compiler.addInstruction(new RTS());
    }

    public void codeGenSaveRegister(DecacCompiler compiler) {
        for (int index = 2; index <= listParams.getList().size() + 2; index++) {
            compiler.addInstruction(new PUSH(Register.getR(index)));
        }
    }
    
    public void codeGenRestoreRegister(DecacCompiler compiler) {
        for (int index = listParams.getList().size() + 2; index >= 2; index--) {
            compiler.addInstruction(new POP(Register.getR(index)));
        }
    }


    public void codeGenEquals(DecacCompiler compiler)
    {
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessageNull);
            
        DVal instThis =  new RegisterOffset(-2, Register.LB);
        DVal param = new RegisterOffset(-3, Register.LB);

        compiler.addInstruction(new LOAD(instThis, Register.getR(2)));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
        compiler.addInstruction(new BEQ(errorMessageNull.getLabel()));

        compiler.addInstruction(new LOAD(param, Register.getR(3)));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(3)));
        compiler.addInstruction(new BEQ(errorMessageNull.getLabel()));
        
        compiler.addInstruction(new SUB(Register.getR(3), Register.getR(2)));
        compiler.addInstruction(new SEQ(Register.R0));
    }

    @Override
    protected DeclMethods clone() {
        return new DeclMethods(type, name, listParams, methodbody);
    }

}
