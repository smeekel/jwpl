package com.srx.jwpl.def;

public enum EOP
{
  NOP   (0x00),
  PUSH  (0x01),
  POP   (0x02)
  ;

  public final int id;

  EOP(int id)
  {
    this.id = id;
  }
}
