package AST;

abstract public class Node
{
    protected Location loc;
    protected boolean outInfluence = false;

    public Location getLocation()
    {
        return loc;
    }

    abstract public void accept(ASTVisitor visitor);
}
