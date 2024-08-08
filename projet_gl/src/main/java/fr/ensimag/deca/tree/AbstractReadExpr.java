package fr.ensimag.deca.tree;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;

/**
 * read...() statement.
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class AbstractReadExpr extends AbstractExpr {

    public AbstractReadExpr() {
        super();
    }

    public void codeGenExprARM(DecacCompiler compiler, Register reg) {
        if (reg == fr.ensimag.arm.pseudocode.Register.R0) {
            if (this instanceof ReadInt)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.read_int));
            else if (this instanceof ReadFloat)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.read_float));
            else
                throw new DecacInternalError("THIS SHOULD NEVER HAPPEN!");
        } else {
            boolean r0Used = compiler.armAllocator.used(fr.ensimag.arm.pseudocode.Register.R0);
            if (r0Used)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.PUSH(fr.ensimag.arm.pseudocode.Register.R0));

            if (this instanceof ReadInt)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.read_int));
            else if (this instanceof ReadFloat)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.BL(ARMProgram.LibDeca.read_float));
            else
                throw new DecacInternalError("THIS SHOULD NEVER HAPPEN!");

            compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.MOV(reg, fr.ensimag.arm.pseudocode.Register.R0));

            if (r0Used)
                compiler.armProgram.main.addInstruction(new fr.ensimag.arm.pseudocode.instructions.POP(fr.ensimag.arm.pseudocode.Register.R0));
        }
    }
}
