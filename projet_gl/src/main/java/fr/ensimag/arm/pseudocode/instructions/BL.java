package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.BinaryInstruction;
import fr.ensimag.arm.pseudocode.Label;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.UnaryInstruction;
import fr.ensimag.arm.pseudocode.Operand;

public class BL extends UnaryInstruction {
    
    public BL (Label label) {
        super(label);
    }
}
