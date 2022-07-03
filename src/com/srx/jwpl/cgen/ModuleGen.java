package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.vm.module.EOP;
import com.srx.jwpl.vm.module.EVarFlags;
import com.srx.jwpl.vm.module.OP;
import org.antlr.v4.runtime.tree.ParseTree;

public class ModuleGen extends BaseGenerator<Object>
{
  public ModuleGen(Scope defTree)
  {
    this.defTree = defTree;
  }

  public Scope getDefTree()
  {
    return defTree;
  }

  @Override
  public Object visitModule(WPLParser.ModuleContext ctx)
  {
    Scope.Def root = defTree.getRoot();
    root.flags.add(EVarFlags.F_PUBLIC);
    root.flags.add(EVarFlags.F_EXPORT);

    addStub("print");
    addStub("import");

    super.visitModule(ctx);

    emit(EOP.RET);

    return null;
  }

  @Override
  public Object visitClassDefinition(WPLParser.ClassDefinitionContext ctx)
  {
    String    name  = ctx.IDENT().getText();
    Scope.Def def   = defTree.findLocal(name);

    if( def.type != Scope.EDefTypes.FLASK )
      throw new ICEException("Type does not match named scope element '%s':flask", name);

    defTree.push(def);
      super.visitClassDefinition(ctx);
      emit(EOP.RET);
    defTree.pop();

    return null;
  }

  @Override
  public Object visitReturnStatement(WPLParser.ReturnStatementContext ctx)
  {
    super.visitReturnStatement(ctx);

    emit(EOP.RET, 1);

    return null;
  }

  @Override
  public Object visitLambdaExpression(WPLParser.LambdaExpressionContext ctx)
  {
    String    name = ctx.internalName;
    Scope.Def def;

    if( name==null )
      throw new ICEException("Unnamed lambda express found");
    if( !name.startsWith("$") )
      throw new ICEException("Expected generate lambda name, found '%s' instead", name);
    def = defTree.findLocal(name);
    if( def.type != Scope.EDefTypes.FLASK )
      throw new ICEException("Type does not match named scope element '%s':flask", name);

    defTree.push(def);
      super.visitLambdaExpression(ctx);
      emit(EOP.RET);
    defTree.pop();

    //throw new ICEException("Missing lambda branch");
    //emit(EOP.PUSHFN, def.index);
    emit(EOP.GETTK, 0, defTree.emitConst(def.name));

    return null;
  }

  @Override
  public Object visitVisibility(WPLParser.VisibilityContext ctx)
  {
    Scope.Def active  = defTree.getActive();
    boolean   any     = false;

    if( ctx.PRIVATE()!=null )
    {
      active.flags.add(EVarFlags.F_PRIVATE);
      any = true;
    }
    else if( ctx.PROTECTED()!=null )
    {
      active.flags.add(EVarFlags.F_PROTECTED);
      any = true;
    }
    else if( ctx.PUBLIC()!=null )
    {
      active.flags.add(EVarFlags.F_PUBLIC);
      any = true;
    }

    //
    // Marking a member with a visibility modifier promotes it to a full
    // member, thus making it static. It will exist on the heap instead of the
    // stack (as a temporary)
    //
    if( any )
      active.flags.add(EVarFlags.F_STATIC);

    return null;
  }

  @Override
  public Object visitParameterDefinition(WPLParser.ParameterDefinitionContext ctx)
  {
    String    name  = ctx.IDENT().getText();
    Scope.Def def   = defTree.findLocal(name);

    if( def.type != Scope.EDefTypes.PARAM )
      throw new ICEException("Type does not match named scope element '%s':param", name);

    //
    // Const flag already captured in phase 1
    //
    def.index = def.parent.allocStackSlot();

    return super.visitParameterDefinition(ctx);
  }

  @Override
  public Object visitVariableDefinition(WPLParser.VariableDefinitionContext ctx)
  {
    String    name  = ctx.IDENT().getText();
    Scope.Def def   = defTree.findLocal(name);

    if( def.type != Scope.EDefTypes.VAR )
      throw new ICEException("Type does not match named scope element '%s':var", name);

    //
    // Const flag already captured in phase 1
    //
    if( ctx.expression()!=null )
    {
      emit(EOP.GETTK, 0, defTree.emitConst(def.name));
      super.visitVariableDefinition(ctx);
      emit(EOP.ASGN);
      emit(EOP.POP);
    }

    return null;
  }

  @Override
  public Object visitNakedExpression(WPLParser.NakedExpressionContext ctx)
  {
    super.visitNakedExpression(ctx);
    emit(EOP.POP);
    return null;
  }

  @Override
  public Object visitCallExpr(WPLParser.CallExprContext ctx)
  {
    super.visitCallExpr(ctx);

    int size = 0;
    if( ctx.callArguments().argumentList()!=null && ctx.callArguments().argumentList().expr!=null )
      size = ctx.callArguments().argumentList().expr.size();

    emit(EOP.CALL, size, 1);

    return null;
  }

  @Override
  public Object visitIfStatement(WPLParser.IfStatementContext ctx)
  {
    OP labelFalse   = genLabel();
    OP labelTrue    = genLabel();


    super.visit(ctx.getChild(1)); // expression
    emit(EOP.BF).setAux(labelFalse);

    super.visit(ctx.getChild(2)); // true-body

    if( ctx.ELSE()!=null  )
    {
      emit(EOP.B).setAux(labelTrue);
      emit(labelFalse);

      super.visit(ctx.getChild(4));

      emit(labelTrue);
    }
    else
    {
      emit(labelFalse);
    }

    return null;
  }

  @Override
  public Object visitAssignmentExpr(WPLParser.AssignmentExprContext ctx)
  {
    boolean memberAssign = ctx.getChild(0).getClass().isAssignableFrom(WPLParser.MemberAccessExprContext.class);

    if( memberAssign )
    {
      ParseTree memberAccessExpr = ctx.getChild(0);

      this.visit(memberAccessExpr.getChild(0));

      emit(EOP.PUSHK, getDefTree().emitConst(memberAccessExpr.getChild(2).getText()));

      this.visit(ctx.getChild(2));
      emit(EOP.PUT);
    }
    else
    {
      super.visitAssignmentExpr(ctx);
      emit(EOP.ASGN);
    }

    return null;
  }

  @Override
  public Object visitCoalescingExpr(WPLParser.CoalescingExprContext ctx)
  {
    super.visitCoalescingExpr(ctx);
    emit(EOP.COAL);
    return null;
  }

  @Override
  public Object visitSafeMemberAccessExpr(WPLParser.SafeMemberAccessExprContext ctx)
  {
    String name = ctx.IDENT().getText();

    super.visitSafeMemberAccessExpr(ctx);
    emit(EOP.PUSHK, getDefTree().emitConst(name));
    emit(EOP.SGET);

    return null;
  }

  @Override
  public Object visitMemberAccessExpr(WPLParser.MemberAccessExprContext ctx)
  {
    String name = ctx.IDENT().getText();

    super.visitMemberAccessExpr(ctx);
    emit(EOP.PUSHK, getDefTree().emitConst(name));
    emit(EOP.GET);

    return null;
  }



  @Override
  public Object visitIdentExpr(WPLParser.IdentExprContext ctx)
  {
    String    name  = ctx.IDENT().getText();
    Scope.Def def   = defTree.findFirst(name);

    if( def==null )
    {
      error(ctx.IDENT().getSymbol().getLine(), "Undefined symbol '%s'", name);
      return null;
    }

    if( def.type==Scope.EDefTypes.VAR || def.type==Scope.EDefTypes.FLASK )
    {
      int delta = defTree.calculateParentDelta(defTree.getActive(), def);

      emit(EOP.GETTK, delta, defTree.emitConst(def.name));
    }
    else if( def.type==Scope.EDefTypes.PARAM )
    {
      if( def.parent!=defTree.getActive() )
      {
        error
        (
          ctx.IDENT().getSymbol().getLine(),
          "Cannot access parameter '%s' out of current scope",
          name
        );
      }

      emit(EOP.PUSH, def.index);
    }

    return super.visitIdentExpr(ctx);
  }

  @Override
  public Object visitDecimalLiteral(WPLParser.DecimalLiteralContext ctx)
  {
    String  raw   = ctx.getText();
    long    value = Long.parseLong(raw);


    if( Long.compareUnsigned(value, 0xFFFFFFL)<=0 )
    {
      // Value is less then the 24bit available in the opcode
      // stuff it into a PUSH_I instruction
      emit(EOP.PUSHI, (int)value);
    }
    else
    {
      int index = defTree.emitConst(raw);
      emit(EOP.PUSHK, index);
      emit(EOP.CASTI);
    }

    return null;
  }

  @Override
  public Object visitStringLiteral(WPLParser.StringLiteralContext ctx)
  {
    String value = ctx.getText();

    value = value.substring(1, value.length()-1);
    value = value.replace("\\n", "\n");

    emit(EOP.PUSHK, defTree.emitConst(value));
    return null;
  }

  @Override
  public Object visitNoneLiteral(WPLParser.NoneLiteralContext ctx)
  {
    emit(EOP.PUSHNULL);
    return null;
  }

  @Override
  public Object visitThisExpr(WPLParser.ThisExprContext ctx)
  {
    emit(EOP.PUSHTHIS);
    return super.visitThisExpr(ctx);
  }
}
