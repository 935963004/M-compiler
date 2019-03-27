package Frontend;

import AST.*;
import Scope.*;
import Type.*;
import Utils.SemanticError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClassFuncBuilder extends ScopeBuilder
{
    private Scope scope = new Scope();

    @Override
    public void visit(ProgramNode node)
    {
        init();
        for (DeclNode declNode : node.getDecls()) {
            if (!(declNode instanceof VarDeclNode)) declNode.accept(this);
        }
        checkMain();
    }

    @Override
    public void visit(FuncDeclNode node)
    {
        String name = node.getName();
        scope.put(node.getLocation(), name, "@F" + name, new FuncEntity(node));
    }

    @Override
    public void visit(ClassDeclNode node)
    {
        String name = node.getName();
        scope.put(node.getLocation(), name, "@C" + name, new ClassEntity(node, scope));
    }

    private void init()
    {
        scope.setTop(true);
        builtInFunction(scope, "print", Collections.singletonList(new VarEntity("str", StringType.getStringType())), VoidType.getVoidType());
        builtInFunction(scope, "println", Collections.singletonList(new VarEntity("str", StringType.getStringType())), VoidType.getVoidType());
        builtInFunction(scope, "getString", new ArrayList<>(), StringType.getStringType());
        builtInFunction(scope, "getInt", new ArrayList<>(), IntType.getIntType());
        builtInFunction(scope, "toString", Collections.singletonList(new VarEntity("i", IntType.getIntType())), StringType.getStringType());
        ClassEntity string = new ClassEntity("string", new ClassType("string"), scope);
        builtInFunction(string.getScope(), "length", new ArrayList<>(), IntType.getIntType());
        builtInFunction(string.getScope(), "substring", Arrays.asList(new VarEntity("left", IntType.getIntType()), new VarEntity("right", IntType.getIntType())), StringType.getStringType());
        builtInFunction(string.getScope(), "parseInt", new ArrayList<>(), IntType.getIntType());
        builtInFunction(string.getScope(), "ord", Collections.singletonList(new VarEntity("pos", IntType.getIntType())), IntType.getIntType());
        scope.put("string", "@Cstring", string);
        ClassEntity array = new ClassEntity("array", new ClassType("string"), scope);
        builtInFunction(array.getScope(), "size", new ArrayList<>(), IntType.getIntType());
        scope.put("array", "@Carray", array);
    }

    private void builtInFunction(Scope scope1, String name, List<VarEntity> parameters, Type returnType)
    {
        FuncEntity entity = new FuncEntity("@F" + name, new FuncType(name));
        entity.setBuiltIn(true);
        entity.setParameters(parameters);
        entity.setReturnType(returnType);
        entity.setMember(!scope1.isTop());
        scope1.put(name, "@F" + name, entity);
    }

    private void checkMain()
    {
        FuncEntity entity = (FuncEntity) scope.get("@Fmain");
        if (entity == null) throw new SemanticError("\"main\" function not found");
        if (!(entity.getReturnType() instanceof IntType)) throw new SemanticError("\"main\" function's return type should be int");
        if (!entity.getParameters().isEmpty()) throw new SemanticError("\"main\" function's parameters should be null");
    }

    public Scope getScope()
    {
        return scope;
    }
}
