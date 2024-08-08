package fr.ensimag.arm.pseudocode;

import fr.ensimag.arm.pseudocode.Instruction;
import fr.ensimag.arm.pseudocode.Operand;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public abstract class TernaryInstruction extends Instruction {
    private Operand operand1, operand2, operand3;

    public Operand getOperand1() {
        return operand1;
    }

    public Operand getOperand2() {
        return operand2;
    }
    public Operand getOperand3() {
        return operand3;
    }

    @Override
    protected void displayOperands(PrintStream s) {
        s.print(" ");
        s.print(operand1);
        s.print(", ");
        s.print(operand2);
        s.print(", ");
        s.print(operand3);
    }

    protected TernaryInstruction(Operand op1, Operand op2, Operand op3) {
        Validate.notNull(op1);
        Validate.notNull(op2);
        Validate.notNull(op3);
        this.operand1 = op1;
        this.operand2 = op2;
        this.operand3 = op3;
    }
}