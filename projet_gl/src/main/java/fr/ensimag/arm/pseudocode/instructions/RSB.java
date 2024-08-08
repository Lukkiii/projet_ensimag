package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Operand;
import fr.ensimag.arm.pseudocode.Register;
import fr.ensimag.arm.pseudocode.TernaryInstruction;

import java.io.PrintStream;

public class RSB extends TernaryInstruction {
    public RSB(Register dst, Register operand1, Immediate operand2) {
        super(dst, operand1, operand2);
    }
    protected void displayOperands(PrintStream s) {
        Operand operand1 = this.getOperand1();
        Operand operand2 = this.getOperand2();
        Operand operand3 = this.getOperand3();
        s.print(" ");
        s.print(operand1);
        s.print(", ");
        s.print(operand2);
        s.print(", ");
        if (operand3 instanceof Immediate) {
            s.print("#");
            s.print(operand3);
        }
    }
}
