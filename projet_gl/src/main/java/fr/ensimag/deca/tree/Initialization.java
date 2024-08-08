package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.instructions.LDR;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl41
 * @date 01/01/2024
 */
public class Initialization extends AbstractInitialization {

    // @Override
    // public AbstractExpr getExpression() {
    //     return expression;
    // }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public AbstractExpr getExpression() { return this.expression; }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    /**
     * Implements non-terminal "initialization" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param t corresponds to the "type" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     * @date 9/01/2024
     * @author heuzec
     */
    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        setExpression(this.expression.verifyRValue(compiler, localEnv, currentClass, t));
    }


    @Override
    public void decompile(IndentPrintStream s) {
        expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenInit(DecacCompiler compiler, DAddr dAddr) {
        DVal regDest = expression.codeGenInst(compiler);

        compiler.addInstruction(new STORE((Register)regDest, dAddr));
    }

    @Override
    public void codeGenInitARM(DecacCompiler compiler, fr.ensimag.arm.pseudocode.Register reg) {
        this.expression.codeGenExprARM(compiler, reg);
    }

    @Override
    public DVal codeGenInitField(DecacCompiler compiler, DVal dval){
        return expression.codeGenInst(compiler);
    }

}
