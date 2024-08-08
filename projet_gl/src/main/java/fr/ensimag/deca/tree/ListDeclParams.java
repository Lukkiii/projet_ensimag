package fr.ensimag.deca.tree;

import java.util.List;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclParams extends TreeList<AbstractDeclParams>{

    @Override
    public void decompile(IndentPrintStream s){
        List<AbstractDeclParams> liste = this.getList();

        if (liste.size() > 0) {
            liste.get(0).decompile(s);
            for (int i = 1; i < liste.size(); i++) {
                s.print(",");
                liste.get(i).decompile(s);
            }
            
        }
    }

    protected Signature verifyListDeclParamsMember(DecacCompiler compiler) throws ContextualError{
        Signature signature = new Signature();
        for (AbstractDeclParams p: getList()) {
            Type type = p.verifyDeclParamMember(compiler);
            signature.add(type);
        }
        return signature;
    }
    
    protected EnvironmentExp verifyListDeclParamBody(DecacCompiler compiler) throws ContextualError{

        EnvironmentExp envExp = new EnvironmentExp(null);

        for (AbstractDeclParams p: getList()) {
            p.verifyDeclParamBody(compiler, envExp);
        }
        return envExp;
    }
}