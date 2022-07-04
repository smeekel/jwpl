package com.srx.jwpl;

import com.srx.jwpl.cgen.Compiler;
import com.srx.jwpl.elf.ELF;
import com.srx.jwpl.vm.VirtualMachine;
import com.srx.jwpl.vm.module.Flask;

import java.io.FileNotFoundException;
import java.io.IOException;


public class Main
{
  public static void main(String[] args)
  {
    //compile();
    eflTest();
  }

  private static void eflTest()
  {
    try
    {
      ELF.test();
    }
    catch( IOException e )
    {
      throw new RuntimeException(e);
    }
  }

  protected static void compile()
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
      }
      System.out.print("\n");
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
}
