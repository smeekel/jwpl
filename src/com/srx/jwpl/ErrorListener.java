package com.srx.jwpl;

import com.srx.jwpl.cgen.EMessageLevel;
import com.srx.jwpl.cgen.ICGenMessageListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class ErrorListener extends BaseErrorListener implements ICGenMessageListener
{
  public static final ErrorListener INSTANCE = new ErrorListener();
  public List<Message>  messages = new LinkedList<>();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
  {
    Message obj = new Message();
    obj.level = EMessageLevel.ERROR;
    obj.msg   = msg;
    obj.line  = line;
    obj.col   = charPositionInLine;
    messages.add(obj);
  }

  @Override
  public void onMessage(@NotNull Message msg)
  {
    /*
    if( msg.level==EMessageLevel.ICE )
    {
      throw new RuntimeException("Internal compiler error: " + msg.msg);
    }
    */

    messages.add(msg);
  }

  protected final static String TERM_RESET    = "\u001B[0m";
  protected final static String TERM_RED      = "\u001B[31m";
  protected final static String TERM_YELLOW   = "\u001B[33m";
  protected final static String TERM_MAGENTA  = "\u001B[35m";
  protected final static String TERM_GREEN    = "\u001B[32m";
  protected final static String TERM_RED_BG   = "\u001B[41;1m";
  

  public boolean hasErrors()
  {
    return messages
      .stream()
      .map( msg -> msg.level==EMessageLevel.ERROR ? 1 : 0 )
      .reduce(0, Integer::sum)
      != 0
      ;
  }

  public void printMessages()
  {
    int errorCount = 0;
    int warnCount  = 0;

    for( Message m : messages )
    {
      if( m.level==EMessageLevel.ERROR || m.level==EMessageLevel.FATAL || m.level==EMessageLevel.ICE )
        errorCount++;
      else if( m.level==EMessageLevel.WARN )
        warnCount++;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("============ \u001b[36mMessages\u001B[0m ============\n");

    for( Message m : messages )
    {
      String prefix = levelToPrefix(m.level);

      sb.append(String.format(
        "[%s%5s%s:%05d]: \u001B[37m%s\u001B[0m\n",
        prefix,
        m.getLevelString(),
        TERM_RESET,
        m.line,
        m.msg
      ));
    }

    sb.append("==================================\n");
    String strErrorCount    = "\u001b[32m0\u001b[0m";
    String strWarningCount  = strErrorCount;

    if( errorCount>0 )
      strErrorCount = String.format("\u001b[31m%s\u001b[0m", errorCount);
    if( warnCount>0 )
      strWarningCount = String.format("\u001b[33m%s\u001b[0m", warnCount);

    sb.append(String.format("> \u001B[36mError(s)\u001B[0m:   %s\n> \u001B[36mWarning(s)\u001B[0m: %s\n", strErrorCount, strWarningCount));

    System.out.println(sb);
  }

  protected String levelToPrefix(EMessageLevel level)
  {
    return switch( level )
    {
      case INFO   -> TERM_GREEN;
      case WARN   -> TERM_YELLOW;
      case ERROR  -> TERM_RED;
      case FATAL  -> TERM_RED;
      case ICE    -> TERM_RED_BG;
    };
  }
}
