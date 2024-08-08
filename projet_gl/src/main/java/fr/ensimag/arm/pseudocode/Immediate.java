package fr.ensimag.arm.pseudocode;

public class Immediate extends Operand {
    private int literal;
    public Immediate(int value) {
        this.literal = value;
    }

    public Immediate(float value) {
        this.literal = Float.floatToIntBits(value);
    }

    public Immediate(boolean value){
        if (value)
            this.literal = 0xCAFEBABE;
        else
            this.literal = 0x00;
    }

    @Override
    public String toString() {
        return String.format("0x%08X", this.literal);
    }
}
