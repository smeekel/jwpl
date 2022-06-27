package com.srx.jwpl.cgen;

import com.srx.jwpl.ErrorListener;
import com.srx.jwpl.antlr.WPLBaseVisitor;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.def.*;

public class ModuleGen extends WPLBaseVisitor<Object>
{
  protected ICGenMessageListener msgListener;
  protected Def   def_root;
  protected Def   def_active;
  protected Scope scope;

  public ModuleGen()
  {
    def_root    = null;
    def_active  = null;
    scope       = null;
  }

  @Override
  public Object visitModule(WPLParser.ModuleContext ctx)
  {
    def_root = new Def();
    def_active        = def_root;
    def_active.type   = EDefType.CLASS;
    def_active.flags  = DefFlags.F_PUBLIC;

    scope = new Scope();

    return super.visitModule(ctx);
  }

  @Override
  public Object visitClassDefinition(WPLParser.ClassDefinitionContext ctx)
  {
    String className = ctx.IDENT().getText();
    Def classDef;

    classDef = scope.findImmediate(className);
    if( classDef!=null )
    {
      error(ctx.IDENT().getSymbol().getLine(), "Duplicate class/fn name found in local scope: '%s'", className);
      return null;
    }

    classDef = new Def();
    classDef.type   = EDefType.CLASS;
    classDef.flags  = DefFlags.F_PROTECTED;
    classDef.value  = className;
    classDef.index  = def_root.generateObjSlot();
    defPush(classDef);

    super.visitClassDefinition(ctx);

    emit(EOP.RET, 0);
    defPop();
    return null;
  }

  @Override
  public Object visitVisibility(WPLParser.VisibilityContext ctx)
  {
    assert def_active.type == EDefType.CLASS || def_active.type == EDefType.VAR ;

    def_active.flags &= ~DefFlags.MASK_VISIBILITY;

    if( ctx.PRIVATE()!=null  )
    def_active.flags |= DefFlags.F_PRIVATE ;
    else if( ctx.PROTECTED()!=null )
      def_active.flags |= DefFlags.F_PROTECTED ;
    else if( ctx.PUBLIC()!=null )
      def_active.flags |= DefFlags.F_PUBLIC ;

    return super.visitVisibility(ctx);
  }

  @Override
  public Object visitParameterDefinition(WPLParser.ParameterDefinitionContext ctx)
  {
    assert def_active.type == EDefType.CLASS ;

    String  name = ctx.IDENT().getText();
    Def     param;

    param = scope.findImmediate(name);
    if( param!=null )
    {
      error(ctx.IDENT().getSymbol().getLine(), "Duplicate parameter name found in local scope: '%s'", name);
      return null;
    }

    param = new Def();
    param.type  = EDefType.VAR;
    param.flags = DefFlags.F_PARAM | DefFlags.F_PRIVATE;
    param.value = name;
    param.index = def_active.generateVarSlot();

    if( ctx.CONST()!=null )
      param.flags |= DefFlags.F_CONST ;


    defAdd(param);
    scope.add(param);

    return null;
  }


  @Override
  public Object visitVariableDefinition(WPLParser.VariableDefinitionContext ctx)
  {
    String name = ctx.IDENT().getText();
    Def var;

    var = scope.findImmediate(name);
    if( var!=null )
    {
      error(ctx.IDENT().getSymbol().getLine(), "Duplicate variable name found in local scope: '%s'", name);
      return null;
    }

    var = new Def();
    var.type  = EDefType.VAR;
    var.flags = DefFlags.F_PRIVATE;
    var.value = name;
    var.index = def_active.generateVarSlot();

    if( ctx.CONST()!=null )
      var.flags |= DefFlags.F_CONST;

    defPush(var);
    defPop();

    if( ctx.expression()!=null )
    {
      super.visitVariableDefinition(ctx);
      emit(EOP.MOV, var.index);
    }

    return null;
  }


  //<editor-fold desc="Expressions">
  @Override
  public Object visitNumericLiteral(WPLParser.NumericLiteralContext ctx)
  {
    if( ctx.DecimalLiteral() != null )
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
        int index = defMakeConst(raw);
        emit(EOP.PUSHK, index);
        emit(EOP.CASTI);
      }
    }

    return null;
  }

  @Override
  public Object visitStringLiteral(WPLParser.StringLiteralContext ctx)
  {
    String  value = ctx.getText();
    int     index;

    value = value.substring(1, value.length()-1);
    index = defMakeConst(value);
    emit(EOP.PUSHK, index);

    return null;
  }

//</editor-fold>





  //<editor-fold desc="Def / Scope Helpers">
  protected void defPush(Def context)
  {
    def_active.addChild(context);
    def_active = context;
    scope.push();
  }

  protected void defPop()
  {
    assert def_active!=null ;
    assert def_active.parent!=null ;

    scope.pop();
    def_active = def_active.parent;
  }

  protected void defAdd(Def context)
  {
    def_active.addChild(context);
  }

  protected int defMakeConst(String value)
  {
    Def konst = def_root.find_const(value);

    if( konst==null )
    {
      konst = new Def();
      konst.type = EDefType.CONST;
      konst.value = value;
      konst.index = def_root.generateConstSlot();
      def_root.addChild(konst);
    }

    return konst.index;
  }

  public Def getRootDef()
  {
    return def_root;
  }

  //</editor-fold>

  //<editor-fold desc="Opcode emit helpers">
  protected void emit(EOP opcode, Integer... values)
  {
    assert def_active.type == EDefType.CLASS ;

    def_active.emit(new OP(opcode, values));
  }
  //</editor-fold>

  //<editor-fold desc="Error handling">
  protected void error(int line, String format, Object ...args)
  {
    emitMessage(EMessageLevel.ERROR, String.format(format, args), line);
  }

  protected void emitMessage(EMessageLevel level, String msg, int line)
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
