package IR;

public class StaticStr extends StaticData
{
    private String value;

    public StaticStr(String value)
    {
        super("static_str", 8);
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }
}
