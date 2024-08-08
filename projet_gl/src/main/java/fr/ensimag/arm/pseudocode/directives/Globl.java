package fr.ensimag.arm.pseudocode.directives;

import fr.ensimag.arm.pseudocode.Directive;

import java.io.PrintStream;

public class Globl extends Directive {
    private String name;
    public Globl(String name) {
        this.name = name;
    }

    public void display(PrintStream s) {
        s.print(".globl " + this.name);
    }
}