package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl41
 * @date 01/01/2024
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();

        defTypes = new HashMap<Type, Definition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.

        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        envTypes.put(nullSymb, new TypeDefinition(NULL, Location.BUILTIN));

        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);
        envTypes.put(objectSymb, new ClassDefinition(OBJECT, Location.BUILTIN, null));
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    private final Map<Type, Definition> defTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType OBJECT;
    public final NullType NULL;

    /**
     * Returns the type corresponding to the key
     * @param key
     * @return Type of the key
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    public Type getType(Symbol key) {
        if (envTypes.containsKey(key)) {
            return envTypes.get(key).getType();
        } else {
            return null;
        }
    }

    /**
     * Returns the definition corresponding to the type
     * @param type
     * @return Definition of the type
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    public Definition getDefinition(Type type) {
        if (defTypes.containsKey(type))
            return defTypes.get(type);
        else
            return null;
    }


    /**
     * Add a new type into EnvironmentType
     * @param symbol, typeDef
     * @return void
     * 
     * @author thory
     * @date 15/01/2024
     */
    public void declareClass(Symbol symbol, TypeDefinition typeDef) throws DoubleDefException {
        if (envTypes.containsKey(symbol)) {
            throw new DoubleDefException();
        }
        envTypes.put(symbol, typeDef);
    }

    /**
     * verify if type1 is subtype of type2
     * @param type1 
     * @param type2
     * @return boolean
     * 
     * @author yuxuan
     * @date 17/01/2024
     */
    public boolean subType(Type type1, Type type2){
        // T est un sous-type de T
        if(type1.sameType(type2))
            return true;

        //  Pour toute classe A, null est un sous-type de type_class(A)
        if((type1.isNull()) && (type2.isClass())){
            return true;
        }
        
        if(!(type1.isClass()) || !(type2.isClass()) || type2.isNull()){
            return false;
        }
        
        // Pour toute classe A, type_class(A) est un sous-type de type_class(Object)
        if(type2.getName().toString().equals("Object") && type1.isClass()){
            return true;
        }
        // Si une classe B étend une classe A dans l'environnement env, alors type_class(B) est un sous-type de type_class(A)
        // Si une classe C étend une classe B dans l'environnement env et si type_class(B) est un sous-type de T, alors type_class(C) est un sous-type de T.
        if(((ClassType)type1).isSubClassOf((ClassType)type2)){
            return true;
        }
        
        return false;
    }

    /**
     * verify assign_compatible
     * @param type1
     * @param type2
     * @return boolean
     * 
     * @author yuxuan
     * @date 17/01/2024
     */
    public boolean assign_compatible(Type type1, Type type2){
        // T1 est le type float et T2 est le type int
        // subtype(T2, T1)
        if((type1.isFloat() && type2.isInt())){
            return true;
        } else if(subType(type2, type1)){
            return true;
        }
        return false;
    }

    /**
     * verify cast_compatible
     * @param type1
     * @param type2
     * @return boolean
     * 
     * @author yuxuan
     * @date 17/01/2024
     */
    public boolean cast_compatible(Type type1, Type type2){
        if(!type1.isVoid()){
            // assign_compatible(T1, T2)
            // assign_compatible(T2, T1)
            if(assign_compatible(type1, type2) || assign_compatible(type2, type1)){
                return true;
            }
        }
        return false;
    }
       
    @Override
    public String toString() {
        String toReturn = new String();
        for (Symbol name: envTypes.keySet()) {
            String key = name.toString();
            String value = envTypes.get(name).toString();
            toReturn += (key + " " + value + "\n");
        }
        return toReturn;
    }
}
