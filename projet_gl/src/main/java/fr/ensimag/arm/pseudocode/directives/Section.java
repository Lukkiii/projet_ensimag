package fr.ensimag.arm.pseudocode.directives;

import fr.ensimag.arm.pseudocode.Directive;

import java.io.PrintStream;

public class Section extends Directive {
    private String name;
    public Section(String name) {
        this.name = name;
    }

    public void display(PrintStream s) {
        s.print(".section " + this.name);
    }
}