package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.vm.module.EVarFlags;

import java.util.UUID;

public class TypeGen extends BaseGenerator<Void>
{
  public TypeGen()
  {
    defTree = new Scope();
  }

  public Scope getDefTree()
  {
    return defTree;
  }

  @Override
  public Void visitModule(WPLParser.ModuleContext ctx)
  {
    defTree.getRoot().name = "$module";

    super.visitModule(ctx);
    return null;
  }

  @Override
  public Void visitClassDefinition(WPLParser.ClassDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();

    if( !defTree.doesNameExistInCurrentScope(name) )
    {
      Scope.Def def = new Scope.Def(Scope.EDefTypes.FLASK);
      def.name  = name;

      defTree.push(def);
      super.visitClassDefinition(ctx);
      defTree.pop();
    }
    else
    {
      error
      (
        ctx.IDENT().getSymbol().getLine(),
        "Flask definition: name '%s' already exists in current scope",
        name
      );

      //
      // Need to push a fake Def in order to continue the parse at this point.
      // Without it, all the subsequent defs would end up in the parent def and
      // cause additional incorrect error messages.
      //
      //super.visitClassDefinition(ctx);
    }

    return null;
  }

  @Override
  public Void visitParameterDefinition(WPLParser.ParameterDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();

    if( !defTree.doesNameExistInCurrentScope(name)  )
    {
      Scope.Def def = new Scope.Def(Scope.EDefTypes.PARAM);
      def.name  = name;

      if( ctx.CONST()!=null )
        def.flags.add(EVarFlags.F_CONST);

      defTree.add(def);
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

    if( !defTree.doesNameExistInCurrentScope(name) )
    {
      Scope.Def def = new Scope.Def(Scope.EDefTypes.VAR);
      def.name = name;

      if( ctx.CONST()!=null )
        def.flags.add(EVarFlags.F_CONST);

      defTree.add(def);
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

    super.visitVariableDefinition(ctx);
    return null;
  }

  @Override
  public Void visitLambdaExpression(WPLParser.LambdaExpressionContext ctx)
  {
    UUID uuid = UUID.randomUUID();
    ctx.internalName = "$" + uuid;

    Scope.Def def = new Scope.Def(Scope.EDefTypes.FLASK);
    def.name = ctx.internalName;

    defTree.push(def);
    super.visitLambdaExpression(ctx);
    defTree.pop();

    return null;
  }



}
