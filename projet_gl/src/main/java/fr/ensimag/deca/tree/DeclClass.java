package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.EnvironmentExp;


import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.commons.lang.Validate;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl41
 * @date 01/01/2024
 */
public class DeclClass extends AbstractDeclClass {
    private AbstractIdentifier name;
    private AbstractIdentifier superclass;
    private ListDeclFields fields;
    private ListDeclMethods methods;
    
    public DeclClass(AbstractIdentifier name, AbstractIdentifier superclass
    ,     ListDeclFields fields, ListDeclMethods methods
    ){
        Validate.notNull(name);
        Validate.notNull(superclass);
        Validate.notNull(fields);
        Validate.notNull(methods);
        this.name = name;
        this.superclass = superclass;
        this.fields = fields;
        this.methods = methods;
    }

    /**
    * 
    * 
    * @author yuxuan
    * @date 15/01/2024
    */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        this.name.decompile(s);
        s.print(" extends ");
        this.superclass.decompile(s);
        s.println(" {");
        s.indent();
        this.fields.decompile(s);
        this.methods.decompile(s);
        s.unindent();
        s.println("}");

    }


    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        SymbolTable.Symbol superSymbol = compiler.createSymbol(superclass.getName().getName());
        Type superType = compiler.getEnvType().getType(superSymbol);
        if (superType == null) {
            throw new ContextualError("Superclass: " + superclass.getName() + " is not declared in Environment Type", getLocation());
        }
        
        if (!superType.isClass()) {
            throw new ContextualError("Superclass: " + superclass.getName() + " is not of type Class", getLocation());
        }

        ClassType classType = new ClassType(compiler.createSymbol(name.getName().getName()), getLocation(), ((ClassType) superType).getDefinition());
        ClassDefinition classDefinition = classType.getDefinition();
        try {
            compiler.getEnvType().declareClass(compiler.createSymbol(name.getName().getName()), classDefinition);
        } catch (DoubleDefException e) {
            throw new ContextualError("Class identifier: " + name.getName() + " is already declared previously", getLocation());
        }
        
        name.setDefinition(classDefinition);
        name.setType(classType);
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        ClassDefinition currentClass = name.getClassDefinition();
        currentClass.setNumberOfFields(currentClass.getSuperClass().getNumberOfFields());
        currentClass.setNumberOfMethods(currentClass.getSuperClass().getNumberOfMethods());
        this.fields.verifyListDeclFieldsMembers(compiler, currentClass);
        this.methods.verifyListDeclMethodsMember(compiler, currentClass);
    }


    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp envExp = this.name.getClassDefinition().getMembers();
        ClassDefinition classDef = this.name.getClassDefinition();
        this.fields.verifyListDeclFieldBody(compiler, envExp, classDef);
        this.methods.verifyListDeclMethodBody(compiler, envExp, classDef);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superclass.prettyPrint(s, prefix, false);
        fields.prettyPrint(s, prefix, false);
        methods.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iter(f);
        superclass.iter(f);
        fields.iter(f);
        methods.iter(f);
    }

    public String getNameString() {
        return this.name.getName().getName();
    }

    public void createMethodTables(DecacCompiler compiler)
    {   
        // On met dans la définition de la classe la déclaration de la classe pour plus tard
        this.name.getClassDefinition().setListDeclMethods(this.methods);
        this.name.getClassDefinition().setClasse(this);

        compiler.addComment("Construction de la table des methodes de " + this.name.getName());    
        RegisterOffset locClass = compiler.getStack().newGlobal();

        // le pere
        if (this.superclass.getName().getName() == "Object") {
            name.getClassDefinition().setDeep(1);
            compiler.addInstruction(new LEA(new RegisterOffset(1, Register.GB),Register.R0));     
            compiler.getClassDeep().add(1);
        }
        else
        {
            compiler.addInstruction(new LEA((DAddr)this.name.getClassDefinition().getSuperClass().getOperand(), Register.R0));
            this.name.getClassDefinition().setDeep(this.name.getClassDefinition().getSuperClass().getIndexDeep() + 1);
            compiler.getClassDeep().add(this.name.getClassDefinition().getIndexDeep());
        }

        this.name.getClassDefinition().setOperand(locClass);
        compiler.addInstruction(new STORE(Register.R0, locClass));

        boolean alreadyDone = false;

        // heritage de la méthode equals (on passe uniquement dans le if si la méthode equals n'est pas redefini)
        TypeDefinition classCourante = compiler.getEnvType().defOfType(compiler.createSymbol(name.getName().getName()));
        if (((ClassDefinition) classCourante).getMembers().get(compiler.createSymbol("equals")) == null)
        {   
            if (this.name.getClassDefinition().getSuperClass().getType().getName().getName() == "Object")
            {
                DAddr addrEquals = compiler.getStack().newGlobal();
                compiler.addInstruction(new LOAD(new Label("code.Object.equals"), Register.R0));
                compiler.addInstruction(new STORE(Register.R0, addrEquals));
                LabelOperand lab = new LabelOperand(new Label("code.Object.equals"));
                this.name.getClassDefinition().setOperandEquals(lab);
            }
            else // equals non déféni par ses ancêtres
            {            
                DAddr addrEquals = compiler.getStack().newGlobal();
                compiler.addInstruction(new LOAD(this.name.getClassDefinition().getSuperClass().getOperandEquals(), Register.R0));
                compiler.addInstruction(new STORE(Register.R0, addrEquals));
                this.name.getClassDefinition().setOperandEquals(name.getClassDefinition().getSuperClass().getOperandEquals());
            }

            alreadyDone = true;
        }

        // if there is no method in class
        if (!methods.hasEquals() && !alreadyDone)
        {   
            DAddr addrEquals = compiler.getStack().newGlobal();
            compiler.addInstruction(new LOAD(this.name.getClassDefinition().getSuperClass().getOperandEquals(), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, addrEquals));
            this.name.getClassDefinition().setOperandEquals(name.getClassDefinition().getSuperClass().getOperandEquals());
        }
        
        // cf. p213 avec index
        for (int i = 0 ; i <= buildList().size() ; i++)
        // for(AbstractDeclMethods method : methods.getList())
        {   
            DeclMethods method = getRightMethodList(i);
            if (method != null)
            {
                insertIntoStack(compiler, (DeclMethods)method);
            }
        }
    }  

    /**
     * Get the method with the index i
     * @param i
     * @return
     */
    public DeclMethods getRightMethod(int i)
    {
        for (AbstractDeclMethods method : methods.getList())
        {
            DeclMethods meth = (DeclMethods) method;
            if (meth.getNameIdentifier().getMethodDefinition().getIndex() == i) return meth;
        }
        return null;
    }

    /**
     * Get the method with the index i
     * @param i
     * @return
     */
    public DeclMethods getRightMethodList(int i)
    {
        for (DeclMethods method : this.buildList())
        {
            if (method.getNameIdentifier().getMethodDefinition().getIndex() == i) return method;
        }
        return null;
    }

    public void insertIntoStack(DecacCompiler compiler, DeclMethods method)
    {   
        if (method.getNameIdentifier().getName().getName().equals("equals") && methods.hasEquals()) {
            DAddr addrEquals = compiler.getStack().newGlobal();
            LabelOperand lab = new LabelOperand(new Label("code." + name.getName().getName() + ".equals"));
            this.name.getClassDefinition().setOperandEquals(lab);
            method.setLabelAndOperand(compiler, this);

            compiler.addInstruction(new LOAD(new Label("code." + name.getName().getName() + ".equals"), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, addrEquals));
        } else {
            if (!method.getNameIdentifier().getName().getName().equals("equals")) {                
                if (method.getNameIdentifier().getMethodDefinition().hasLabel() && this.superclass != null) {
                    DeclMethods meth = method.clone();
    
                    // On met la super class
                    meth.setLabelAndOperand(compiler, this.name.getClassDefinition().getSuperClass().getClasse());
    
                    if (this.superclass == null)
                        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
                    else
                        compiler.addInstruction(new LOAD(method.getNameIdentifier().getMethodDefinition().getLabel(), Register.R0));
                    compiler.addInstruction(new STORE(Register.R0, method.getNameIdentifier().getMethodDefinition().getOperand()));
                    
                } else {
                    method.setLabelAndOperand(compiler, this);
    
                    if (this.superclass == null)
                        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
                    else
                        compiler.addInstruction(new LOAD(method.getNameIdentifier().getMethodDefinition().getLabel(), Register.R0));
                    compiler.addInstruction(new STORE(Register.R0, method.getNameIdentifier().getMethodDefinition().getOperand()));
                    
                }
            }
        }
    }

    public void codeGenInst(DecacCompiler compiler)
    {   
        name.getClassDefinition().setListDeclFields(fields);
        compiler.addComment("---------- Initialisation des champs de " + name.getName());
        LabelOperand labelOp = new LabelOperand(new Label("init." + name.getName()));
        compiler.addLabel(labelOp.getLabel());
        // Génération des champs de la class
        ListDeclFields fieldsSuper = null;
        if (this.name.getClassDefinition().getSuperClass().getType().getName().getName() == "Object"){
            fields.codeGenListDeclField(compiler, null);
        } 
        else 
        {
            fieldsSuper = name.getClassDefinition().getSuperClass().getListDeclFields();
            fields.codeGenListDeclField(compiler, fieldsSuper);
        }
        // Génération des méthodes de la classe
        methods.codeGenListDeclMethod(compiler, this);
    }

    public ArrayList<DeclMethods> buildList() {
        ArrayList<DeclMethods> resList = new ArrayList<DeclMethods>();

        // Methods of this
        ListDeclMethods currentMethods = this.methods;

        // Methods of the parent
        ListDeclMethods superMethods = name.getClassDefinition().getSuperClass().getListDeclMethods();

        if (superMethods == null ) {
            // Case equals : we add the method equals if it exists
            if (currentMethods.hasEquals()) {
                resList.add(currentMethods.getRightMethod(0));
            } else {
                // la méthode equals sera ajouter plus tard dans le programme
                // donc rien à faire ici
            }
            
            // Ensuite on met le reste des méthodes
            for (AbstractDeclMethods abstractMethod : currentMethods.getList()) {
                DeclMethods method = (DeclMethods)abstractMethod;
    
                // On regarde si la méthode actuelle est nouvelle
                // La méthode est nouvelle
                resList.add(method);
            }
        } else {
            // Case equals : we add the method equals if it exists
            if (currentMethods.hasEquals()) {
                resList.add(currentMethods.getRightMethod(0));
            } else if (superMethods.hasEquals()){
                resList.add(superMethods.getRightMethod(0));
            } else {
                // la méthode equals sera ajouter plus tard dans le programme
                // donc rien à faire ici
            }
    
            // Other methods
            // On parcourt les méthodes de super que l'on place dans la list résultat
            // si une méthode est redéfini alors on la sélection dans currentMethonds
            for (AbstractDeclMethods abstractMethod : superMethods.getList()) {
    
                DeclMethods superMethod = (DeclMethods)abstractMethod;
    
                String nameOfSuperMethod = superMethod.getNameIdentifier().getName().getName();
    
                // On regarde si la méthode actuelle a redéfini cette méthode
                if (currentMethods.containsMethod(nameOfSuperMethod)) {
                    // La méthode est redéfini alors on met celle-ci
                    resList.add(currentMethods.returnMethod(nameOfSuperMethod));
                } else {
                    // La méthode n'est pas redéfinie donc on met la méthode de super
                    resList.add(superMethod);
    
                }
    
            }
            
            // Ensuite on met le reste des méthodes
            for (AbstractDeclMethods abstractMethod : currentMethods.getList()) {
                DeclMethods method = (DeclMethods)abstractMethod;
    
                String nameOfMethod = method.getNameIdentifier().getName().getName();
    
                // On regarde si la méthode actuelle est nouvelle
                if (! superMethods.containsMethod(nameOfMethod)) {
                    // La méthode est nouvelle
                    resList.add(method);
    
                } else {
                    // La méthode est dans super donc elle n'est pas nouvelle
                }
            }
        }


        return resList;        
    }
}

