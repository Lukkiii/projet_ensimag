package fr.ensimag.arm.pseudocode.directives;

import fr.ensimag.arm.pseudocode.Directive;

import java.io.PrintStream;

public class Asciz extends Directive {
    private String name;
    public Asciz(String name) {
        this.name = name;
    }

    public void display(PrintStream s) {
        s.print(".asciz \"" + this.name + "\"");
    }
}