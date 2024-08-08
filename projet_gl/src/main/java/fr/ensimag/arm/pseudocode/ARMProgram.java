package fr.ensimag.arm.pseudocode;

import fr.ensimag.arm.pseudocode.directives.Asciz;
import fr.ensimag.arm.pseudocode.directives.Globl;
import fr.ensimag.arm.pseudocode.directives.Section;
import fr.ensimag.arm.pseudocode.instructions.BL;
import fr.ensimag.arm.pseudocode.instructions.MOV;
import fr.ensimag.arm.pseudocode.instructions.POP;
import fr.ensimag.arm.pseudocode.instructions.PUSH;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ARMProgram {
    public Block data = new Block();
    public Block methods = new Block();
    public Block main = new Block();

     public static class LibDeca {
        public static final Label write_bool = new Label("write_bool");
        public static final Label write_bool_nl = new Label("write_bool_nl");
        public static final Label write_int = new Label("write_int");
        public static final Label write_int_nl = new Label("write_int_nl");
        public static final Label write_int_x = new Label("write_int_x");
        public static final Label write_int_nl_x = new Label("write_int_nl_x");
        public static final Label write_float = new Label("write_float");
        public static final Label write_float_nl = new Label("write_float_nl");
        public static final Label write_float_x = new Label("write_float_x");
        public static final Label write_float_nl_x = new Label("write_float_nl_x");
        public static final Label write_str = new Label("write_str");
        public static final Label write_str_nl = new Label("write_str_nl");

        public static final Label read_int = new Label("read_int");

        public static final Label read_float = new Label("read_float");

        public static final Label leave  = new Label("leave");

        public static final Label conv_float = new Label("conv_float");

        public static final Label add_float = new Label("add_float");

    }

    private Map<String, Label> stringPool = new HashMap<String, Label>();
     private String encodeStringPool(String s) {
         return "str_" + Base64.getEncoder().encodeToString(s.getBytes()).replace('=', '_').replace('+', '$').replace('/', '.');
     }

     public Label addStringPool(String s) {
         Label label = new Label(encodeStringPool(s));
         this.stringPool.put(s, label);
         return label;
     }

    public void display(PrintStream s) {
        Block program = new Block();

        program.addComment("");
        program.addComment("=====================");
        program.addComment("== String literals ==");
        program.addComment("=====================");
        program.addComment("");
        program.addDirective(new Section(".rodata"));
        for (Map.Entry<String, Label> tuple : this.stringPool.entrySet()) {
            program.addLabel(tuple.getValue());
            program.addDirective(new Asciz(tuple.getKey()));
        }


        program.addComment("");
        program.addComment("======================");
        program.addComment("== Global variables ==");
        program.addComment("======================");
        program.addComment("");
        program.addDirective(new Section(".data"));
        program.append(data);

        program.addComment("");
        program.addComment("===================");
        program.addComment("== Class methods ==");
        program.addComment("===================");
        program.addComment("");
        program.addDirective(new Section(".text"));
        program.append(methods);

        program.addComment("");
        program.addComment("==========");
        program.addComment("== Main ==");
        program.addComment("==========");
        program.addComment("");
        program.addDirective(new Globl("main"));
        program.addLabel(new Label("main"));
        //program.addInstruction(new PUSH(Register.LR));
        program.append(main);
        //program.addInstruction(new POP(Register.LR));
        program.addInstruction(new MOV(Register.R0, new Immediate(0)));
        program.addInstruction(new BL(LibDeca.leave));

        program.display(s);
    }

    public String display() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream s = new PrintStream(out);
        display(s);
        return out.toString();
    }
}
