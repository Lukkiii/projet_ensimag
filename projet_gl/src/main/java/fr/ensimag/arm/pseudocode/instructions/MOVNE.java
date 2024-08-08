package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.*;

import java.io.PrintStream;

public class MOVNE extends BinaryInstruction {
    
    public MOVNE(Register dst, Immediate operand) {
        super(dst, operand);
    }
    protected void displayOperands(PrintStream s) {
        Operand operand1 = this.getOperand1();
        Operand operand2 = this.getOperand2();
        s.print(" ");
        s.print(operand1);
        s.print(", ");
        if (operand2 instanceof Immediate) {
            s.print("#");
            s.print(operand2);
        }
    }
}
