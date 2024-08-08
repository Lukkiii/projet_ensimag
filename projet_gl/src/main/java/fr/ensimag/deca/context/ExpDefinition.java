package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Definition associated to identifier in expressions.
 *
 * @author gl41
 * @date 01/01/2024
 */
public abstract class ExpDefinition extends Definition {

    public void setOperand(DAddr operand) {
        this.operand = operand;
    }

    private int operandOffset;

    public void setOperandOffset(int offset)
    {
        operandOffset = offset;
    }

    public DAddr getOperand() {
        return operand;
    }

    public int getOffsetOperand()
    {
        return this.operandOffset;
    }

    private DAddr operand;

    public ExpDefinition(Type type, Location location) {
        super(type, location);
    }

    private fr.ensimag.arm.pseudocode.Addr armOperand;
    public void setOperandARM(fr.ensimag.arm.pseudocode.Addr operand) {
        this.armOperand = operand;
    }

    public fr.ensimag.arm.pseudocode.Addr getOperandARM() { return this.armOperand; }
}
