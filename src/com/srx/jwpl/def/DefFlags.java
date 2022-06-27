package com.srx.jwpl.def;

public class DefFlags
{
  public static final int NONE          = 0;
  public static final int F_PUBLIC      = 0x0001;
  public static final int F_PROTECTED   = 0x0002;
  public static final int F_PRIVATE     = 0x0004;
  public static final int F_PARAM       = 0x0008;
  public static final int F_CONST       = 0x0010;

  public static final int MASK_VISIBILITY = F_PUBLIC | F_PROTECTED | F_PRIVATE;
}
