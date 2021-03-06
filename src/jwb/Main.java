import AST.ProgramNode;
import Backend.*;
import Frontend.*;
import IR.IRRoot;
import Parser.MLexer;
import Parser.MParser;
import Parser.SyntaxErrorListener;
import ScopeEntity.Scope;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


import java.io.*;

public class Main
{
    private static ProgramNode ast;
    private static Scope globalScope;
    private static IRRoot irRoot;

    public static void main(String[] args) throws Exception
    {
        try {
            compile();
        }
        catch (Error e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void compile() throws Exception
    {
        buildAST();
        //printAST();
        semanticCheck();
        buildIR();
        //printIR();
        generateCode();
    }

    private static void buildAST() throws Exception
    {
        InputStream inS = System.in;
        CharStream input = CharStreams.fromStream(inS);
        MLexer lexer = new MLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MParser parser = new MParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new SyntaxErrorListener());
        ParseTree tree = parser.program();
        ASTBuilder astBuilder = new ASTBuilder();
        ast = (ProgramNode) astBuilder.visit(tree);
    }

    private static void printAST()
    {
        new ASTPrinter().visit(ast);
    }

    private static void semanticCheck()
    {
        ClassFuncBuilder globalScopeBuilder = new ClassFuncBuilder();
        globalScopeBuilder.visit(ast);
        globalScope = globalScopeBuilder.getScope();
        new ClassVarMemBuilder(globalScope).visit(ast);
        new SemanticChecker(globalScope).visit(ast);
    }

    private static void buildIR()
    {
        IRBuilder irBuilder = new IRBuilder(globalScope);
        irBuilder.visit(ast);
        irRoot = irBuilder.getIrRoot();
        new BinaryOpTransformer(irRoot).run();
    }

    private static void printIR() throws Exception
    {
        String outFile = "D:\\QQPCmgr\\Desktop\\src\\jwb\\gzp.txt";
        //outFile = null;
        PrintStream outS;
        if (outFile == null) outS = System.out;
        else outS = new PrintStream(new FileOutputStream(outFile));
        new IRPrinter(outS).visit(irRoot);
    }

    private static void generateCode() throws Exception
    {
        PrintStream outS = System.out;
        new FunctionInLineOptimizer(irRoot).run();
        new GlobalVarProcessor(irRoot).run();
        new RegisterAllocator(irRoot).run();
        new NASMTransformer(irRoot).run();
        new NASMPrinter(outS).visit(irRoot);
    }
}