package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;


import java.util.LinkedList;
import java.util.List;
public class DummyRegisterAllocator implements RegisterAllocator {
    private int MAX_REGISTERS = 16;
    private GPRegister lastRegister = Register.getR(2);
    private List<DVal> inUseRegs = new LinkedList<DVal>();


    /**
     * adding an used register un used list of register
     */
    public DummyRegisterAllocator()
    {
        inUseRegs.add(Register.getR(2));
    }


    /**
     * 
     * @return return the new allocated register
     */
    public int allocate() {
        for (int i = 2; i < MAX_REGISTERS; i++) {
            if (!inUseRegs.contains(Register.getR(i))) {
                inUseRegs.add(Register.getR(i));
                lastRegister = GPRegister.getR(i);
                return i;
            }
        }
        throw new RuntimeException("We will see what to put there!");
    }

    /**
     * true if val is in list of used registers
     * @param val : register to test 
     * @return boolean if it contains or not
     */
    public boolean isUsed(DVal val)
    {
        return inUseRegs.contains(val);
    }

    /**
     * allocate a new register to compute expressions
     * @param val new register to use 
     */
    public void allocate(DVal val)
    {
        inUseRegs.add((GPRegister) val);
        lastRegister = (GPRegister) val;
    }

    /**
     * deallocate the last register used to compute expressions
     * @param val last register used 
     */
    public void deallocate(DVal val) {
        inUseRegs.remove((GPRegister)val);

        lastRegister = (GPRegister) inUseRegs.get(inUseRegs.size()-1);
    }

    /**
     * get the last register
     * @return the last used register
     */
    public GPRegister getLastRegister()
    {
        return lastRegister;
    }

    /**
     * 
     * @param nb number of register to set
     */
    public void setNbRegister(int nb) {
        this.MAX_REGISTERS = nb;
    }
}
