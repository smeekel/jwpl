package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.vm.module.Flask;
import com.srx.jwpl.vm.module.VarFlags;
import com.srx.jwpl.vm.module.Variable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.UUID;

public class TypeGen extends BaseGenerator<Void>
{
  protected LinkedList<Flask> stack;
  protected Flask             active;

  public TypeGen()
  {
    stack   = null;
    active  = null;
  }

  public Flask getRoot()
  {
    return stack.getFirst();
  }

  @Override
  public Void visitModule(WPLParser.ModuleContext ctx)
  {
    stack   = new LinkedList<>();
    active  = new Flask();
    stack.push(active);

    super.visitModule(ctx);
    return null;
  }

  @Override
  public Void visitClassDefinition(WPLParser.ClassDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();

    if( !doesNameExistInCurrentScope(name) )
    {
      Flask flask = new Flask();
      flask.name  = name;
      active.members.add(flask);

      push(flask);
      super.visitClassDefinition(ctx);
      pop();
    }
    else
    {
      error
      (
        ctx.IDENT().getSymbol().getLine(),
        "Flask definition: name '%s' already exists in current scope",
        name
      );
      super.visitClassDefinition(ctx);
    }

    return null;
  }

  @Override
  public Void visitParameterDefinition(WPLParser.ParameterDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();

    if( !doesNameExistInCurrentScope(name)  )
    {
      Variable var = new Variable();
      var.name = name;
      var.flags.add(VarFlags.F_PARAM);
      active.members.add(var);
    }
    else
    {
      error
      (
        ctx.IDENT().getSymbol().getLine(),
        "Parameter definition: name '%s' already exists in current scope",
        name
      );
    }

    return null;
  }

  @Override
  public Void visitVariableDefinition(WPLParser.VariableDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();

    if( !doesNameExistInCurrentScope(name) )
    {
      Variable var = new Variable();
      var.name = name;

      if( ctx.CONST()!=null )
        var.flags.add(VarFlags.F_CONST);

      active.members.add(var);

      super.visitVariableDefinition(ctx);
    }
    else
    {
      error
      (
        ctx.IDENT().getSymbol().getLine(),
        "Variable definition: name '%s' already exists in current scope",
        name
      );
    }

    return null;
  }

  @Override
  public Void visitLambdaExpression(WPLParser.LambdaExpressionContext ctx)
  {
    UUID uuid = UUID.randomUUID();
    ctx.internalName = "$" + uuid.toString();

    Flask flask = new Flask();
    flask.name = ctx.internalName;
    active.members.add(flask);

    push(flask);
    super.visitLambdaExpression(ctx);
    pop();

    return null;
  }


  protected boolean doesNameExistInCurrentScope(@NotNull String name)
  {
    for( Variable var : active.members )
    {
      if( var.name!=null && var.name.equals(name) )
        return true;
    }
    return false;
  }

  protected void push(Flask flask)
  {
    stack.push(flask);
    active = flask;
  }

  protected void pop()
  {
    stack.pop();
    active = stack.getFirst();
  }

}
