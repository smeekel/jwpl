package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLBaseVisitor;
import com.srx.jwpl.vm.module.EOP;
import com.srx.jwpl.vm.module.OP;

import static com.srx.jwpl.vm.module.EVarFlags.F_GLOBAL;

public abstract class BaseGenerator<T> extends WPLBaseVisitor<T>
{
  protected ErrorListener msgListener;
  protected Scope         defTree;
  protected int           labelIndex;

  public void addMessageListener(ErrorListener msgListener)
  {
    this.msgListener = msgListener;
  }

  protected void emitMessage(EMessageLevel level, String msg, int line, int col)
  {
    if( msgListener!=null )
    {
      ICGenMessageListener.Message obj = new ICGenMessageListener.Message();
      obj.level = level;
      obj.msg   = msg;
      obj.line  = line;
      obj.col   = col;
      msgListener.onMessage(obj);
    }
  }

  protected void error(int line, String format, Object ...args)
  {
    emitMessage(EMessageLevel.ERROR, String.format(format, args), line, 0);
  }

  protected void warn(int line, String format, Object ...args)
  {
    emitMessage(EMessageLevel.WARN, String.format(format, args), line, 0);
  }

  protected OP emit(EOP opcode, Integer... values)
  {
    assert defTree.getActive().type == Scope.EDefTypes.FLASK ;

    OP v = new OP(opcode, values);
    defTree.getActive().ops.add(v);

    return v;
  }

  protected OP genLabel()
  {
    OP label = new OP(EOP.LABEL, ++labelIndex);
    return label;
  }

  protected OP emit(OP opcode)
  {
    assert defTree.getActive().type == Scope.EDefTypes.FLASK ;

    defTree.getActive().ops.add(opcode);

    return opcode;
  }

  protected void addStub(String name)
  {
    Scope.Def def = new Scope.Def(Scope.EDefTypes.FLASK);
    def.name = name;
    def.flags.add(F_GLOBAL);
    defTree.addRoot(def);
  }

}
