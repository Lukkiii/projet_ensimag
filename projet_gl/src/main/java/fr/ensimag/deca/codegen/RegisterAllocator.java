package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.DVal;

public interface RegisterAllocator {
    int allocate();
    void deallocate(DVal val);
}
