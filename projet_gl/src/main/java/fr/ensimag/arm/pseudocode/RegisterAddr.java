package fr.ensimag.arm.pseudocode;

//Register that points to an address.
public class RegisterAddr extends Addr {
    Register register;
    public RegisterAddr(Register reg) {
        this.register = reg;
    }

    @Override
    public String toString() {
        return "[" + this.register + "]";
    }
}