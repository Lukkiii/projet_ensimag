package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

public class MethodBody extends AbstractMethodBody{
    private ListDeclVar listVars;
    private ListInst listInsts;

    public MethodBody(ListDeclVar listVar, ListInst listInsts){
        Validate.notNull(listVar);
        Validate.notNull(listInsts);
        this.listVars = listVar;
        this.listInsts = listInsts;
    }
        
    /**
    * 
    * 
    * @author yuxuan
    * @date 15/01/2024
    */
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        this.listVars.decompile(s);
        this.listInsts.decompile(s);
        s.unindent();
        s.println("}");

    }
    
    @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams, ClassDefinition classDef, Type returnType) throws ContextualError {
        
        envExpParams.setEnvironmentParent(envExp);
        this.listVars.verifyListDeclVariable(compiler, envExpParams, classDef);
        this.listInsts.verifyListInst(compiler, envExpParams, classDef, returnType);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        listVars.prettyPrint(s, prefix, false);
        listInsts.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        listVars.iter(f);
        listInsts.iter(f);
    }

    /**
     * Generation oh method body
     */
    @Override
    public void codeGenMethodBody(DecacCompiler compiler, DeclMethods method, DeclClass classe) {
        // On est en local
        compiler.startLocal();
        // Variables
        this.listVars.codeGenListDeclVar(compiler);
        // Instructions
        this.listInsts.codeGenListInst(compiler);           
        // On repasse en global
        compiler.endLocal();   
        
        // On retourne l'expression évaluée
        compiler.addInstruction(new BRA(new Label("fin." + classe.getNameString() + "." + method.getNameIdentifier().getName().getName())));
        compiler.addInstruction(new WSTR(new ImmediateString("Erreur sortie de la méthode [METHODE A DEFINIR] sans return")));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
