package Utils;

import AST.Location;

public class CompilerError extends Error
{
    public CompilerError(Location loc, String msg)
    {
        super(String.format("Compiler Error at %s: %s", loc.toString(), msg));
    }

    public CompilerError(String msg)
    {
        super(String.format("Compiler Error: %s", msg));
    }
}
