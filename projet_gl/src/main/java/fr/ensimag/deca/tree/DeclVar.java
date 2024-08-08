package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;


import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Line;

import org.apache.commons.lang.Validate;

/**
 * @author gl41
 * @date 01/01/2024
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type varType = this.type.verifyType(compiler);
        
        if (varType.isVoid()) {
            throw new ContextualError("Variable type should not be null.", this.getLocation());
        }
        
        this.type.setDefinition(new TypeDefinition(varType, getLocation()));

        this.initialization.verifyInitialization(compiler, varType, localEnv, currentClass); // TO CHANGE FOR CLASS
        
        try {
            VariableDefinition varDef = new VariableDefinition(varType, this.getLocation());
            localEnv.declare(this.varName.getName(), varDef);
            this.varName.setDefinition(varDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Variable is redeclared.", getLocation());
        }

        // Definition
    }

    /**
     * @author heuzec
     * @date 10/01/2024
     */
    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.varName.decompile(s);
        if (this.initialization instanceof Initialization)
        {
            s.print(" = ");
            this.initialization.decompile(s);
        }
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclVar(DecacCompiler compiler)
    {
        compiler.add(new Line(""));
        //compiler.add(new Line(decompile()));

        DAddr dAddr =  compiler.getStack().newGlobal();
        if (type.getType().isClass()) {
            this.initialization.codeGenInit(compiler, dAddr);
        } else if (!(initialization instanceof NoInitialization)){
            this.initialization.codeGenInit(compiler, dAddr);
        }
        varName.getVariableDefinition().setOperand(dAddr);
        //compiler.getEnvExp().get(this.varName.getName()).setOperand(dAddr);
    }


    private Integer fastInitARM() {
        if (this.initialization instanceof Initialization) {
            AbstractExpr expr = ((Initialization) this.initialization).getExpression();
            if (expr instanceof IntLiteral)
                return ((IntLiteral)expr).getValue();
            else if (expr instanceof FloatLiteral)
                return Float.floatToIntBits(((FloatLiteral)expr).getValue());
            else if (expr instanceof BooleanLiteral)
               return ((BooleanLiteral)expr).getValue() ? 0xCAFEBABE : 0x00;
        }
        return null;
    }

    @Override
    public void codeGenDeclVarARM(DecacCompiler compiler) {
        fr.ensimag.arm.pseudocode.Label label = new fr.ensimag.arm.pseudocode.Label("var_" + this.varName.getName().getName());
        compiler.armProgram.data.addLabel(label);

        Integer initializationValue = fastInitARM();
        if (initializationValue != null) {
            compiler.armProgram.data.addDirective(new fr.ensimag.arm.pseudocode.directives.Word(initializationValue));
        } else {
            compiler.armProgram.data.addDirective(new fr.ensimag.arm.pseudocode.directives.Word(0));

            if (this.initialization instanceof Initialization) {
                fr.ensimag.arm.pseudocode.Register regSrc = compiler.armAllocator.alloc();
                this.initialization.codeGenInitARM(compiler, regSrc);

                fr.ensimag.arm.pseudocode.Register regDst = compiler.armAllocator.alloc();
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.LDR(regDst, label));
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.STR(regSrc, new fr.ensimag.arm.pseudocode.RegisterAddr(regDst)));

                compiler.armAllocator.deAlloc(regDst);
                compiler.armAllocator.deAlloc(regSrc);
            }

        }

        this.varName.getVariableDefinition().setOperandARM(label);
    }
}
