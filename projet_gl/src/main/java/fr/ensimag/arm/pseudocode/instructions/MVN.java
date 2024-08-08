package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.BinaryInstruction;
import fr.ensimag.arm.pseudocode.Register;

public class MVN extends BinaryInstruction {
    /**
     * 1111 = MVN - Rd:= NOT Op2
     * @param dst
     * @param operand
     */
    public MVN(Register dst, Register operand) {
        super(dst, operand);
    }
}
