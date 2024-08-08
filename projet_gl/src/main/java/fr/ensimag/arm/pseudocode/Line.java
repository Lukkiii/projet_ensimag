package fr.ensimag.arm.pseudocode;

import java.io.PrintStream;

/**
 * Line of code in an IMA program.
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class Line extends AbstractLine {
    public Line(Label label, Instruction instruction, String comment) {
        super();
        checkComment(comment);
        this.label = label;
        this.instruction = instruction;
        this.comment = comment;
    }

    public Line(Label label, Directive directive, String comment) {
        super();
        checkComment(comment);
        this.label = label;
        this.directive = directive;
        this.comment = comment;
    }

    public Line(Instruction instruction) {
        super();
        this.instruction = instruction;
    }

    public Line(Directive directive) {
        super();
        this.directive = directive;
    }
    public Line(String comment) {
        super();
        checkComment(comment);
        this.comment = comment;
    }

    public Line(Label label) {
        super();
        this.label = label;
    }

    private void checkComment(final String s) throws ARMInternalError {
        if (s == null) {
            return;
        }
        if (s.contains("\n")) {
            throw new ARMInternalError("Comment '" + s + "'contains newline character");
        }
        if (s.contains("\r")) {
            throw new ARMInternalError("Comment '" + s + "'contains carriage return character");
        }
    }
    private Instruction instruction;
    private String comment;
    private Label label;

    private Directive directive;

    @Override
    void display(PrintStream s) {
        boolean tab = false;
        if (label != null) {
            s.print(label);
                        s.print(":");
            tab = true;
        }
        if (instruction != null) {
            s.print("\t");
            instruction.display(s);
            tab = true;
        }
        if (directive != null) {
            directive.display(s);
        }
        if (comment != null) {
            if (tab) {
                            s.print("\t");
                        }
            s.print("@ " + comment);
        }
        s.println();
    }

    public void setInstruction(Instruction instruction) {
        if (this.directive != null)
            throw new ARMInternalError("Instruction and Directive are incompatible!");
        this.instruction = instruction;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setDirective(Directive directive) {
        if (this.instruction != null)
            throw new ARMInternalError("Instruction and Directive are incompatible!");
    }

    public Directive getDirective() {
        return this.directive;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Label getLabel() {
        return label;
    }
}
