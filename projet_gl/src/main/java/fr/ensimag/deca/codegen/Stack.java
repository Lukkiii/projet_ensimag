 package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;


public class Stack {


    private boolean isGlobal = true;

    private int globals = 0;
    private int locals = 0;

    /**
     * Global or Local
     */
    public RegisterOffset newGlobal() {
        if (isGlobal)
            return new RegisterOffset(++globals, Register.GB);
        else
            return new RegisterOffset(++locals, Register.LB);
    }

    public int getGlobals()
    {
        return globals;
    }

    public int getLocals()
    {
        return locals;
    }

    public void setVisibility(boolean visibility)
    {
        this.isGlobal = visibility;
        // lorsqu'on sort d'un bloc local
        if (visibility == true)
            this.locals = 0;
    }   

}

