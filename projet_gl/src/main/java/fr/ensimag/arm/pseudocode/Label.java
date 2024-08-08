package fr.ensimag.arm.pseudocode;

import fr.ensimag.ima.pseudocode.Operand;
import org.apache.commons.lang.Validate;

public class Label extends Addr {

    private boolean hasChanged = false;
    @Override
    public String toString() {
        return name;
    }

    public Label(String name) {
        super();
        Validate.isTrue(name.matches("^[a-zA-Z0-9_\\.$][a-zA-Z0-9_$]*$"), "Invalid label name " + name);
        this.name = name;
    }

    public void setName(String name)
    {
        if (!hasChanged)
        {
            this.name = name;
            hasChanged = true;
        }
    }
    private String name;
}
