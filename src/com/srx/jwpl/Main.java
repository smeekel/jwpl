package com.srx.jwpl;

import com.srx.jwpl.cgen.Compiler;
import com.srx.jwpl.vm.VirtualMachine;
import com.srx.jwpl.vm.module.Flask;


public class Main
{
  public static void main(String[] args)
  {
    try
    {
      Compiler compiler = new Compiler();
      Flask flask;

      flask = compiler.compile("test/input.wpl");
      compiler.defDump();
      compiler.getMessages().print();

      if( !compiler.getMessages().hasErrors() )
      {
        VirtualMachine vm = new VirtualMachine();
        vm.addExec(flask);
        System.out.print("\n");
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

  }

}
