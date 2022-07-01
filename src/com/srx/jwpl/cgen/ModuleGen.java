package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.vm.module.EOP;
import com.srx.jwpl.vm.module.EVarFlags;

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

    def.index = def.parent.allocFlaskSlot();

    defTree.push(def);
      super.visitClassDefinition(ctx);
      emit(EOP.RET);
    defTree.pop();

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

    def.index = def.parent.allocFlaskSlot();

    defTree.push(def);
      super.visitLambdaExpression(ctx);
      emit(EOP.RET);
    defTree.pop();

    emit(EOP.PUSHFN, def.index);

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
    def.index = def.parent.allocStackSlot();

    if( ctx.expression()!=null )
    {
      super.visitVariableDefinition(ctx);
      emit(EOP.MOV, def.index);
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
    emit(EOP.CALL, ctx.callArguments().argumentList().expr.size());

    return null;
  }

  @Override
  public Object visitAssignmentExpr(WPLParser.AssignmentExprContext ctx)
  {
    super.visitAssignmentExpr(ctx);

    emit(EOP.ASGN);

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

    if( def.type==Scope.EDefTypes.VAR )
    {
      emit(EOP.PUSHTHIS);
      emit(EOP.PUSHK, defTree.emitConst(def.name));
      emit(EOP.GET);
    }
    else if( def.type==Scope.EDefTypes.PARAM )
    {
      emit(EOP.PUSH, def.index);
    }
    else if( def.type==Scope.EDefTypes.FLASK )
    {
      emit(EOP.NOP);
      //emit(EOP.PUSHFN, def.index);
      //throw new ICEException("Missing branch");
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
    emit(EOP.PUSHK, defTree.emitConst(ctx.getText()));
    return null;
  }
}
