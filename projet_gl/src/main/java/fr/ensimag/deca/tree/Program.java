package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    private ErrorMessage errorMessageNull = new ErrorMessage("Object is null", new Label("null_dereferencing"));
    private ErrorMessage errorMessageOverflow = new ErrorMessage("Stack Overflow", new Label("stack_overflow_error"));
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;


    private void initObjectEqualsMethod(DecacCompiler compiler) {
        SymbolTable.Symbol equalsSymbol = compiler.createSymbol("equals");
        SymbolTable.Symbol objectSymbol = compiler.createSymbol("Object");
        SymbolTable.Symbol boolSymbol = compiler.createSymbol("boolean");
        Signature equalsSig = new Signature();
        Type objectType = compiler.getEnvType().getType(objectSymbol);
        Type booleanType = compiler.getEnvType().getType(boolSymbol);
        equalsSig.add(objectType);
        MethodDefinition equalsMethod = new MethodDefinition(booleanType, Location.BUILTIN, equalsSig, 1);
        try{
            ((ClassDefinition) compiler.getEnvType().defOfType(objectSymbol)).getMembers().declare(equalsSymbol, equalsMethod);
        } catch (DoubleDefException e) {
            //LOG.error("Equals method for Object is already defined.");
        }
        ((ClassDefinition) compiler.getEnvType().defOfType(objectSymbol)).incNumberOfMethods();
        AbstractIdentifier equalsIdentifier = new Identifier(equalsSymbol);
        equalsIdentifier.setDefinition(equalsMethod);
        equalsIdentifier.setType(booleanType);
    }

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        initObjectEqualsMethod(compiler);
        LOG.debug("verify program: start");
        // pass 1
        this.classes.verifyListClass(compiler);
        // pass 2
        this.classes.verifyListClassMembers(compiler);
        // pass 3
        this.classes.verifyListClassBody(compiler);
        this.main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        compiler.addInstruction(new TSTO(5));
        compiler.setErrorMessage(4, errorMessageOverflow);
        compiler.addInstruction(new BOV(errorMessageOverflow.getLabel()));
        
        // if (getMain().getListDeclVar() != null && getClasses().getList() != null) {
        //     compiler.addInstruction(new ADDSP(getClasses().getList().size() + getMain().getListDeclVar().size()));           
        // } else if (getClasses().getList() != null) {
        //     compiler.addInstruction(new ADDSP(getClasses().getList().size())); 
        // }  else if (getClasses().getList() != null) {
        //     compiler.addInstruction(new ADDSP(getMain().getListDeclVar().size()+2)); 
        // } else {
        //     compiler.addInstruction(new ADDSP(2)); 
        // }

        declObjet(compiler);

        getClasses().codeGenMethodTables(compiler);

        compiler.addComment("Positionnement Stack Pointer");
        compiler.addInstruction(new ADDSP(compiler.getStack().getGlobals()+1));


        compiler.addComment("------------------------------------------------------------");
        compiler.addComment("                     START MAIN PROGRAM                     ");
        compiler.addComment("------------------------------------------------------------");

        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        compiler.addComment("------------------------------------------------------------");
        compiler.addComment("                      END MAIN PROGRAM                      ");
        compiler.addComment("------------------------------------------------------------");

        // Affichier Object.Equals
        createEqualsObjet(compiler);
        // afficher classes
        getClasses().codeGenInst(compiler);

        // Afficher les Erreurs
        for (ErrorMessage error : compiler.getErrorMessageTab())
        {
            if (error != null)
                error.display(compiler);
        }

    }

    public void codeGenProgramARM(DecacCompiler compiler) {
        this.main.codeGenMainARM(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }


    public void declObjet(DecacCompiler compiler)
    {
        compiler.addComment("------------------------------------------------------------");
        compiler.addComment("            Construction des tables des methodes            ");
        compiler.addComment("------------------------------------------------------------");
        compiler.addComment("Construction de la table des methodes de Object");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, compiler.getStack().newGlobal()));
        DVal labelObjet = new LabelOperand(new Label("code.Object.equals"));
        compiler.addInstruction(new LOAD(labelObjet, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, compiler.getStack().newGlobal()));
    }


    public void createEqualsObjet(DecacCompiler compiler)
    {
        compiler.addComment("---------- Code de la methode equals dans la classe Object");
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessageNull);

        compiler.addLabel(new Label("code.Object.equals"));    
            
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
}
