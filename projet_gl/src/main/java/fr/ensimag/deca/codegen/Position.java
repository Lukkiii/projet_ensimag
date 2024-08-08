package fr.ensimag.deca.codegen;

import java.awt.Point;

// create 
public class Position extends Point{

    public Position(int line, int col)
    {
        super(col, line);
    }


    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Position)
        {
            Position pos = (Position) arg0;
            return this.getX() == pos.getX() && this.getY() == pos.getY();
        }
        return false;
    }
}
