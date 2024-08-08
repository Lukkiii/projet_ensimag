package fr.ensimag.deca.codegen.arm;

import fr.ensimag.arm.pseudocode.Register;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterAllocator {
    Map<Register, Boolean> usedRegs = new LinkedHashMap<Register, Boolean>() {{
        put(Register.R0, false);
        put(Register.R1, false);
        put(Register.R2, false);
        put(Register.R3, false);
        put(Register.R4, false);
        put(Register.R5, false);
        put(Register.R6, false);
        put(Register.R7, false);
        put(Register.R8, false);
        put(Register.R9, false);
        put(Register.R10, false);
        put(Register.R11, true);
        put(Register.R12, true);
        put(Register.R13, true);
        put(Register.R14, true);
        put(Register.R15, true);
    }};;

    public boolean alloc(Register r) {
        if (this.usedRegs.get(r))
            return false;
        this.usedRegs.put(r, true);
        return true;
    }
    public Register alloc() {
        for (Map.Entry<Register, Boolean> tuple : this.usedRegs.entrySet()) {
            if (this.alloc(tuple.getKey())) {
                return tuple.getKey();
            }
        }
        return null;
    }

    public void deAlloc(Register r) {
        this.usedRegs.put(r, false);
    }

    public boolean used(Register r) {
        return this.usedRegs.get(r);
    }
}
