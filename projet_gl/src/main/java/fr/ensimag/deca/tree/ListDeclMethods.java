package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclMethods extends TreeList<AbstractDeclMethods>{

    @Override
    public void decompile(IndentPrintStream s){
        for (AbstractDeclMethods m : getList()) {
            m.decompile(s);
            s.println();
        }
    }
    
    protected void verifyListDeclMethodsMember(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError{
        for (AbstractDeclMethods d: getList()) {
            d.verifyDeclMethodMember(compiler, classDef);
        }
    }
    
    protected void verifyClassBody(DecacCompiler compiler)
            throws ContextualError{
                throw new UnsupportedOperationException("not yet implemented");
            }

    protected void verifyListDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef) throws ContextualError{
        for (AbstractDeclMethods d: getList()) {
            d.verifyDeclMethodBody(compiler, envExp, classDef);
        }
    }

    public void codeGenListDeclMethod(DecacCompiler compiler, DeclClass classe) {
        for (AbstractDeclMethods d : getList()) {
            d.codeGenDeclMethod(compiler, classe);
        }
    }
    
    /**
     * Get the method with the index i
     * @param i
     * @return
     */
    public DeclMethods getRightMethod(int i)
    {
        for (AbstractDeclMethods method : this.getList())
        {
            DeclMethods meth = (DeclMethods) method;
            if (meth.getNameIdentifier().getMethodDefinition().getIndex() == i) return meth;
        }
        return null;
    }

    /**
     * Test if the method equals is among the method in ListDeclMethod
     * @return boolean
     */
    public boolean hasEquals() {
        for (AbstractDeclMethods method : this.getList()) {
            DeclMethods meth = (DeclMethods) method;
            if (meth.getNameIdentifier().getName().getName().equals("equals")) return true;
        }
        return false;
    }

    /**
     * Test if the method is among the method in ListDeclMethod
     * @return boolean
     */
    public boolean containsMethod(String nameMethodString) {
        for (AbstractDeclMethods method : this.getList()) {
            DeclMethods meth = (DeclMethods) method;
            if (meth.getNameIdentifier().getName().getName().equals(nameMethodString)) return true;
        }
        return false;
    }

    public DeclMethods returnMethod(String nameMethodString) {
        for (AbstractDeclMethods method : this.getList()) {
            DeclMethods meth = (DeclMethods) method;
            if (meth.getNameIdentifier().getName().getName().equals(nameMethodString)) return meth;
        }
        return null;
    }

}
