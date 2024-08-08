package fr.ensimag.arm.pseudocode;

import java.io.PrintStream;

/**
 * IMA instruction.
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public abstract class Instruction {
    String getName() {
        return this.getClass().getSimpleName();
    }
    protected abstract void displayOperands(PrintStream s);
    void display(PrintStream s) {
        s.print(getName());
        displayOperands(s);
    }
}
