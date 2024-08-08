package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.*;

import java.io.PrintStream;

// This is a pseudo instruction as described in https://developer.arm.com/documentation/dui0041/c/Babbfdih
public class LDR extends BinaryInstruction {
    public LDR(Register reg, Immediate imm) {
        super(reg, imm);
    }

    public LDR(Register reg, Label lab) {
        super(reg, lab);
    }

    public LDR(Register dst, Register src) {
        super(dst, src);
    }


    protected void displayOperands(PrintStream s) {
        Operand operand1 = this.getOperand1();
        Operand operand2 = this.getOperand2();
        s.print(" ");
        s.print(operand1);
        s.print(", ");
        if (operand2 instanceof Immediate || operand2 instanceof Label) {
            s.print("=");
            s.print(operand2);
        }
        else if (operand2 instanceof Register) {
            s.print('[');
            s.print(operand2);
            s.print(']');
        }
    }
}
