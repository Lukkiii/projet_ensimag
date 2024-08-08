package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * methods declaration
 *
 * @author yuxuan
 * @date 08/01/2024
 */

public abstract class AbstractDeclMethods extends Tree{
    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the methods
     * are OK, without looking at the method body.
     */
    protected abstract void verifyDeclMethodMember(DecacCompiler compiler, ClassDefinition classDef)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the method are OK.
     */
    protected abstract void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef)
            throws ContextualError;


	public abstract void codeGenDeclMethod(DecacCompiler compiler, DeclClass classe);

	public abstract void setLabelAndOperand(DecacCompiler compiler, DeclClass classe);

}
