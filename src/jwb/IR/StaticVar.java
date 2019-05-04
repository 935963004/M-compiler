package IR;

public class StaticVar extends StaticData
{
    public StaticVar(String name, int size)
    {
        super(name, size);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }
}
