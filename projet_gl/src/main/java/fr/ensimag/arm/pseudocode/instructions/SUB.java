package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.Operand;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.TernaryInstruction;

public class SUB extends TernaryInstruction {
    public SUB(Register dst, Register operand1, Operand operand2) {
        super(dst, operand1, operand2);
    }
}
