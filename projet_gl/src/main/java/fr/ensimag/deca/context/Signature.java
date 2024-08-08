package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl41
 * @date 01/01/2024
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Signature)) {
            return false;
        }
        if (((Signature) o).args.size() != this.args.size()) {
            return false;
        }
        return ((Signature) o).args.equals(this.args);
    }

    @Override
    public String toString() {
        return args.toString();
    }

}
