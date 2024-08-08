package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * methods parametre declaration
 *
 * @author yuxuan
 * @date 08/01/2024
 */

public abstract class AbstractDeclParams extends Tree{
    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the methods' params
     * are OK, without looking at the method body.
     */
    protected abstract Type verifyDeclParamMember(DecacCompiler compiler) throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the parameters are OK.
     */
    protected abstract void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp envExp) throws ContextualError;
}
