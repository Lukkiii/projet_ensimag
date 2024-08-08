package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl41
 * @date 01/01/2024
 */
public class ListExpr extends TreeList<AbstractExpr> {

    /**
     * @author heuzec
     * @date 10/01/2024
     */
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractExpr inst : this.getList()) {
            inst.decompile(s);
        }
    }
}
