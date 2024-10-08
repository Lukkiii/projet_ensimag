package fr.ensimag.deca;

import fr.ensimag.arm.pseudocode.ARMProgram;
import fr.ensimag.arm.pseudocode.Block;
import fr.ensimag.deca.codegen.DummyRegisterAllocator;
import fr.ensimag.deca.codegen.ErrorMessage;
import fr.ensimag.deca.codegen.ManagementLabel;
import fr.ensimag.deca.codegen.Stack;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl41
 * @date 01/01/2024
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        this.environmentType = new EnvironmentType(this);
        this.initObjectEqualsMethod();
        if (compilerOptions != null) {
            registerAllocator.setNbRegister(compilerOptions.getRegisters());            
        }
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }


    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    
    /**
     * @see 
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }
    public String displayARMProgram() { return this.armProgram.display();}
    
    private final CompilerOptions compilerOptions;
    private final File source;

    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();

    public final ARMProgram armProgram = new ARMProgram();
    public final fr.ensimag.deca.codegen.arm.RegisterAllocator armAllocator = new fr.ensimag.deca.codegen.arm.RegisterAllocator();
 

    /** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    public final EnvironmentType environmentType;

    public Symbol createSymbol(String name) {
        //return null; // A FAIRE: remplacer par la ligne en commentaire ci-dessous
        return symbolTable.create(name);
    }


    /**
     * Returns the type corresponding to the key
     * @param key
     * @return Type of the key
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    public Type getType(String keyString) {
        Symbol key = createSymbol(keyString);

        return environmentType.getType(key);
    }

    /**
     * Returns the definition corresponding to the type
     * @param key
     * @return Definition of the type
     * 
     * @author heuzec
     * @date 20/12/2023
     */
    public Definition getDefinition(Type type) {
        return environmentType.getDefinition(type);
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.substring(0, sourceFile.length()-4) + "ass";

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * 
     * @return
     */
    public boolean compileARM() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.substring(0, sourceFile.length()-4) + "s";

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompileARM(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    private Stack stackManagement = new Stack();

    /**
     * Returns the stack for the codeGen Management
     * @param void
     * @return stackManagement
     * 
     * @author verneyd
     */
    public Stack getStack()
    {
        return stackManagement;
    }


    // LOCAL OR GLOBAL

    public void startLocal()
    {
        getStack().setVisibility(false);
    }

    public void endLocal()
    {
        getStack().setVisibility(true);
    }

    private DummyRegisterAllocator registerAllocator = new DummyRegisterAllocator();    

    /**
     * Returns the Registers allocator
     * @param void
     * @return DummyRegisterAllocator
     * 
     * @author verneyd
     */
    public DummyRegisterAllocator getRegAlloc()
    {
        return registerAllocator;
    }

    private EnvironmentExp envExp = new EnvironmentExp(null);

    public EnvironmentExp getEnvExp()
    {
        return envExp;
    }

    public EnvironmentType getEnvType() {
        return environmentType;
    }

    private void initObjectEqualsMethod() {
        SymbolTable.Symbol equalsSymbol = this.createSymbol("equals");
        SymbolTable.Symbol objectSymbol = this.createSymbol("Object");
        Signature equalsSig = new Signature();
        Type objectType = getEnvType().getType(objectSymbol);
        equalsSig.add(objectType);
        MethodDefinition equalsMethod = new MethodDefinition(objectType, Location.BUILTIN, equalsSig, 0);
        try{
            ((ClassDefinition) this.getEnvType().defOfType(objectSymbol)).getMembers().declare(equalsSymbol, equalsMethod);
        } catch (DoubleDefException e) {
            LOG.error("Equals method for Object is already defined.");
        }
        ((ClassDefinition) this.getEnvType().defOfType(objectSymbol)).incNumberOfMethods();
        AbstractIdentifier equalsIdentifier = new Identifier(equalsSymbol);
        equalsIdentifier.setDefinition(equalsMethod);
    }

    
    private ManagementLabel managementLabel = new ManagementLabel();

    public ManagementLabel getManagementLabel()
    {
        return managementLabel;
    }
    
    // index = 0: Heap Overflow Error
    // index = 1: IO error
    // index = 2: null dereferencing
    // index = 3: Overflow Error
    // index = 4: Stack Overflow Error
    private ErrorMessage[] errorTab = new ErrorMessage[10];
    
    public ErrorMessage[] getErrorMessageTab()    
    {
        return errorTab;
    }

    public void setErrorMessage(int i, ErrorMessage error)
    {   
        errorTab[i] = error;
    }

    public boolean isSetErrorMessage(int i)
    {
        return errorTab[i] != null;
    }



    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean decompile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.substring(0, sourceFile.length()-5) + "-p.deca";

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Decompiling file " + sourceFile + " to file " + destFile);
        try {
            return doDecompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        //assert(prog.checkAllLocations());


        prog.verifyProgram(this);
        //assert(prog.checkAllDecorations());

        addComment("start main program");
        prog.codeGenProgram(this);
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    private boolean doCompileARM(String sourceName, String destName,
                                 PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);
        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert(prog.checkAllLocations());
        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());
        prog.codeGenProgramARM(this);

        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");
        this.armProgram.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");

        return false;
    }


    /**
     * Internal function that does the job of decompiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    public boolean doDecompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);
        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        //assert(prog.checkAllLocations());

        prog.decompile(out);

        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

    ArrayList<Integer> tabClassDeep = new ArrayList<Integer>();

    public ArrayList<Integer> getClassDeep() {
        return this.tabClassDeep;
    }
}
