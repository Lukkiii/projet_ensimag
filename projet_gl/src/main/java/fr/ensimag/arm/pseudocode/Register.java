package fr.ensimag.arm.pseudocode;
public class Register extends Val {

    int number;
    private Register(int number) {
        this.number = number;
    }
    @Override
    public String toString() {
        return "r" + number;
    }

    public static final Register R0 = new Register(0);
    public static final Register R1 = new Register(1);
    public static final Register R2 = new Register(2);
    public static final Register R3 = new Register(3);
    public static final Register R4 = new Register(4);
    public static final Register R5 = new Register(5);
    public static final Register R6 = new Register(6);
    public static final Register R7 = new Register(7);
    public static final Register R8 = new Register(8);
    public static final Register R9 = new Register(9);
    public static final Register R10 = new Register(10);
    public static final Register R11 = new Register(11);
    public static final Register R12 = new Register(12);
    public static final Register R13 = new Register(13);
    public static final Register R14 = new Register(14);
    public static final Register R15 = new Register(15);
    public static final Register FP = R11;
    public static final Register IP = R12;
    public static final Register SP = R13;
    public static final Register LR = R14;
    public static final Register PC = R15;
}
