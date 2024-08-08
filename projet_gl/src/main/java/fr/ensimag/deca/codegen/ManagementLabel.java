package fr.ensimag.deca.codegen;

import java.util.HashMap;
import fr.ensimag.ima.pseudocode.Label;

public class ManagementLabel {

    private HashMap<Position, Label> listExpStart = new HashMap<Position, Label>();
    private HashMap<Position, Label> listExpEnd = new HashMap<Position, Label>();

    private HashMap<Position, Label> listIfThenElseStart = new HashMap<Position, Label>();
    private HashMap<Position, Label> listIfThenElseEnd = new HashMap<Position, Label>();

    private HashMap<Position, Label> listWhileStart = new HashMap<Position, Label>();
    private HashMap<Position, Label> listWhileEnd = new HashMap<Position, Label>();

    private int numberOfLabelBool = 0;

    /**
     * getter of number of boolean expression label
     * @return number of boolean expression label
     */
    public int getNumberOfLabelBool()
    {
        return numberOfLabelBool;
    }

    /**
     * adding of labels start and end for boolean expressions
     * @param line position on the  line
     */
    public void addLabelExprBool(int line)
    {   
        numberOfLabelBool++;
        Position pos = new Position(line, numberOfLabelBool);
        listExpStart.put(pos, new Label("Start_Label_ExprBool."+String.valueOf(line)+"."+String.valueOf(numberOfLabelBool)));
        listExpEnd.put(pos, new Label("End_Label_ExprBool."+String.valueOf(line)+"."+String.valueOf(numberOfLabelBool)));
    }




    /* POUR LES IF THEN ELSE */


    /**
     * New Start label for if Then Else
     * @param line position on the line 
     * @param column position on the column
     * @return list of if then ..  start label
     */
    public Label getLabelIfThenElseStart(int line, int column)
    {   
        Position pos = new Position(line, column);
        return listIfThenElseStart.get(pos);
    }   


    /**
     * New end label for if Then Else
     * @param line position on the line 
     * @param column position on the column
     * @return list of if then ..  end label
     */
    public Label getLabelIfThenElseEnd(int line, int column)
    {   
        Position pos = new Position(line, column);
        return listIfThenElseEnd.get(pos);
    }

    /**
     * adding of labels start and end for if then else expressions
     * @param line position on the  line
     * @param column position on the column
     */
    public void addLabelExprIfThenElse(int line, int column)
    {
        Position pos = new Position(line, column);
        listIfThenElseStart.put(pos, new Label("Start_Label_If_Then_Else."+String.valueOf(line)+"."+String.valueOf(column)));
        listIfThenElseEnd.put(pos, new Label("End_Label_If_Then_Else."+String.valueOf(line)+"."+String.valueOf(column)));
    }


    /* POUR LES WHILE */


    /**
     * New Start label for while 
     * @param line position on the line 
     * @param column position on the column
     * @return list of while  start label
     */
    public Label getLabelWhileStart(int line, int column)
    {   
        Position pos = new Position(line, column);
        return listWhileStart.get(pos);
    }   

    /**
     * New end label for while Else
     * @param line position on the line 
     * @param column position on the column
     * @return list of if then ..  end label
     */
    public Label getLabelWhileEnd(int line, int column)
    {   
        Position pos = new Position(line, column);
        return listWhileEnd.get(pos);
    }

    /**
     * adding of labels start and end for while  expressions
     * @param line position on the  line
     * @param column position on the column
     */
    public void addLabelExprWhile(int line, int column)
    {
        Position pos = new Position(line, column);
        listWhileStart.put(pos, new Label("Start_Label_While."+String.valueOf(line)+"."+String.valueOf(column)));
        listWhileEnd.put(pos, new Label("End_Label_While."+String.valueOf(line)+"."+String.valueOf(column)));
    }


    

    
}
