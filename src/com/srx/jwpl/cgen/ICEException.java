package com.srx.jwpl.cgen;

public class ICEException extends RuntimeException
{
  public ICEException(String format, Object... args)
  {
    super(String.format(format, args));
  }

  public ICEException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
