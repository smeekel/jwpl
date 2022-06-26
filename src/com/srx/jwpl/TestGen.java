package com.srx.jwpl;

import com.srx.jwpl.antlr.WPLBaseVisitor;
import com.srx.jwpl.antlr.WPLParser;
import org.antlr.v4.runtime.tree.ParseTree;

public class TestGen extends WPLBaseVisitor<Object>
{
  @Override
  public Object visit(ParseTree tree)
  {
    System.out.println("visit");
    return super.visit(tree);
  }

  @Override
  public Object visitStatement(WPLParser.StatementContext ctx)
  {
    System.out.println("statement");
    return super.visitStatement(ctx);
  }


  @Override
  public Object visitPostIncrementExpr(WPLParser.PostIncrementExprContext ctx)
  {
    System.out.println("post_incr");
    return super.visitPostIncrementExpr(ctx);
  }

  @Override
  public Object visitPreIncrementExpr(WPLParser.PreIncrementExprContext ctx)
  {
    System.out.println("pre_incr");
    return super.visitPreIncrementExpr(ctx);
  }
}
