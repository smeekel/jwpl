package com.srx.jwpl.cgen;

import com.srx.jwpl.ErrorListener;
import com.srx.jwpl.antlr.WPLBaseVisitor;

public abstract class BaseGenerator<T> extends WPLBaseVisitor<T>
{
  protected ErrorListener msgListener;

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
}
