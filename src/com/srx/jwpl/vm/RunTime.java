package com.srx.jwpl.vm;

import com.srx.jwpl.vm.module.EVariableTypes;
import com.srx.jwpl.vm.module.Variable;

public class RunTime
{
  static public void extPrint(CallContext ctx)
  {
    Variable var;
    String out;

    for( int i=ctx.cvals-1 ; i>=0 ; i-- )
    {
      var = ctx.stack.get(i);
      out = ctx.vm.varToString(var);
      System.out.print(out);
      if( i>0 ) System.out.print(" ");
    }
    System.out.print("\n");

    for( int i=0 ; i< ctx.cvals+1 ; i++ )
      ctx.stack.pop();

    for( int i=0 ; i<ctx.rvals ; i++ )
      ctx.stack.push(new Variable(EVariableTypes.NONE));

  }

  static public void extStub(CallContext ctx)
  {
    for( int i=0 ; i< ctx.cvals+1 ; i++ )
      ctx.stack.pop();
    for( int i=0 ; i<ctx.rvals ; i++ )
      ctx.stack.push(new Variable(EVariableTypes.NONE));
  }
}
