package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Operand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * expr InstanceOf type
 *
 * @author yuxuan
 * @date 23/12/2023
 */
public class InstanceOf extends AbstractExpr{
    private AbstractExpr expr;
    private AbstractIdentifier type;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier type){
        Validate.notNull(expr);
        Validate.notNull(type);
        this.expr = expr;
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
                // right part
                Type rightType = this.type.verifyType(compiler);
                // left part
                Type exprType = this.expr.verifyExpr(compiler, localEnv, currentClass);

                if(!(rightType.isClassOrNull()) || !(exprType.isClass()))
                    throw new ContextualError("TypeError: Invalid 'instanceof' ", this.getLocation());
                    
                this.setType(compiler.environmentType.BOOLEAN);

                Type returnType = this.getType();
                return returnType;
                
    }

    private ErrorMessage errorMessage = new ErrorMessage("Object is null", new Label("null_dereferencing"));

    public DVal codeGenPrint(DecacCompiler compiler) {
        return codeGenInst(compiler);
    }

    public DVal codeGenInst(DecacCompiler compiler) {
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessage);  

        compiler.getManagementLabel().addLabelExprBool(getLocation().getLine());

        Label ifTrue = new Label("If_True_instanceof" + compiler.getManagementLabel().getNumberOfLabelBool());
        Label ifFalse = new Label("If_False_instanceof" + compiler.getManagementLabel().getNumberOfLabelBool());
            
        Identifier ident = (Identifier) expr;
        
        TypeDefinition classCourante = compiler.getEnvType().defOfType(compiler.createSymbol(ident.getName().getName()));
        TypeDefinition classType = compiler.getEnvType().defOfType(compiler.createSymbol(type.getName().getName()));

        // a instance of B
        // On récupère a
        DVal regDest = expr.codeGenInst(compiler);

        // On vérifie que a n'est pas nul
        instr(compiler, expr, regDest);

        // On récupère l'adresse du type de a
        DVal regExpr = new RegisterOffset(0, (Register)regDest);
        
        Operand opLeft = ident.getExpDefinition().getOperand();
        //ident.getExpDefinition().get;

        
        // On récupère l'adresse du type B
        Operand opRight = ((ClassDefinition)classType).getOperand();

        // for (int i = 0; i < ident.getExpDefinition().getIndexDeep() ; i++) {
        //     compiler.addInstruction(new LOAD(regExpr, Register.R1));
        //     compiler.addInstruction(new LOAD((DVal)opRight, Register.R0));
        //     // On compare les deux adresses
        //     compiler.addInstruction(new CMP(Register.R1, Register.R0));
        //     compiler.addInstruction(new BEQ(ifTrue));
            
        //     // On descend
        //     compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), (GPRegister)regExpr));
        // }

        // On retourne le résultat
        //False
        compiler.addInstruction(new LOAD(0, compiler.getRegAlloc().getLastRegister()));
        compiler.addInstruction(new BRA(ifFalse));
        // True
        compiler.addLabel(ifTrue);
        compiler.addInstruction(new LOAD(1, compiler.getRegAlloc().getLastRegister()));
        compiler.addLabel(ifFalse);

        return compiler.getRegAlloc().getLastRegister(); 
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        this.expr.decompile(s);
        s.print(" instanceof ");
        this.type.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr, DVal locDest) {
        compiler.addInstruction(new CMP(new NullOperand(), (GPRegister)locDest));
        compiler.addInstruction(new BEQ(errorMessage.getLabel()));
    }
}
