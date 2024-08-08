package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

public abstract class AbstractMethodBody extends Tree{

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the method body are OK.
     */
    protected abstract void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp envExpParams, ClassDefinition classDef, Type returnType)
            throws ContextualError;

    public abstract void codeGenMethodBody(DecacCompiler compiler, DeclMethods method, DeclClass classe);
}
