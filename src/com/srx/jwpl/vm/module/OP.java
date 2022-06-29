package com.srx.jwpl.vm.module;

public class OP
{
  public EOP op;
  public int a;
  public int b;
  public int c;

  public OP()
  {
  }

  public OP(EOP op, Integer... values)
  {
    this.op = op;
    this.a  = values.length>0 ? values[0] : 0 ;
    this.b  = values.length>1 ? values[1] : 0 ;
    this.c  = values.length>2 ? values[2] : 0 ;
  }
}
