package com.srx.jwpl.vm.module;

public enum EOP
{
  NOP       (0x00, "nop"),
  MOV       (0x01, "mov"),
  PUSH      (0x02, "push"),
  PUSHK     (0x04, "pushk"),
  PUSHI     (0x05, "pushi"),
  PUSHTHIS  (0x06, "pushthis"),
  PUSHNULL  (0x07, "pushnull"),
  POP       (0x08, "pop"),
  CASTI     (0x09, "casti"),
  CALL      (0x0A, "call"),
  RET       (0x0B, "ret"),
  ADD       (0x0C, "add"),
  INC       (0x0D, "inc"),
  GET       (0x0E, "get"),
  ASGN      (0x0F, "asgn"),
  GETTK     (0x10, "gettk"),
  PUT       (0x11, "put"),
  SGET      (0x12, "sget"),
  COAL      (0x13, "coal"),
  B         (0x14, "b"),
  BF        (0x15, "bf"),
  LABEL     (0x16, "label"),
  XENTER    (0x17, "xenter"),
  XLEAVE    (0x18, "xleave"),
  XTHROW    (0x19, "xthrow"),
  XGET      (0x1A, "xget"),
  ;

  public final int    id;
  public final String mnemonic;

  EOP(int id, String mnemonic)
  {
    this.id       = id;
    this.mnemonic = mnemonic;
  }

  @Override
  public String toString()
  {
    return mnemonic;
  }
}
