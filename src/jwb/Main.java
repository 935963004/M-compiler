import AST.ProgramNode;
import Frontend.*;
import Parser.MLexer;
import Parser.MParser;
import Parser.SyntaxErrorListener;
import Scope.Scope;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


import java.io.*;

public class Main
{
    private static ProgramNode ast;
    private static Scope globalScope;

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

    }

    private static void buildAST() throws Exception
    {
        String inFile = "D:\\QQPCmgr\\Desktop\\src\\jwb\\testcase\\test0.mx";
        InputStream inS;
        if (inFile == null) inS = System.in;
        else inS = new FileInputStream(inFile);
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
        GlobalScopeBuilder globalScopeBuilder = new GlobalScopeBuilder();
        globalScopeBuilder.visit(ast);
        globalScope = globalScopeBuilder.getScope();
        new ClassVarMemBuilder(globalScope).visit(ast);
        new SemanticChecker(globalScope).visit(ast);
    }
}
