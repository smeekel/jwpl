package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLBaseVisitor;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.def.Def;
import com.srx.jwpl.def.DefFlags;
import com.srx.jwpl.def.EDefType;

public class ModuleGen extends WPLBaseVisitor<Object>
{
  private ICGenErrorListener  error_listener;
  protected Def ctx_root;
  protected Def ctx_active;


  public ModuleGen()
  {
    ctx_root    = new Def();
    ctx_active  = ctx_root;
  }

  @Override
  public Object visitModule(WPLParser.ModuleContext ctx)
  {
    ctx_active = ctx_root;
    ctx_active.type   = EDefType.MODULE;
    ctx_active.flags  = DefFlags.NONE;

    return super.visitModule(ctx);
  }

  @Override
  public Object visitImportPartsStatement(WPLParser.ImportPartsStatementContext ctx)
  {
    for(final WPLParser.IdentifierContext ident : ctx.identList().identifier() )
    {
      //System.out.printf("> [%s] <- [%s]\n", ident.getText(), ctx.ConstString().getText());

      Def imp = new Def();
      imp.type  = EDefType.ANY;
      imp.flags = DefFlags.F_IMPORT;
      imp.value = ident.getText();
      imp.setAux("module", ctx.ConstString().getText());
    }

    return null;
  }

  @Override
  public Object visitImportWholeStatement(WPLParser.ImportWholeStatementContext ctx)
  {
    super.visitImportWholeStatement(ctx);
    //ctx.identifier().getText()
    return null;
  }
}
