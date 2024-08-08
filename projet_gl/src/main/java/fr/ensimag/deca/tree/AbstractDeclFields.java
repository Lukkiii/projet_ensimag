package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * fields declaration
 *
 * @author yuxuan
 * @date 08/01/2024
 */

public abstract class AbstractDeclFields extends Tree {

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the fields
     * are OK, without looking at the field initialization.
     */
    protected abstract void verifyDeclFieldMember(DecacCompiler compiler, ClassDefinition classDef)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the fields are OK.
     */
    protected abstract void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition classDef)
            throws ContextualError;

	public abstract void codeGenDeclField(DecacCompiler compiler);
}
