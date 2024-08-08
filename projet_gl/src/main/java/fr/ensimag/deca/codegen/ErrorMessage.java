package fr.ensimag.deca.codegen;



import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/* displaying error messages in .ass files  */
public class ErrorMessage {
    
    private Label label;
    private String name;
    public ErrorMessage(String name, Label label)
    {
        this.label = label;
        this.name = name;
    }
    
    /**
     * getter of error label
     * @return label
     */
    public Label getLabel()
    {
        return this.label;
    }

    /**
     * display error management in files.ass
     * @param compiler
     */
    public void display(DecacCompiler compiler)
    {
        compiler.addComment("------------------------------------------------------------");
        String str = "Message d'erreur : "+ this.name;
        int offset = (60-str.length())/2;
        String space = "";

        for(int i=0;i<offset;i++) {
           space += " ";
        }

        compiler.addComment(space+"Message d'erreur : "+ this.name+space);
        compiler.addComment("------------------------------------------------------------");

        compiler.addLabel(label);
        compiler.addInstruction(new WSTR(new ImmediateString("Error : "+this.name)));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
