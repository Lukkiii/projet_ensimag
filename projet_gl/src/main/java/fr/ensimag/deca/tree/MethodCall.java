package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.Validate;
/**
 * methodcall
 *
 * @author yuxuan
 * @date 23/12/2023
 */
public class MethodCall extends AbstractExpr{
    private AbstractExpr obj;
    private AbstractIdentifier method;
    private ListExpr params;

    public MethodCall(AbstractExpr obj, AbstractIdentifier method, ListExpr params){
        Validate.notNull(obj);
        Validate.notNull(method);
        Validate.notNull(params);
        this.obj = obj;
        this.method = method;
        this.params = params;
    }

    private ErrorMessage errorMessageNull = new ErrorMessage("Object is null", new Label("null_dereferencing"));

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
                Type objType = this.obj.verifyExpr(compiler, localEnv, currentClass);
                if (!objType.isClass()) {
                    throw new ContextualError("Type error : should be a class type", getLocation());
                }
                ClassDefinition class2 = (ClassDefinition) compiler.getEnvType().defOfType(objType.getName());
                EnvironmentExp envExpMethod = class2.getMembers();
                Type methodType = this.method.verifyExpr(compiler, envExpMethod, class2);
                Signature sig = this.method.getDefinition().asMethodDefinition("Type error : not a method", getLocation()).getSignature();

                setType(methodType);
                
                if(sig.size() != this.params.size())
                    throw new ContextualError("Error : Missing parameters", this.getLocation());

                if(sig.size() == 0)
                    return methodType;
                
                List<AbstractExpr> listParam = this.params.getList();
                for (int paramNum=0;paramNum<sig.size();paramNum++){
                    Type paramType = listParam.get(paramNum).verifyExpr(compiler, localEnv, currentClass);
                    if(!(compiler.getEnvType().assign_compatible(sig.paramNumber(paramNum), paramType))){
                        throw new ContextualError("Parameter type error", this.getLocation());
                    }
                }
                
                return methodType;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // this.method
        if (obj instanceof This){
            if (!obj.isImplicit()){
                this.obj.decompile(s);
                s.print(".");
            }
        } else {
            // obj.method
            this.obj.decompile(s);
            s.print(".");
        } 
        this.method.decompile(s);
        s.print("(");
        this.params.decompile(s);
        s.print(")");
        
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        obj.iter(f);
        method.iter(f);
        params.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }

    public DVal codeGenPrint(DecacCompiler compiler)
    {
        compiler.addComment("Appel de la méthode " + obj.getType().getName() + "." +  method.getName() + " ligne " + getLocation().getLine());
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessageNull);
        instr(compiler, method, null);
        return Register.R0;
    }

    public DVal codeGenInst(DecacCompiler compiler) {
        if (!(obj instanceof This)) {
            compiler.addComment("Appel de la méthode " + obj.getType().getName() + "." +  method.getName() + " ligne " + getLocation().getLine());
        }
            
        if (!compiler.isSetErrorMessage(2))
            compiler.setErrorMessage(2, errorMessageNull);
        instr(compiler, method, null);
        return Register.R0;
    }

    @Override
    protected fr.ensimag.arm.pseudocode.Register codeGenInstARM(DecacCompiler compiler) {
        return null;
    }

    @Override
    protected void instr(DecacCompiler compiler, AbstractExpr expr,DVal locDest) {
        if (obj instanceof Identifier) {
            Identifier object = (Identifier) obj; 
            compiler.addInstruction(new ADDSP(2 + params.size()));
            compiler.addInstruction(new LOAD(object.getVariableDefinition().getOperand(), Register.getR(2)));
            
            // On met dans la pile
            compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(0, Register.SP)));
            
            // On met la valeur des paramètre dans la pile
            int posStack = - 1;
            for (AbstractExpr param : params.getList()) {
                DVal source = param.codeGenInst(compiler);
                compiler.addInstruction(new STORE((Register)source, new RegisterOffset(posStack, Register.SP)));
                posStack--;
            }
            
            // Récupération 
            compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(2)));

            // Null ou non
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
            compiler.addInstruction(new BEQ(errorMessageNull.getLabel()));

            // Appel de la méthode
            compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));

            // On saute à la méthode
            compiler.addInstruction(new BSR((DVal)new RegisterOffset(method.getMethodDefinition().getIndex()+1, Register.getR(2))));
            //compiler.addInstruction(new BSR(new Label("code.A.setX")));
            // compiler.addInstruction(new BSR(method.getMethodDefinition().getOperand()));
            compiler.addInstruction(new SUBSP(2 + params.size()));            
        }
    }
}
