package Frontend;

import AST.*;

public class ASTPrinter implements ASTVisitor
{
    private StringBuffer indent = new StringBuffer();

    private void printf(String format, Object... args)
    {
        System.out.printf(indent.toString() + format, args);
    }

    private void addIndent()
    {
        indent.append('\t');
    }

    private void deleteIndent()
    {
        indent.deleteCharAt(indent.length() - 1);
    }

    @Override
    public void visit(ProgramNode node)
    {
        if (node == null) printf("AST tree is null");
        else {
            printf("ProgramNode %s:\n", node.getLocation().toString());
            if (node.getDecls().isEmpty()) printf("-> decls: null\n");
            else {
                printf("-> decls:\n");
                for (DeclNode declNode : node.getDecls()) declNode.accept(this);
            }
        }
    }

    @Override
    public void visit(FuncDeclNode node)
    {
        addIndent();
        printf("FuncDeclNode %s:\n", node.getLocation().toString());
        printf("-> isConstruct: %b\n", node.getIsConstruct());
        if (node.getReturnType() == null) printf("returnType: null\n");
        else {
            printf("-> returnType:\n");
            node.getReturnType().accept(this);
        }
        printf("-> name: %s\n", node.getName());
        if (node.getParameterList().isEmpty()) printf("-> parameterList: null");
        else {
            printf("-> parameterList:\n");
            for (VarDeclNode varDeclNode : node.getParameterList()) varDeclNode.accept(this);
        }
        printf("-> body:\n");
        node.getBody().accept(this);
        deleteIndent();
    }

    @Override
    public void visit(ClassDeclNode node)
    {
        addIndent();
        printf("ClassDeclNode %s:\n", node.getLocation().toString());
        printf("-> name: %s\n", node.getName());
        if (node.getVarMember().isEmpty()) printf("-> varMember: null\n");
        else {
            printf("-> varMember:\n");
            for (VarDeclNode varDeclNode : node.getVarMember()) varDeclNode.accept(this);
        }
        if (node.getFuncMember().isEmpty()) printf("-> funcMember: null\n");
        else {
            printf("-> funcMember:\n");
            for (FuncDeclNode funcDeclNode : node.getFuncMember()) funcDeclNode.accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(VarDeclNode node)
    {
        addIndent();
        printf("VarDeclNode %s:\n", node.getLocation().toString());
        printf("-> type:\n");
        node.getType().accept(this);
        printf("-> name: %s\n", node.getName());
        if (node.getExpr() == null) printf("-> expr: null\n");
        else {
            printf("-> expr:\n");
            node.getExpr().accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(BlockStmtNode node)
    {
        addIndent();
        printf("BlockStmtNode %s:\n", node.getLocation().toString());
        if (node.getStmtsAndVarDecls().isEmpty()) printf("-> stmtsAndVarDecls: null\n");
        else {
            printf("->stmtsAndVarDecls\n");
            for (Node node1 : node.getStmtsAndVarDecls()) node1.accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(ExprStmtNode node)
    {
        addIndent();
        printf("ExprStmtNode %s:\n", node.getLocation().toString());
        printf("-> expr:\n");
        node.getExpr().accept(this);
        deleteIndent();
    }

    @Override
    public void visit(IfElseStmtNode node)
    {
        addIndent();
        printf("IfElseStmtNode %s:\n", node.getLocation().toString());
        printf("-> condition:\n");
        node.getCondition().accept(this);
        if (node.getThenStmt() == null) printf("-> thenStmt: null\n");
        else {
            printf("-> thenStmt:\n");
            node.getThenStmt().accept(this);
        }
        if (node.getElseStmt() == null) printf("-> elseStmt: null\n");
        else {
            printf("-> elseStmt:\n");
            node.getElseStmt().accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(WhileStmtNode node)
    {
        addIndent();
        printf("WhileStmtNode %s:\n", node.getLocation().toString());
        printf("-> condition:\n");
        node.getCondition().accept(this);
        if (node.getStmt() == null) printf("-> stmt: null\n");
        else {
            printf("-> stmt:\n");
            node.getStmt().accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(ForStmtNode node)
    {
        addIndent();
        printf("ForStmtNode %s:\n", node.getLocation().toString());
        if (node.getInit() == null) printf("-> init: null\n");
        else {
            printf("-> init:\n");
            node.getInit().accept(this);
        }
        if (node.getCond() == null) printf("-> cond: null\n");
        else {
            printf("-> cond:\n");
            node.getCond().accept(this);
        }
        if (node.getUpdate() == null) printf("-> update: null\n");
        else {
            printf("-> update:\n");
            node.getUpdate().accept(this);
        }
        if (node.getStmt() == null) printf("-> stmt: null\n");
        else {
            printf("-> stmt:\n");
            node.getStmt().accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(ContinueStmtNode node)
    {
        addIndent();
        printf("ContinueStmtNode %s:\n", node.getLocation().toString());
        deleteIndent();
    }

    @Override
    public void visit(BreakStmtNode node)
    {
        addIndent();
        printf("BreakStmtNode %s:\n", node.getLocation().toString());
        deleteIndent();
    }

    @Override
    public void visit(ReturnStmtNode node)
    {
        addIndent();
        printf("ReturnStmtNode %s:\n", node.getLocation().toString());
        if (node.getExpr() == null) printf("-> expr: null\n");
        else {
            printf("-> expr:\n");
            node.getExpr().accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(SuffixExprNode node)
    {
        addIndent();
        printf("SuffixExprNode %s:\n", node.getLocation().toString());
        printf("-> op: %s\n", node.getOp().toString());
        printf("-> expr:\n");
        node.getExpr().accept(this);
        deleteIndent();
    }

    @Override
    public void visit(FuncCallExprNode node)
    {
        addIndent();
        printf("FuncCallExprNode %s:\n", node.getLocation().toString());
        printf("-> expr:\n");
        node.getExpr().accept(this);
        if (node.getParaList().isEmpty()) printf("-> paraList: null\n");
        else {
            printf("-> paraList:\n");
            for (ExprNode exprNode : node.getParaList()) exprNode.accept(this);
        }
        deleteIndent();
    }

    @Override
    public void visit(ArrayExprNode node)
    {
        addIndent();
        printf("ArrayExprNode %s:\n", node.getLocation().toString());
        printf("-> arr:\n");
        node.getArr().accept(this);
        printf("-> sub:\n");
        node.getSub().accept(this);
        deleteIndent();
    }

    @Override
    public void visit(MemExprNode node)
    {
        addIndent();
        printf("MemExprNode %s:\n", node.getLocation().toString());
        printf("-> expr:\n");
        node.getExpr().accept(this);
        printf("-> name: %s\n", node.getName());
        deleteIndent();
    }

    @Override
    public void visit(PrefixExprNode node)
    {
        addIndent();
        printf("PrefixExprNode %s:\n", node.getLocation().toString());
        printf("-> expr:\n");
        node.getExpr().accept(this);
        printf("-> op: %s\n", node.getOp().toString());
        deleteIndent();
    }

    @Override
    public void visit(NewExprNode node)
    {
        addIndent();
        printf("NewExprNode %s:\n", node.getLocation().toString());
        printf("-> type:\n");
        node.getNewType().accept(this);
        if (node.getDimNum() == 0) printf("-> dimNum: 0\n");
        else {
            printf("-> exprList:\n");
            for (ExprNode exprNode : node.getExprList()) exprNode.accept(this);
            printf("-> dimNum: %d\n", node.getDimNum());
        }
        deleteIndent();
    }

    @Override
    public void visit(BinaryExprNode node)
    {
        addIndent();
        printf("BinaryExprNode %s:\n", node.getLocation().toString());
        printf("-> lhs:\n");
        node.getLhs().accept(this);
        printf("-> rhs:\n");
        node.getRhs().accept(this);
        printf("-> op: %s\n", node.getOp().toString());
        deleteIndent();
    }

    @Override
    public void visit(AssignExprNode node)
    {
        addIndent();
        printf("AssignExprNode %s:\n", node.getLocation().toString());
        printf("-> lhs:\n");
        node.getLhs().accept(this);
        printf("-> rhs:\n");
        node.getRhs().accept(this);
        deleteIndent();
    }

    @Override
    public void visit(IdExprNode node)
    {
        addIndent();
        printf("IdExprNode %s:\n", node.getLocation().toString());
        printf("-> IdExprNode: %s\n", node.getName());
        deleteIndent();
    }

    @Override
    public void visit(ThisExprNode node)
    {
        addIndent();
        printf("ThisExprNode %s:\n", node.getLocation().toString());
        deleteIndent();
    }

    @Override
    public void visit(NumExprNode node)
    {
        addIndent();
        printf("NumExprNode %s:\n", node.getLocation().toString());
        printf("-> value: %d\n", node.getValue());
        deleteIndent();
    }

    @Override
    public void visit(StrExprNode node)
    {
        addIndent();
        printf("StrExprNode %s:\n", node.getLocation().toString());
        printf("-> str: %s\n", node.getStr());
        deleteIndent();
    }

    @Override
    public void visit(BoolConstExprNode node)
    {
        addIndent();
        printf("BoolConstExprNode %s:\n", node.getLocation().toString());
        printf("-> value: %b\n", node.getValue());
        deleteIndent();
    }

    @Override
    public void visit(NullExprNode node)
    {
        addIndent();
        printf("NullExprNode %s:\n", node.getLocation().toString());
        deleteIndent();
    }

    @Override
    public void visit(TypeNode node)
    {
        addIndent();
        printf("TypeNode %s:\n", node.getLocation().toString());
        printf("-> type: %s\n", node.getType().toString());
        deleteIndent();
    }
}
