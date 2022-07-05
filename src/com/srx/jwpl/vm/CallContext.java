package com.srx.jwpl.vm;

public class CallContext
{
  public VirtualMachine  vm;
  public StackFrame      stack;
  public int             cvals;
  public int             rvals;

  public CallContext(VirtualMachine vm, int cvals, int rvals)
  {
    this.vm     = vm;
    this.stack  = vm.getStack();
    this.cvals  = cvals;
    this.rvals  = rvals;
  }

}
