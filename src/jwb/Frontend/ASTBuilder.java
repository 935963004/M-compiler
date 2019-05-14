package Frontend;

import AST.*;
import Parser.MBaseVisitor;
import Parser.MParser;
import Type.*;
import org.antlr.v4.runtime.ParserRuleContext;
import Utils.CompilerError;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends MBaseVisitor<Node>
{
    private boolean commonExprOptimize = true;

    @Override
    public Node visitProgram(MParser.ProgramContext ctx)
    {
        List<DeclNode> decls = new ArrayList<>();
        if (ctx.programSection() != null) {
            for (ParserRuleContext programSec : ctx.programSection()) {
                Node decl = visit(programSec);
                decls.add((DeclNode) decl);
            }
        }
        return new ProgramNode(decls, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitProgramSection(MParser.ProgramSectionContext ctx)
    {
        if (ctx.variableDecl() != null) return visit(ctx.variableDecl());
        else if (ctx.functionDecl() != null) return visit(ctx.functionDecl());
        else if (ctx.classDecl() != null) return visit(ctx.classDecl());
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid program section");
    }

    @Override
    public Node visitFunctionDecl(MParser.FunctionDeclContext ctx)
    {
        TypeNode returnType = ctx.functionType() == null ? null : (TypeNode) visit(ctx.functionType());
        String name = ctx.ID().getText();
        List<VarDeclNode> parameterList = new ArrayList<>();
        if (ctx.parameterListDecl() != null) {
            for (ParserRuleContext parameterDecl : ctx.parameterListDecl().parameterDecl()) {
                Node paraDecl = visit(parameterDecl);
                parameterList.add((VarDeclNode) paraDecl);
            }
        }
        BlockStmtNode body = (BlockStmtNode) visit(ctx.block());
        return new FuncDeclNode(returnType, name, parameterList, body, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitClassDecl(MParser.ClassDeclContext ctx)
    {
        String name = ctx.ID().getText();
        List<VarDeclNode> varMember = new ArrayList<>();
        List<FuncDeclNode> funMember = new ArrayList<>();
        if (ctx.memberDecl() != null) {
            for (ParserRuleContext memberDecl : ctx.memberDecl()) {
                Node memberDeclaration = visit(memberDecl);
                if (memberDeclaration instanceof VarDeclNode) varMember.add((VarDeclNode) memberDeclaration);
                else if (memberDeclaration instanceof FuncDeclNode) funMember.add((FuncDeclNode) memberDeclaration);
                else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid member declaration");
            }
        }
        return new ClassDeclNode(name, varMember, funMember, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitVariableDecl(MParser.VariableDeclContext ctx)
    {
        TypeNode type = (TypeNode) visit(ctx.typeType());
        String name = ctx.ID().getText();
        ExprNode expr = ctx.expression() == null ? null : (ExprNode) visit(ctx.expression());
        return new VarDeclNode(type, name, expr, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitMemberDecl(MParser.MemberDeclContext ctx)
    {
        if (ctx.variableDecl() != null) return visit(ctx.variableDecl());
        else if (ctx.functionDecl() != null) return visit(ctx.functionDecl());
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid member declaration");
    }

    @Override
    public Node visitParameterDecl(MParser.ParameterDeclContext ctx)
    {
        TypeNode type = (TypeNode) visit(ctx.typeType());
        String name = ctx.ID().getText();
        return new VarDeclNode(type, name, null, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitFunctionType(MParser.FunctionTypeContext ctx)
    {
        if (ctx.typeType() != null) return visit(ctx.typeType());
        else return new TypeNode(VoidType.getVoidType(), Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitArrayType(MParser.ArrayTypeContext ctx)
    {
        TypeNode baseType = (TypeNode) visit(ctx.typeType());
        return new TypeNode(new ArrayType(baseType.getType()), Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitNonArrayType(MParser.NonArrayTypeContext ctx)
    {
        return visit(ctx.basicType());
    }

    @Override
    public Node visitBasicType(MParser.BasicTypeContext ctx)
    {
        if (ctx.INT() != null) return new TypeNode(IntType.getIntType(), Location.ctxGetLoc(ctx));
        else if (ctx.BOOL() != null) return new TypeNode(BoolType.getBoolType(), Location.ctxGetLoc(ctx));
        else if (ctx.STRING() != null) return new TypeNode(StringType.getStringType(), Location.ctxGetLoc(ctx));
        else if (ctx.ID() != null) return new TypeNode(new ClassType(ctx.ID().getText()), Location.ctxGetLoc(ctx));
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid type");
    }

    @Override
    public Node visitNonArrayTypeCreator(MParser.NonArrayTypeCreatorContext ctx)
    {
        if (ctx.INT() != null) return new TypeNode(IntType.getIntType(), Location.ctxGetLoc(ctx));
        else if (ctx.BOOL() != null) return new TypeNode(BoolType.getBoolType(), Location.ctxGetLoc(ctx));
        else if (ctx.STRING() != null) return new TypeNode(StringType.getStringType(), Location.ctxGetLoc(ctx));
        else if (ctx.ID() != null) return new TypeNode(new ClassType(ctx.ID().getText()), Location.ctxGetLoc(ctx));
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid type");
    }

    @Override
    public Node visitBlockStmt(MParser.BlockStmtContext ctx)
    {
        return visit(ctx.block());
    }

    @Override
    public Node visitExpressionStmt(MParser.ExpressionStmtContext ctx)
    {
        if (ctx.expression() == null) return null;
        ExprNode expr = (ExprNode) visit(ctx.expression());
        return new ExprStmtNode(expr, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitBlock(MParser.BlockContext ctx)
    {
        List<Node> stmtsAndVarDecls = new ArrayList<>();
        if (ctx.blockStatement() != null) {
            for (ParserRuleContext blockStmt : ctx.blockStatement()) {
                Node stmtAndVarDecl = visit(blockStmt);
                if (stmtAndVarDecl != null) stmtsAndVarDecls.add(stmtAndVarDecl);
            }
        }
        return new BlockStmtNode(stmtsAndVarDecls, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitStmt(MParser.StmtContext ctx)
    {
        return visit(ctx.statement());
    }

    @Override
    public Node visitVarDeclStmt(MParser.VarDeclStmtContext ctx)
    {
        return visit(ctx.variableDecl());
    }

    @Override
    public Node visitIfElseStmt(MParser.IfElseStmtContext ctx)
    {
        ExprNode condition = (ExprNode) visit(ctx.expression());
        StmtNode thenStmt = (StmtNode) visit(ctx.thenStmt);
        StmtNode elseStmt = ctx.elseStmt == null ? null : (StmtNode) visit(ctx.elseStmt);
        return new IfElseStmtNode(condition, thenStmt, elseStmt, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitWhileStmt(MParser.WhileStmtContext ctx)
    {
        ExprNode condition = (ExprNode) visit(ctx.expression());
        StmtNode stmt = (StmtNode) visit(ctx.statement());
        return new WhileStmtNode(condition, stmt, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitForStmt(MParser.ForStmtContext ctx)
    {
        ExprNode init = ctx.init == null ? null : (ExprNode) visit(ctx.init);
        ExprNode cond = ctx.cond == null ? null : (ExprNode) visit(ctx.cond);
        ExprNode update = ctx.update == null ? null : (ExprNode) visit(ctx.update);
        StmtNode stmt = (StmtNode) visit(ctx.statement());
        return new ForStmtNode(init, cond, update, stmt, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitContinueStmt(MParser.ContinueStmtContext ctx)
    {
        return new ContinueStmtNode(Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitBreakStmt(MParser.BreakStmtContext ctx)
    {
        return new BreakStmtNode(Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitReturnStmt(MParser.ReturnStmtContext ctx)
    {
        ExprNode expr = ctx.expression() == null ? null : (ExprNode) visit(ctx.expression());
        return new ReturnStmtNode(expr, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitNewExpr(MParser.NewExprContext ctx)
    {
        return visit(ctx.creator());
    }

    @Override
    public Node visitPrefixExpr(MParser.PrefixExprContext ctx)
    {
        ExprNode expr = (ExprNode) visit(ctx.expression());
        PrefixExprNode.prefixOp op;
        if (ctx.op.getText().equals("++")) op = PrefixExprNode.prefixOp.INC;
        else if (ctx.op.getText().equals("--")) op = PrefixExprNode.prefixOp.DEC;
        else if (ctx.op.getText().equals("+")) op = PrefixExprNode.prefixOp.POS;
        else if (ctx.op.getText().equals("-")) op = PrefixExprNode.prefixOp.NEG;
        else if (ctx.op.getText().equals("!")) op = PrefixExprNode.prefixOp.LOGIC_NOT;
        else if (ctx.op.getText().equals("~")) op = PrefixExprNode.prefixOp.BITWISE_NOT;
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid prefix operator");
        return new PrefixExprNode(op, expr, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitArrayExpr(MParser.ArrayExprContext ctx)
    {
        ExprNode arr = (ExprNode) visit(ctx.arr), sub = (ExprNode) visit(ctx.sub);
        return new ArrayExprNode(arr, sub, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitSuffixExpr(MParser.SuffixExprContext ctx)
    {
        ExprNode expr = (ExprNode) visit(ctx.expression());
        SuffixExprNode.suffixOp op;
        if (ctx.op.getText().equals("++")) op = SuffixExprNode.suffixOp.INC;
        else if (ctx.op.getText().equals("--")) op = SuffixExprNode.suffixOp.DEC;
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid suffix operator");
        return new SuffixExprNode(op, expr, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitBinaryExpr(MParser.BinaryExprContext ctx)
    {
        ExprNode lhs = (ExprNode) visit(ctx.lhs), rhs = (ExprNode) visit(ctx.rhs);
        BinaryExprNode.binaryOp op;
        switch (ctx.op.getText()) {
            case "*":
                op = BinaryExprNode.binaryOp.MUL;
                break;
            case "/":
                op = BinaryExprNode.binaryOp.DIV;
                break;
            case "%":
                op = BinaryExprNode.binaryOp.MOD;
                break;
            case "+":
                op = BinaryExprNode.binaryOp.ADD;
                break;
            case "-":
                op = BinaryExprNode.binaryOp.SUB;
                break;
            case "<<":
                op = BinaryExprNode.binaryOp.SHL;
                break;
            case ">>":
                op = BinaryExprNode.binaryOp.SHR;
                break;
            case "<":
                op = BinaryExprNode.binaryOp.LESS;
                break;
            case ">":
                op = BinaryExprNode.binaryOp.GREATER;
                break;
            case "<=":
                op = BinaryExprNode.binaryOp.LESS_EQUAL;
                break;
            case ">=":
                op = BinaryExprNode.binaryOp.GREATER_EQUAL;
                break;
            case "==":
                op = BinaryExprNode.binaryOp.EQUAL;
                break;
            case "!=":
                op = BinaryExprNode.binaryOp.UNEQUAL;
                break;
            case "&":
                op = BinaryExprNode.binaryOp.BITWISE_AND;
                break;
            case "^":
                op = BinaryExprNode.binaryOp.BITWISE_XOR;
                break;
            case "|":
                op = BinaryExprNode.binaryOp.BITWISE_OR;
                break;
            case "&&":
                op = BinaryExprNode.binaryOp.LOGIC_AND;
                break;
            case "||":
                op = BinaryExprNode.binaryOp.LOGIC_OR;
                break;
            default:
                throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid binary operator");
        }
        if (lhs instanceof StrExprNode || rhs instanceof StrExprNode) commonExprOptimize = false;
        if (lhs instanceof PrefixExprNode || rhs instanceof PrefixExprNode) commonExprOptimize = false;
        if (lhs instanceof SuffixExprNode || rhs instanceof SuffixExprNode) commonExprOptimize = false;
        if (lhs instanceof AssignExprNode || rhs instanceof AssignExprNode) commonExprOptimize = false;
        if (lhs instanceof FuncCallExprNode || rhs instanceof FuncCallExprNode) commonExprOptimize = false;
        if (commonExprOptimize && lhs.equals(rhs)) {
            if (op == BinaryExprNode.binaryOp.ADD) return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(2, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
            else if (op == BinaryExprNode.binaryOp.SUB) return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(0, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
        }
        if (commonExprOptimize && (op == BinaryExprNode.binaryOp.ADD || op == BinaryExprNode.binaryOp.SUB)) {
            if (lhs instanceof BinaryExprNode && ((BinaryExprNode) lhs).getOp() == BinaryExprNode.binaryOp.MUL) {
                if (((BinaryExprNode) lhs).getLhs().equals(rhs) && ((BinaryExprNode) lhs).getRhs() instanceof NumExprNode) {
                    if (op == BinaryExprNode.binaryOp.ADD)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, rhs, new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() + 1, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    else if (op == BinaryExprNode.binaryOp.SUB)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, rhs, new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() - 1, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                }
                if (((BinaryExprNode) lhs).getRhs().equals(rhs) && ((BinaryExprNode) lhs).getLhs() instanceof NumExprNode) {
                    if (op == BinaryExprNode.binaryOp.ADD)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, rhs, new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() + 1, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    else if (op == BinaryExprNode.binaryOp.SUB)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, rhs, new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() - 1, Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                }
            }
            if (rhs instanceof BinaryExprNode && ((BinaryExprNode) rhs).getOp() == BinaryExprNode.binaryOp.MUL) {
                if (((BinaryExprNode) rhs).getLhs().equals(lhs) && ((BinaryExprNode) rhs).getRhs() instanceof NumExprNode) {
                    if (op == BinaryExprNode.binaryOp.ADD)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(1 + ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    else if (op == BinaryExprNode.binaryOp.SUB)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(1 - ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                }
                if (((BinaryExprNode) rhs).getRhs().equals(lhs) && ((BinaryExprNode) rhs).getLhs() instanceof NumExprNode) {
                    if (op == BinaryExprNode.binaryOp.ADD)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(1 + ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    else if (op == BinaryExprNode.binaryOp.SUB)
                        return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, lhs, new NumExprNode(1 - ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                }
            }
            if (lhs instanceof BinaryExprNode && rhs instanceof BinaryExprNode && ((BinaryExprNode) lhs).getOp() == BinaryExprNode.binaryOp.MUL && ((BinaryExprNode) rhs).getOp() == BinaryExprNode.binaryOp.MUL) {
                if (((BinaryExprNode) lhs).getLhs() instanceof NumExprNode && ((BinaryExprNode) rhs).getLhs() instanceof NumExprNode) {
                    if (((BinaryExprNode) lhs).getRhs().equals(((BinaryExprNode) rhs).getRhs())) {
                        if (op == BinaryExprNode.binaryOp.ADD)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getRhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() + ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                        else if (op == BinaryExprNode.binaryOp.SUB)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getRhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() - ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    }
                } else if (((BinaryExprNode) lhs).getLhs() instanceof NumExprNode && ((BinaryExprNode) rhs).getRhs() instanceof NumExprNode) {
                    if (((BinaryExprNode) lhs).getRhs().equals(((BinaryExprNode) rhs).getLhs())) {
                        if (op == BinaryExprNode.binaryOp.ADD)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getRhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() + ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                        else if (op == BinaryExprNode.binaryOp.SUB)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getRhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getLhs()).getValue() - ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    }
                } else if (((BinaryExprNode) lhs).getRhs() instanceof NumExprNode && ((BinaryExprNode) rhs).getLhs() instanceof NumExprNode) {
                    if (((BinaryExprNode) lhs).getLhs().equals(((BinaryExprNode) rhs).getRhs())) {
                        if (op == BinaryExprNode.binaryOp.ADD)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getLhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() + ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                        else if (op == BinaryExprNode.binaryOp.SUB)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getLhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() - ((NumExprNode) ((BinaryExprNode) rhs).getLhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    }
                } else if (((BinaryExprNode) lhs).getRhs() instanceof NumExprNode && ((BinaryExprNode) rhs).getRhs() instanceof NumExprNode) {
                    if (((BinaryExprNode) lhs).getLhs().equals(((BinaryExprNode) rhs).getLhs())) {
                        if (op == BinaryExprNode.binaryOp.ADD)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getLhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() + ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                        else if (op == BinaryExprNode.binaryOp.SUB)
                            return new BinaryExprNode(BinaryExprNode.binaryOp.MUL, ((BinaryExprNode) lhs).getLhs(), new NumExprNode(((NumExprNode) ((BinaryExprNode) lhs).getRhs()).getValue() - ((NumExprNode) ((BinaryExprNode) rhs).getRhs()).getValue(), Location.ctxGetLoc(ctx)), Location.ctxGetLoc(ctx));
                    }
                }
            }
        }
        return new BinaryExprNode(op, lhs, rhs, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitMemExpr(MParser.MemExprContext ctx)
    {
        ExprNode expr = (ExprNode) visit(ctx.expression());
        String name = ctx.ID().getText();
        return new MemExprNode(expr, name, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitFuncCallExpr(MParser.FuncCallExprContext ctx)
    {
        ExprNode expr = (ExprNode) visit(ctx.expression());
        List<ExprNode> paraList = new ArrayList<>();
        if (ctx.parameterList() != null) {
            for (ParserRuleContext parameter : ctx.parameterList().expression()) {
                ExprNode para = (ExprNode) visit(parameter);
                paraList.add(para);
            }
        }
        return new FuncCallExprNode(expr, paraList, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitAssignExpr(MParser.AssignExprContext ctx)
    {
        ExprNode lhs = (ExprNode) visit(ctx.lhs), rhs = (ExprNode) visit(ctx.rhs);
        return new AssignExprNode(lhs, rhs, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitIdExpr(MParser.IdExprContext ctx)
    {
        String name = ctx.ID().getText();
        return new IdExprNode(name, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitThisExpr(MParser.ThisExprContext ctx)
    {
        return new ThisExprNode(Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitBracketsExpr(MParser.BracketsExprContext ctx)
    {
        return visit(ctx.expression());
    }

    @Override
    public Node visitNumExpr(MParser.NumExprContext ctx)
    {
        int value;
        try {
            value = Integer.parseInt(ctx.getText());
        }
        catch (Exception e) {
            throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid number: " + e);
        }
        return new NumExprNode(value, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitStrExpr(MParser.StrExprContext ctx)
    {
        commonExprOptimize = false;
        String str = ctx.getText();
        StringBuffer s = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            if (str.charAt(i) == '\\' && i + 1 < len){
                if (str.charAt(i + 1) == '\\') s.append('\\');
                else if (str.charAt(i + 1) == 'n') s.append('\n');
                else if (str.charAt(i + 1) == '\"') s.append('\"');
                else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid string");
                ++i;
            }
            else s.append(str.charAt(i));
        }
        return new StrExprNode(s.toString(), Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitNullExpr(MParser.NullExprContext ctx)
    {
        return new NullExprNode(Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitBoolExpr(MParser.BoolExprContext ctx)
    {
        boolean value;
        if (ctx.getText().equals("true")) value = true;
        else if (ctx.getText().equals("false")) value = false;
        else throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid bool constant");
        return new BoolConstExprNode(value, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitErrorCreator(MParser.ErrorCreatorContext ctx)
    {
        throw new CompilerError(Location.ctxGetLoc(ctx), "Invalid creator");
    }

    @Override
    public Node visitArrayCreator(MParser.ArrayCreatorContext ctx)
    {
        TypeNode type = (TypeNode) visit(ctx.basicType());
        List<ExprNode> exprList = new ArrayList<>();
        int cnt = 0;
        for (ParserRuleContext ExprList : ctx.expression()) {
            ExprNode expr = (ExprNode) visit(ExprList);
            exprList.add(expr);
            ++cnt;
        }
        int dimNum = (ctx.getChildCount() - cnt - 1) / 2;
        for (int i = 0; i < dimNum; ++i) type.setType(new ArrayType(type.getType()));
        return new NewExprNode(type, exprList, dimNum, Location.ctxGetLoc(ctx));
    }

    @Override
    public Node visitNonArrayCreator(MParser.NonArrayCreatorContext ctx)
    {
        TypeNode type = (TypeNode) visit(ctx.nonArrayTypeCreator());
        return new NewExprNode(type, null, 0, Location.ctxGetLoc(ctx));
    }
}
