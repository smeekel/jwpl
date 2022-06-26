package com.srx.jwpl.cgen;

import com.srx.jwpl.ErrorListener;
import com.srx.jwpl.antlr.WPLBaseVisitor;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.def.Def;
import com.srx.jwpl.def.DefFlags;
import com.srx.jwpl.def.EDefType;
import com.srx.jwpl.def.Scope;

import java.text.FieldPosition;
import java.text.MessageFormat;

public class ModuleGen extends WPLBaseVisitor<Object>
{
  private ICGenMessageListener error_listener;
  protected Def   def_root;
  protected Def   def_active;
  protected Scope scope;

  protected boolean pastImports;
  protected ICGenMessageListener msgListener;


  public ModuleGen()
  {
    def_root    = new Def();
    def_active  = def_root;
    scope       = new Scope();
    pastImports = false;
  }

  @Override
  public Object visitModule(WPLParser.ModuleContext ctx)
  {
    def_active        = def_root;
    def_active.type   = EDefType.MODULE;
    def_active.flags  = DefFlags.NONE;

    return super.visitModule(ctx);
  }

  @Override
  public Object visitImportPartsStatement(WPLParser.ImportPartsStatementContext ctx)
  {
    for(final WPLParser.IdentifierContext ident : ctx.identList().identifier() )
    {
      Def imp   = new Def();
      imp.type  = EDefType.ANY;
      imp.flags = DefFlags.F_IMPORT;
      imp.value = ident.getText();
      imp.setAux("module", ctx.ConstString().getText());

      def_active.addChild(imp);
    }

    return null;
  }

  @Override
  public Object visitImportWholeStatement(WPLParser.ImportWholeStatementContext ctx)
  {
    Def imp   = new Def();
    imp.type  = EDefType.MODULE;
    imp.flags = DefFlags.F_IMPORT;
    imp.value = ctx.identifier().getText();
    imp.setAux("module", ctx.ConstString().getText());

    def_active.addChild(imp);

    return null;
  }

  @Override
  public Object visitBlockStatement(WPLParser.BlockStatementContext ctx)
  {
    scope.push();
    super.visitBlockStatement(ctx);
    scope.pop();
    return null;
  }

  @Override
  public Object visitFunctionDefinition(WPLParser.FunctionDefinitionContext ctx)
  {
    String fnName = ctx.identifier().getText();
    Def func;

    if( (func = scope.findImmediate(fnName)) != null )
    {
      error(ctx.identifier().getStart().getLine(), "Unable to create function; duplicate name found in scope [%s]", fnName);
      return null;
    }

    func        = new Def();
    func.value  = ctx.identifier().getText();
    func.type   = EDefType.FN;
    func.flags  = DefFlags.NONE;

    def_push(func);
    scope.add(func);
    scope.push();

    super.visitFunctionDefinition(ctx);

    scope.pop();
    def_pop();

    return null;
  }

  //<editor-fold desc="Def / Scope Helpers">
  protected void def_push(Def context)
  {
    def_active.addChild(context);
    def_active = context;
  }

  protected  void def_pop()
  {
    assert def_active!=null ;
    assert def_active.parent!=null ;

    def_active = def_active.parent;
  }
  //</editor-fold>

  //<editor-fold desc="Error handling">
  protected void error(int line, String format, Object ...args)
  {
    onMessage(EMessageLevel.ERROR, String.format(format, args), line);
  }

  protected void onMessage(EMessageLevel level, String msg, int line)
  {
    if( msgListener!=null )
    {
      ICGenMessageListener.Message obj = new ICGenMessageListener.Message();
      obj.level = level;
      obj.msg   = msg;
      obj.line  = line;
      msgListener.onMessage(obj);
    }
  }

  public void addMessageListener(ErrorListener instance)
  {
    msgListener = instance;
  }
  //</editor-fold>
}
