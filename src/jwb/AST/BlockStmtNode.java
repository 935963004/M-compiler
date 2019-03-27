package AST;

import ScopeEntity.Scope;

import java.util.List;

public class BlockStmtNode extends StmtNode
{
    private List<Node> stmtsAndVarDecls;
    private Scope scope;
    private boolean isInit = false;

    public BlockStmtNode(List<Node> stmtsAndVarDecls, Location loc)
    {
        this.stmtsAndVarDecls = stmtsAndVarDecls;
        this.loc = loc;
    }

    public List<Node> getStmtsAndVarDecls()
    {
        return stmtsAndVarDecls;
    }

    public void setScope(Scope parent)
    {
        if (!isInit) {
            scope = new Scope(parent, false);
            isInit = true;
        }
    }

    public Scope getScope()
    {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
