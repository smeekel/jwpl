package com.srx.jwpl.cgen;

import org.jetbrains.annotations.NotNull;

public interface ICGenMessageListener
{
  class Message
  {
    public EMessageLevel level;
    public String msg;
    public int    line;
    public int    col;

    public String getLevelString()
    {
      return switch( level )
      {
        case INFO   -> "INFO";
        case WARN   -> "WARN";
        case ERROR  -> "ERR";
        case FATAL  -> "FATAL";
        case ICE    -> "ICE";
      };
    }
  }

  void onMessage(@NotNull Message msg);
}
