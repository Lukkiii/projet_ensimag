package fr.ensimag.deca.codegen.arm;

import fr.ensimag.arm.pseudocode.Label;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.instructions.BL;
import fr.ensimag.arm.pseudocode.instructions.MOV;
import fr.ensimag.arm.pseudocode.instructions.POP;
import fr.ensimag.arm.pseudocode.instructions.PUSH;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;

public class Function {

    public static void call(DecacCompiler compiler, Label func, Register dst) {
        compiler.armProgram.main.addInstruction(new BL(func));
        if (dst != null && dst != Register.R0)
            compiler.armProgram.main.addInstruction(new MOV(dst, Register.R0));
    }
    public static void call(DecacCompiler compiler, Label func, Register dst, AbstractExpr expr) {
        if (dst == Register.R0) {
            expr.codeGenExprARM(compiler, dst);
            call(compiler, func, dst);
        } else {
            boolean spilled = !compiler.armAllocator.alloc(Register.R0);
            if (spilled)
                compiler.armProgram.main.addInstruction(new PUSH(Register.R0));

            expr.codeGenExprARM(compiler, Register.R0);
            call(compiler, func, dst);

            if (spilled)
                compiler.armProgram.main.addInstruction(new POP(Register.R0));
            else
                compiler.armAllocator.deAlloc(Register.R0);
        }
    }

    public static void call(DecacCompiler compiler, Label func, Register dst, Register op) {
        if (dst == Register.R0) {
            if (op != Register.R0)
                compiler.armProgram.main.addInstruction(new MOV(Register.R0, op));
            call(compiler, func, dst);
        } else {
            boolean spilled = !compiler.armAllocator.alloc(Register.R0);
            if (spilled)
                compiler.armProgram.main.addInstruction(new PUSH(Register.R0));

            if (op != Register.R0)
                compiler.armProgram.main.addInstruction(new MOV(Register.R0, op));
            call(compiler, func, dst);

            if (spilled)
                compiler.armProgram.main.addInstruction(new POP(Register.R0));
            else
                compiler.armAllocator.deAlloc(Register.R0);
        }
    }

    public static void call(DecacCompiler compiler, Label func, Register dst, AbstractExpr op1, AbstractExpr op2) {
        boolean spilled = !compiler.armAllocator.alloc(Register.R1);
        if (spilled)
            compiler.armProgram.main.addInstruction(new PUSH(Register.R1));

        op2.codeGenExprARM(compiler, Register.R1);
        call(compiler, func, dst, op1);

        if (spilled)
            compiler.armProgram.main.addInstruction(new POP(fr.ensimag.arm.pseudocode.Register.R1));
        else
            compiler.armAllocator.deAlloc(Register.R1);
    }

    public static void call(DecacCompiler compiler, Label func, Register dst, Register op1, Register op2) {
        boolean spilled = !compiler.armAllocator.alloc(Register.R1);
        if (spilled)
            compiler.armProgram.main.addInstruction(new PUSH(Register.R1));

        if (op2 != Register.R1) {
            if (op1 == Register.R1) {
                if (op2 != Register.R0)
                    compiler.armProgram.main.addInstruction(new MOV(Register.R0, op2));
                }
            else
                compiler.armProgram.main.addInstruction(new MOV(Register.R1, op2));
        }
        call(compiler, func, dst, op1);

        if (spilled)
            compiler.armProgram.main.addInstruction(new POP(fr.ensimag.arm.pseudocode.Register.R1));
        else
            compiler.armAllocator.deAlloc(Register.R1);
    }
}
