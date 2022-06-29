package com.srx.jwpl.def;

public enum EOP
{
  NOP       (0x00),
  MOV       (0x01),
  PUSH      (0x02),
  PUSHFN    (0x03),
  PUSHK     (0x04),
  PUSHI     (0x05),
  PUSHTHIS  (0x06),
  PUSHNULL  (0x07),
  POP       (0x08),
  CASTI     (0x09),
  CALL      (0x0A),
  RET       (0x0B),
  ADD       (0x0C),
  INC       (0x0D),
  GET       (0x0E),
  ;

  public final int id;

  EOP(int id)
  {
    this.id = id;
  }
}
