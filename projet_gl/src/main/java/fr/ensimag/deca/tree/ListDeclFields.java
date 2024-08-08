package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.instructions.RTS;

/**
 * ListDeclFields
 *
 * @author yuxuan
 * @date 08/01/2024
 */
public class ListDeclFields extends TreeList<AbstractDeclFields> {

    @Override
    public void decompile(IndentPrintStream s){
        for (AbstractDeclFields f : getList()) {
            f.decompile(s);
        }
    }

    
    public void verifyListDeclFieldsMembers(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError {
        for (AbstractDeclFields d: getList()) {
            d.verifyDeclFieldMember(compiler, classDef);
        }
    }
    
    public void verifyClassBody(DecacCompiler compiler)
            throws ContextualError {
                throw new UnsupportedOperationException("not yet implemented");
            }

    public void verifyListDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef) throws ContextualError {
        for (AbstractDeclFields d: getList()) {
            d.verifyDeclFieldBody(compiler, envExp, classDef);
        }
    }

    /**
     * Codage des champs de la classe
     */
    public void codeGenListDeclField(DecacCompiler compiler, ListDeclFields fieldsSuper) {
        if (fieldsSuper != null ) {
            for (AbstractDeclFields field : fieldsSuper.getList()) {
                field.codeGenDeclField(compiler);            
            }
        }

        for (AbstractDeclFields d : getList()) {
            d.codeGenDeclField(compiler);
        }

        compiler.addInstruction(new RTS());
    }

    public boolean isRedefined(DeclFields field) {
        for (AbstractDeclFields f : this.getList()) {
            if (field == f) return true;            
        }
        return false;
    }
}
