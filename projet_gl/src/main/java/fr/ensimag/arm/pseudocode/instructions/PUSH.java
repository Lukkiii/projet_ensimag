package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.Label;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.UnaryInstruction;

import java.io.PrintStream;

public class PUSH extends UnaryInstruction {

    public PUSH (Register operand) {
        super(operand);
    }

    @Override
    protected void displayOperands(PrintStream s) {
        s.print(" {");
        s.print(getOperand());
        s.print("}");
    }
}
