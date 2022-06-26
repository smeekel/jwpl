package com.srx.jwpl;

import com.srx.jwpl.cgen.ICGenErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.LinkedList;
import java.util.List;

public class ErrorListener extends BaseErrorListener implements ICGenErrorListener
{
  public static class Node
  {
    private int     line;
    private int     col;
    private String  msg;

    public Node()
    {

    }

    public Node(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
    {
      this.line = line;
      this.col  = charPositionInLine;
      this.msg  = msg;
    }
  }

  public static final ErrorListener INSTANCE = new ErrorListener();
  public List<Node> errors = new LinkedList<>();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
  {
    //super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    errors.add(new Node(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
  }
}
