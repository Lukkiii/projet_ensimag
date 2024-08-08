package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.*;

import java.io.PrintStream;

public class STR extends BinaryInstruction {

    public STR(Register reg, RegisterAddr addr) {
        super(reg, addr);
    }
}
