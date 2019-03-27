package Frontend;

import AST.*;
import Scope.*;
import Type.*;
import Utils.CompilerError;
import Utils.SemanticError;

public class SemanticChecker extends ScopeBuilder
{
    private Scope globalScope, currentScope;
    private int inLoop = 0;
    private Type currentReturnType;
    private ClassType currentClassType;
    private FuncEntity currentFuncEntity;

    public SemanticChecker(Scope globalScope)
    {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node)
    {
        currentScope = globalScope;
        for (DeclNode declNode : node.getDecls()) {
            if (declNode instanceof VarDeclNode) declNode.accept(this);
            else if (declNode instanceof FuncDeclNode) declNode.accept(this);
            else if (declNode instanceof ClassDeclNode) declNode.accept(this);
            else throw new CompilerError(declNode.getLocation(), "Invalid declNode");
        }
    }

    @Override
    public void visit(VarDeclNode node)
    {
        if (node.getType().getType() instanceof ClassType) {
            String className = ((ClassType) node.getType().getType()).getName();
            currentScope.get(node.getLocation(), className, "@C" + className);
        }
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            if (checkVarExpr(node)) throw new SemanticError(node.getLocation(), "Invalid initialization type");
        }
        VarEntity entity = new VarEntity(node);
        entity.setGlobal(currentScope.equals(globalScope));
        currentScope.put(node.getLocation(), node.getName(), "@V" + node.getName(), entity);
    }

    private boolean checkVarExpr(VarDeclNode node)
    {
        if (node.getExpr().getType() instanceof NullType) return !(node.getType().getType() instanceof ClassType || node.getType().getType() instanceof ArrayType);
        if (node.getType().getType() instanceof VoidType || node.getExpr().getType() instanceof VoidType) return true;
        return !node.getType().getType().equals(node.getExpr().getType());
    }

    @Override
    public void visit(FuncDeclNode node)
    {
        FuncEntity entity = (FuncEntity) currentScope.get(node.getLocation(), node.getName(), "@F" + node.getName());
        if (entity.getReturnType() instanceof ClassType) globalScope.get(node.getLocation(), ((ClassType) entity.getReturnType()).getName(), "@C" + ((ClassType) entity.getReturnType()).getName());
        currentReturnType = entity.getReturnType();
        node.getBody().setScope(currentScope);
        currentScope = node.getBody().getScope();
        if (currentClassType != null) {
            currentScope.put(node.getLocation(), "this", "@Vthis", new VarEntity("this", currentClassType));
            if (node.getIsConstruct() && !node.getName().equals(currentClassType.getName())) throw new SemanticError(node.getLocation(), "Constructor's name should be the same as the class's name");
        }
        for (VarDeclNode varDeclNode : node.getParameterList()) {
            if (varDeclNode.getExpr() != null) throw new SemanticError(varDeclNode.getLocation(), "Function's parameters should have no assignment");
            varDeclNode.accept(this);
        }
        node.getBody().accept(this);
    }

    @Override
    public void visit(ClassDeclNode node)
    {
        ClassEntity entity = (ClassEntity) globalScope.get(node.getLocation(), node.getName(), "@C" + node.getName());
        currentScope = entity.getScope();
        currentClassType = (ClassType) entity.getType();
        for (FuncDeclNode funcDeclNode : node.getFuncMember()) funcDeclNode.accept(this);
        currentScope = currentScope.getParent();
        currentClassType = null;
    }

    @Override
    public void visit(BlockStmtNode node)
    {
        node.setScope(currentScope);
        currentScope = node.getScope();
        for (Node node1 : node.getStmtsAndVarDecls()) {
            if (node1 instanceof VarDeclNode) node1.accept(this);
            else if (node1 instanceof StmtNode) {
                if (node1 instanceof BlockStmtNode) {
                    ((BlockStmtNode) node1).setScope(currentScope);
                    currentScope = ((BlockStmtNode) node1).getScope();
                }
                node1.accept(this);
            }
            else throw new CompilerError(node.getLocation(), "Invalid statement in block");
        }
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ExprStmtNode node)
    {
        if (node.getExpr() != null) node.getExpr().accept(this);
    }

    @Override
    public void visit(IfElseStmtNode node)
    {
        node.getCondition().accept(this);
        if (!(node.getCondition().getType() instanceof BoolType)) throw new SemanticError(node.getCondition().getLocation(), "Condition should be boolean type");
        if (node.getThenStmt() != null) node.getThenStmt().accept(this);
        if (node.getElseStmt() != null) node.getElseStmt().accept(this);
    }

    @Override
    public void visit(WhileStmtNode node)
    {
        ++inLoop;
        node.getCondition().accept(this);
        if (!(node.getCondition().getType() instanceof BoolType)) throw new SemanticError(node.getCondition().getLocation(), "Condition should be boolean type");
        if (node.getStmt() != null) node.getStmt().accept(this);
        --inLoop;
    }

    @Override
    public void visit(ForStmtNode node)
    {
        ++inLoop;
        if (node.getInit() != null) node.getInit().accept(this);
        if (node.getCond() != null) {
            node.getCond().accept(this);
            if (!(node.getCond().getType() instanceof BoolType)) throw new SemanticError(node.getLocation(), "Condition should be boolean type");
        }
        if (node.getUpdate() != null) node.getUpdate().accept(this);
        if (node.getStmt() != null) node.getStmt().accept(this);
        --inLoop;
    }

    @Override
    public void visit(ContinueStmtNode node)
    {
        if (inLoop == 0) throw new SemanticError(node.getLocation(), "\"continue\" should be in the loop");
    }

    @Override
    public void visit(BreakStmtNode node)
    {
        if (inLoop == 0) throw new SemanticError(node.getLocation(), "\"break\" should be in the loop");
    }

    @Override
    public void visit(ReturnStmtNode node)
    {
        if (currentReturnType instanceof VoidType || currentReturnType == null) {
            if (node.getExpr() != null) throw new SemanticError(node.getLocation(), "\"return\" should return nothing");
        }
        else {
            if (node.getExpr() == null) throw new SemanticError(node.getLocation(), "\"return\" should return something");
            node.getExpr().accept(this);
            if (checkReturn(node.getExpr().getType())) throw new SemanticError(node.getLocation(), "Return type not match");
        }
    }

    private boolean checkReturn(Type returnType)
    {
        if (returnType instanceof NullType) return !(currentReturnType instanceof ArrayType || currentReturnType instanceof ClassType);
        return !returnType.equals(currentReturnType);
    }

    @Override
    public void visit(SuffixExprNode node)
    {
        node.getExpr().accept(this);
        if (!(node.getExpr().getType() instanceof IntType)) throw new SemanticError(node.getLocation(), "Expression's type should be int type");
        if (!node.getExpr().isLeftValue()) throw new SemanticError(node.getLocation(), "Expression should be left value");
        node.setType(IntType.getIntType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(FuncCallExprNode node)
    {
        node.getExpr().accept(this);
        if (!(node.getExpr().getType() instanceof FuncType)) throw new SemanticError(node.getLocation(), "Expression's type should be function type");
        FuncEntity entity = currentFuncEntity;
        node.setFuncEntity(entity);
        int paraNum = entity.getParameters().size();
        if (paraNum != node.getParaList().size()) throw new SemanticError(node.getLocation(), "Parameter number don't match");
        for (int i = 0; i < paraNum; ++i) {
            node.getParaList().get(i).accept(this);
            Type exprType = node.getParaList().get(i).getType(), paraType = entity.getParameters().get(i).getType();
            Location loc = node.getParaList().get(i).getLocation();
            if (checkFuncCall(exprType, paraType)) throw new SemanticError(loc, "Parameter's type not match");
        }
        node.setType(entity.getReturnType());
        node.setLeftValue(false);
    }

    private boolean checkFuncCall(Type exprType, Type paraType)
    {
        if (exprType instanceof VoidType) return true;
        if (exprType instanceof NullType) return !(paraType instanceof ArrayType || paraType instanceof ClassType);
        return !exprType.equals(paraType);
    }

    @Override
    public void visit(ArrayExprNode node)
    {
        node.getArr().accept(this);
        if (!(node.getArr().getType() instanceof ArrayType)) throw new SemanticError(node.getArr().getLocation(), "Expression's type should be array type");
        node.getSub().accept(this);
        if (!(node.getSub().getType() instanceof IntType)) throw new SemanticError(node.getSub().getLocation(), "Expression's type should be int type");
        node.setType(((ArrayType) node.getArr().getType()).getBaseType());
        node.setLeftValue(true);
    }

    @Override
    public void visit(MemExprNode node)
    {
        node.getExpr().accept(this);
        String className;
        if (node.getExpr().getType() instanceof ArrayType) className = "array";
        else if (node.getExpr().getType() instanceof StringType) className = "string";
        else if (node.getExpr().getType() instanceof ClassType) className = ((ClassType) node.getExpr().getType()).getName();
        else throw new SemanticError(node.getExpr().getLocation(), "Expression should be class type");
        ClassEntity entity = (ClassEntity) currentScope.get(node.getExpr().getLocation(), className, "@C" + className);
        Entity varOrFunc = entity.getScope().selfGetVarFunc(node.getLocation(), node.getName());
        if (varOrFunc instanceof FuncEntity) currentFuncEntity = (FuncEntity) varOrFunc;
        node.setType(varOrFunc.getType());
        node.setLeftValue(true);
    }

    @Override
    public void visit(PrefixExprNode node)
    {
        node.getExpr().accept(this);
        switch (node.getOp()) {
            case INC:
            case DEC:
                if (!(node.getExpr().getType() instanceof IntType)) throw new SemanticError(node.getExpr().getLocation(), "Expression's type should be int type");
                if (!node.getExpr().isLeftValue()) throw new SemanticError(node.getExpr().getLocation(), "Expression should be left value");
                node.setType(IntType.getIntType());
                node.setLeftValue(true);
                break;
            case NEG:
            case POS:
            case BITWISE_NOT:
                if (!(node.getExpr().getType() instanceof IntType)) throw new SemanticError(node.getExpr().getLocation(), "Expression's type should be int type");
                node.setType(IntType.getIntType());
                node.setLeftValue(false);
                break;
            case LOGIC_NOT:
                if (!(node.getExpr().getType() instanceof BoolType)) throw new SemanticError(node.getExpr().getLocation(), "Expression's type should be bool type");
                node.setType(BoolType.getBoolType());
                node.setLeftValue(false);
                break;
            default:
                throw new CompilerError(node.getLocation(), "Invalid prefix operator");
        }
    }

    @Override
    public void visit(NewExprNode node)
    {
        if (node.getExprList() != null) {
            for (ExprNode exprNode : node.getExprList()) {
                exprNode.accept(this);
                if (!(exprNode.getType() instanceof IntType)) throw new SemanticError(exprNode.getLocation(), "Expression's type should be int type");
            }
        }
        node.setType(node.getNewType().getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(BinaryExprNode node)
    {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if (node.getLhs().getType() instanceof StringType && node.getRhs().getType() instanceof StringType) {
            switch (node.getOp()) {
                case ADD:
                    node.setType(StringType.getStringType());
                    break;
                case LESS:
                case GREATER:
                case LESS_EQUAL:
                case GREATER_EQUAL:
                case EQUAL:
                case UNEQUAL:
                    node.setType(BoolType.getBoolType());
                    break;
                default:
                    throw new CompilerError(node.getLocation(), "No such binary operator for string type");
            }
        }
        else {
            switch (node.getOp()) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case SHL:
                case SHR:
                case BITWISE_OR:
                case BITWISE_AND:
                case BITWISE_XOR:
                    if (!(node.getLhs().getType() instanceof IntType && node.getRhs().getType() instanceof IntType)) throw new SemanticError(node.getLocation(), "LHS and RHS should both be int type");
                    node.setType(IntType.getIntType());
                    break;
                case UNEQUAL:
                case EQUAL:
                    if (cmpBinType(node.getLhs().getType(), node.getRhs().getType())) throw new SemanticError(node.getLocation(), "Expression's type not match");
                    node.setType(BoolType.getBoolType());
                    break;
                case GREATER_EQUAL:
                case LESS_EQUAL:
                case GREATER:
                case LESS:
                    if (!(node.getLhs().getType() instanceof IntType && node.getRhs().getType() instanceof IntType)) throw new SemanticError(node.getLocation(), "LHS and RHS should both be int type");
                    node.setType(BoolType.getBoolType());
                    break;
                case LOGIC_OR:
                case LOGIC_AND:
                    if (!(node.getLhs().getType() instanceof BoolType && node.getRhs().getType() instanceof BoolType)) throw new SemanticError(node.getLocation(), "LHS and RHS should both be bool type");
                    node.setType(BoolType.getBoolType());
                    break;
                default:
                    throw new CompilerError(node.getLocation(), "Invalid binary operator");
            }
        }
        node.setLeftValue(false);
    }

    private boolean cmpBinType(Type l, Type r)
    {
        if (l instanceof VoidType || r instanceof VoidType) return true;
        if (l.equals(r)) return false;
        if (l instanceof NullType) return !(r instanceof ArrayType || r instanceof ClassType);
        if (r instanceof NullType) return !(l instanceof ArrayType || l instanceof ClassType);
        return true;
    }

    @Override
    public void visit(AssignExprNode node)
    {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if (!node.getLhs().isLeftValue()) throw new SemanticError(node.getLhs().getLocation(), "LHS should be left value");
        if (cmpAssignType(node.getLhs().getType(), node.getRhs().getType())) throw new SemanticError(node.getLocation(), "LHS's type and RHS's type not match");
        node.setType(node.getLhs().getType());
        node.setLeftValue(false);
    }

    private boolean cmpAssignType(Type l, Type r)
    {
        if (l instanceof VoidType || r instanceof VoidType) return true;
        if (l.equals(r)) return false;
        if (l instanceof NullType) return true;
        if (r instanceof NullType) return !(l instanceof ArrayType || l instanceof ClassType);
        return true;
    }

    @Override
    public void visit(IdExprNode node)
    {
        Entity entity = currentScope.getVarFunc(node.getLocation(), node.getName());
        if (entity instanceof VarEntity) {
            node.setEntity((VarEntity) entity);
            node.setLeftValue(true);
        }
        else if (entity instanceof FuncEntity) {
            currentFuncEntity = (FuncEntity) entity;
            node.setLeftValue(false);
        }
        else throw new SemanticError(node.getLocation(), "Invalid ID");
        node.setType(entity.getType());
    }

    @Override
    public void visit(ThisExprNode node)
    {
        Entity entity = currentScope.getVarFunc(node.getLocation(), "this");
        if (!(entity instanceof VarEntity)) throw new SemanticError(node.getLocation(), "Invalid entity type");
        node.setType(entity.getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(NumExprNode node)
    {
        node.setType(IntType.getIntType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(StrExprNode node)
    {
        node.setType(StringType.getStringType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(BoolConstExprNode node)
    {
        node.setType(BoolType.getBoolType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(NullExprNode node)
    {
        node.setType(NullType.getNullType());
        node.setLeftValue(false);
    }
}
