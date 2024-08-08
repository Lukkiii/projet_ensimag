package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;

/**
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class IntType extends Type {

    public IntType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isInt() {
        return true;
    }

    /**
     * Check the Type
     * @param otherType
     * @return
     * 
     * @author heuzec
     * @date 19/12/2023
     */
    @Override
    public boolean sameType(Type otherType) {
        return otherType.isInt();
    }


}
