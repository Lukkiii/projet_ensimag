package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.DeclClass;
import fr.ensimag.deca.tree.ListDeclFields;
import fr.ensimag.deca.tree.ListDeclMethods;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Operand;

import org.apache.commons.lang.Validate;

/**
 * Definition of a class.
 *
 * @author gl41
 * @date 01/01/2024
 */
public class ClassDefinition extends TypeDefinition {

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }
    
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    private final EnvironmentExp members;
    private final ClassDefinition superClass; 

    public EnvironmentExp getMembers() {
        return members;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }


    private Operand operand;

    public void setOperand(Operand op)
    {
        this.operand = op;
    }

    public Operand getOperand()
    {
        return operand;
    }

    private LabelOperand operandEquals;

    public void setOperandEquals(LabelOperand op)
    {
        this.operandEquals = op;
    }

    public LabelOperand getOperandEquals()
    {
        return operandEquals;
    }

    private ListDeclMethods listMethods;

    public void setListDeclMethods(ListDeclMethods c)
    {
        this.listMethods = c;
    }

    public ListDeclMethods getListDeclMethods()
    {
        return this.listMethods;
    }

    private ListDeclFields listFields;

    public void setListDeclFields(ListDeclFields c)
    {
        this.listFields = c;
    }

    public ListDeclFields getListDeclFields()
    {
        return this.listFields;
    }

    private DeclClass classe;

    public void setClasse(DeclClass c)
    {
        this.classe = c;
    }

    public DeclClass getClasse()
    {
        return this.classe;
    }

    int deep = 0;

    public int getIndexDeep() {
        return deep;
    }

    public void setDeep(int deepInt) {
        this.deep = deepInt;
    }
}
