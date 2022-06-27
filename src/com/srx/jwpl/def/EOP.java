package com.srx.jwpl.def;

public enum EOP
{
  NOP     (0x00),
  MOV     (0x01),
  PUSHK   (0x02),
  PUSHI   (0x03),
  CASTI   (0x04),
  RET     (0x05),
  ;

  public final int id;

  EOP(int id)
  {
    this.id = id;
  }
}
