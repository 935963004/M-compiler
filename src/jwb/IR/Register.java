package IR;

public abstract class Register extends RegValue
{
    public abstract void accept(IRVisitor irVisitor);
}
