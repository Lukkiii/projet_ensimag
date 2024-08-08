package fr.ensimag.arm.pseudocode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public class Block {
    private final LinkedList<AbstractLine> lines = new LinkedList<AbstractLine>();

    public void add(AbstractLine line) {
        lines.add(line);
    }

    public void addComment(String s) {
        lines.add(new Line(s));
    }

    public void addLabel(Label l) {
        lines.add(new Line(l));
    }

    public void addInstruction(Instruction i) {
        lines.add(new Line(i));
    }

    public void addInstruction(Instruction i, String s) {
        lines.add(new Line(null, i, s));
    }

    public void addDirective(Directive directive) {
        lines.add(new Line(directive));
    }

    public void addDirective(Directive directive, String s) {
        lines.add(new Line(null, directive, s));
    }

    public void append(Block p) {
        lines.addAll(p.lines);
    }

    public void addFirst(Line l) {
        lines.addFirst(l);
    }

    public void display(PrintStream s) {
        for (AbstractLine l: lines) {
            l.display(s);
        }
    }

    public String display() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream s = new PrintStream(out);
        display(s);
        return out.toString();
    }

    public void addFirst(Instruction i) {
        addFirst(new Line(i));
    }

    public void addFirst(Instruction i, String comment) throws ARMInternalError {
        addFirst(new Line(null, i, comment));
    }
}
