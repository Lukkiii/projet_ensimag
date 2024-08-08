package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.codegen.arm.Function;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.BOV;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.Validate;


/**
 * Print statement (print, println, ...).
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractPrint extends AbstractInst {

    private ErrorMessage errorMessage = new ErrorMessage("Input/Output error", new Label("io_error"));

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

            for (AbstractExpr expr : this.arguments.getList()) {
                Type printType = expr.verifyExpr(compiler, localEnv, currentClass);
                if (!printType.isInt() && !printType.isFloat() && !printType.isString() && !printType.isBoolean()) {
                    throw new ContextualError("Invalid type to print : " + printType, expr.getLocation());
                }
            }      
    }

    @Override
    protected DVal codeGenInst(DecacCompiler compiler) {
        compiler.add(new Line(""));
        compiler.add(new Line(decompile()));

        if (!compiler.isSetErrorMessage(1))
            compiler.setErrorMessage(1, errorMessage);
        for (AbstractExpr a : getArguments().getList()) {
            DVal reg = a.codeGenPrint(compiler);
            a.choiceInstr(compiler, reg, getPrintHex());
        }
        compiler.addInstruction(new BOV(errorMessage.getLabel()));
        return null;
    }

    @Override
    protected Register codeGenInstARM(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            boolean spilled = !compiler.armAllocator.alloc(fr.ensimag.arm.pseudocode.Register.R0);
            if (spilled)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.PUSH(fr.ensimag.arm.pseudocode.Register.R0));

            a.codeGenExprARM(compiler, fr.ensimag.arm.pseudocode.Register.R0);

            if (a.getType().isBoolean() && this instanceof Print)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_bool));
            else if (a.getType().isBoolean() && this instanceof Println)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_bool_nl));
            else if (a.getType().isInt() && this instanceof Print) {
                if (!getPrintHex())
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_int));
                else
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_int_x));
            }
            else if (a.getType().isInt() && this instanceof Println) {
                if (!getPrintHex())
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_int_nl));
                else
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_int_nl_x));
            }
            else if (a.getType().isFloat() && this instanceof Print) {
                if (!getPrintHex())
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_float));
                else
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_float_x));
            }
            else if (a.getType().isFloat() && this instanceof Println){
                if (!getPrintHex())
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_float_nl));
                else
                    compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_float_nl_x));
            }
            else if (a.getType().isString() && this instanceof Print)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_str));
            else if (a.getType().isString() && this instanceof Println)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.write_str_nl));
            else
                throw new DecacInternalError("THIS SHOULD NOT HAPPEN!");

            compiler.armAllocator.deAlloc(fr.ensimag.arm.pseudocode.Register.R0);
            if (spilled) {
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.POP(fr.ensimag.arm.pseudocode.Register.R0));
                compiler.armAllocator.alloc(fr.ensimag.arm.pseudocode.Register.R0);
            }
        }
        return null;
    }

//    @Override
//    protected Register codeGenInstARM(DecacCompiler compiler) {
//        for (AbstractExpr a : getArguments().getList()) {
//            fr.ensimag.arm.pseudocode.Label func;
//
//            // SELECT THE CORRECT FUNCTION
//            if (a.getType().isBoolean() && this instanceof Print)
//                func = ARMProgram.LibDeca.write_bool;
//            else if (a.getType().isBoolean() && this instanceof Println)
//                func = ARMProgram.LibDeca.write_bool_nl;
//            else if (a.getType().isInt() && this instanceof Print) {
//                if (!getPrintHex())
//                    func = ARMProgram.LibDeca.write_int;
//                else
//                    func = ARMProgram.LibDeca.write_int_x;
//            }
//            else if (a.getType().isInt() && this instanceof Println) {
//                if (!getPrintHex())
//                    func = ARMProgram.LibDeca.write_int_nl;
//                else
//                    func = ARMProgram.LibDeca.write_int_nl_x;
//            }
//            else if (a.getType().isFloat() && this instanceof Print) {
//                if (!getPrintHex())
//                    func = ARMProgram.LibDeca.write_float;
//                else
//                    func = ARMProgram.LibDeca.write_float_x;
//            }
//            else if (a.getType().isFloat() && this instanceof Println){
//                if (!getPrintHex())
//                    func = ARMProgram.LibDeca.write_float_nl;
//                else
//                    func = ARMProgram.LibDeca.write_float_nl_x;
//            }
//            else if (a.getType().isString() && this instanceof Print)
//                func = ARMProgram.LibDeca.write_str;
//            else if (a.getType().isString() && this instanceof Println)
//                func = ARMProgram.LibDeca.write_str_nl;
//            else
//                throw new DecacInternalError("THIS SHOULD NOT HAPPEN!");
//
//            // CALL FUNC
//            Function.call(compiler, func, null, a);
//
//        }
//        return null;
//    }

    private boolean getPrintHex() {
        return printHex;
    }

    /**
     * @author heuzec
     * @date 10/01/2024
     */
    @Override
    public void decompile(IndentPrintStream s) {

        if (getPrintHex())
            s.print("print" + this.getSuffix() + "x(");              
        else 
            s.print("print" + this.getSuffix() + "(");

        if (this.getArguments().size() < 2) {
            this.getArguments().decompile(s);            
        } else {
            List<AbstractExpr> liste = this.getArguments().getList();
            liste.get(0).decompile(s);
            for (int i = 1; i < liste.size(); i++) {
                s.print(',');
                liste.get(i).decompile(s);
            }
        }

        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
