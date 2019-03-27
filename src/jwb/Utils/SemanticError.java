package Utils;

import AST.Location;

public class SemanticError extends Error
{
    public SemanticError(Location loc, String msg)
    {
        super(String.format("Semantic Error at %s: %s", loc.toString(), msg));
    }

    public SemanticError(String msg)
    {
        super(String.format("Semantic Error: %s", msg));
    }
}
