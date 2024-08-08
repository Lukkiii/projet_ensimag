package fr.ensimag.ima.pseudocode;

import org.apache.commons.lang.Validate;

/**
 * Representation of a label in IMA code. The same structure is used for label
 * declaration (e.g. foo: instruction) or use (e.g. BRA foo).
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class Label extends Operand {

    private boolean hasChanged = false;
    @Override
    public String toString() {
        return name;
    }

    public Label(String name) {
        super();
        Validate.isTrue(name.length() <= 1024, "Label name too long, not supported by IMA");
        Validate.isTrue(name.matches("^[a-zA-Z][a-zA-Z0-9_.]*$"), "Invalid label name " + name);
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
