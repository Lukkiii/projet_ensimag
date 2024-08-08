package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;

public class CodeGenError extends LocationException {
    public CodeGenError(String message, Location location) {
        super(message, location);
    }
}