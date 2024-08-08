package fr.ensimag.arm.pseudocode.instructions;

import fr.ensimag.arm.pseudocode.BinaryInstruction;
import fr.ensimag.arm.pseudocode.Immediate;
import fr.ensimag.arm.pseudocode.Operand;
import fr.ensimag.arm.pseudocode.Register;

import java.io.PrintStream;

public class MOV extends BinaryInstruction {
    /**
     * 1101 = MOV - Rd:= Op2
     * @param dst
     * @param operand
     */
    public MOV(Register reg, Immediate imm) {
        super(reg, imm);
    }

    public MOV(Register reg1, Register reg2) {
        super(reg1, reg2);
    }

    protected void displayOperands(PrintStream s) {
        Operand operand1 = this.getOperand1();
        Operand operand2 = this.getOperand2();
        s.print(" ");
        s.print(operand1);
        s.print(", ");
        if (operand2 instanceof Immediate)
            s.print("#");
        s.print(operand2);
    }

}
