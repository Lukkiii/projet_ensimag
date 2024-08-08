package fr.ensimag.arm.pseudocode.directives;

import fr.ensimag.arm.pseudocode.Directive;

import java.io.PrintStream;

public class Word extends Directive {
    private int value;
    public Word(int value) {
        this.value = value;
    }

    public void display(PrintStream s) {
        s.print(".word " + String.format("0x%08X", this.value));
    }
}
