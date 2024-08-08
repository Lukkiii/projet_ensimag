package fr.ensimag.deca.context;

import java.util.HashMap;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl41
 * @date 01/01/2024
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).

    private HashMap<Symbol, ExpDefinition> environment;

    private EnvironmentExp parentEnvironment;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;

        this.environment = new HashMap<>();
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the environment with an association of name and definition
     * @return environment
     * 
     * @author heuzec
     * @date 19/12/2023
     */
    public HashMap<Symbol, ExpDefinition> getEnvironment() {
        return environment;
    }

    /**
     * Return the environment parent
     * @return parentEnvironment
     * 
     * @author heuzec
     * @date 19/12/2023
     */
    public EnvironmentExp getEnvironmentParent() {
        return parentEnvironment;
    }

    public void setEnvironmentParent(EnvironmentExp parentEnvironment){
        this.parentEnvironment = parentEnvironment;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     * @return Definition
     * 
     * @author heuzec
     * @date 19/12/2023
     */
    public ExpDefinition get(Symbol key) {
        // throw new UnsupportedOperationException("not yet implemented");
        if (environment.containsKey(key)) {
            return environment.get(key);
        }
        else if (parentEnvironment != null) {
            return parentEnvironment.get(key);
        }
        else {
            return null;
        }
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if (environment.containsKey(name) == false)
            environment.put(name, def);
        else
            throw new DoubleDefException();
    }

    @Override
    public String toString() {
        String toReturn = new String();
        for (Symbol name: environment.keySet()) {
            String key = name.toString();
            String value = environment.get(name).toString();
            toReturn += (key + " " + value + "\n");
        }
        if (parentEnvironment != null) {
            toReturn += parentEnvironment.toString();
        }
        return toReturn;
    }
}
