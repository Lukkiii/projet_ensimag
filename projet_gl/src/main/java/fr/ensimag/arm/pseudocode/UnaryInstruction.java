package fr.ensimag.arm.pseudocode;

import fr.ensimag.arm.pseudocode.Instruction;
import fr.ensimag.arm.pseudocode.Operand;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public abstract class UnaryInstruction extends Instruction {
    private Operand operand;

    public Operand getOperand() {
        return operand;
    }

    @Override
    protected void displayOperands(PrintStream s) {
        s.print(" ");
        s.print(operand);
    }

    protected UnaryInstruction(Operand op) {
        Validate.notNull(op);
        this.operand = op;
    }
}
