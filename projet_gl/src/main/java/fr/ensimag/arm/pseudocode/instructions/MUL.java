package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.TernaryInstruction;

public class MUL extends TernaryInstruction {
    public MUL(Register dst, Register operand1, Register operand2) {
        super(dst, operand1, operand2);
    }
}
