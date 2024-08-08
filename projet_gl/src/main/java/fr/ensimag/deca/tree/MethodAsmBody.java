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
import fr.ensimag.ima.pseudocode.InlinePortion;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

public class MethodAsmBody extends AbstractMethodBody{

    private AbstractStringLiteral code;

    public MethodAsmBody(AbstractStringLiteral code){
        Validate.notNull(code);
        this.code = code;
    }

    /**
    * 
    * 
    * @author yuxuan
    * @date 15/01/2024
    */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("asm(");
        this.code.decompile(s);
        s.print(");");
    }
    
    @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams, ClassDefinition classDef, Type returnType) throws ContextualError {
        
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        code.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        code.iter(f);
    }

    @Override
    public void codeGenMethodBody(DecacCompiler compiler, DeclMethods method, DeclClass classe) {
        // On affiche simplement le code ASM
        compiler.add(new InlinePortion(code.getValue()));
        
        // On retourne l'expression évaluée
        compiler.addInstruction(new BRA(new Label("fin." + classe.getNameString() + "." + method.getNameIdentifier().getName().getName())));
        compiler.addInstruction(new WSTR(new ImmediateString("Erreur sortie de la méthode [METHODE A DEFINIR] sans return")));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
