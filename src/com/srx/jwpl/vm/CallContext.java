package com.srx.jwpl.vm;

public class CallContext
{
  StackFrame stack;
  int cvals;
  int rvals;

  public CallContext()
  {
  }

  public CallContext(StackFrame stack, int cvals, int rvals)
  {
    this.stack = stack;
    this.cvals = cvals;
    this.rvals = rvals;
  }
}
