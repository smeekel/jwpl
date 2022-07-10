package com.srx.jwpl;

import com.srx.jwpl.cgen.Compiler;
import com.srx.jwpl.elf.ELF64;
import com.srx.jwpl.elf.WPLELF;
import com.srx.jwpl.vm.VirtualMachine;
import com.srx.jwpl.vm.module.Module;


public class Main
{
  public static void main(String[] args)
  {
    compile();
    //eflTest();
  }

  private static void eflTest()
  {
    try
    {
      Compiler compiler = new Compiler();
      ELF64 elf;
      Module  module;

      module  = compiler.compile("test/input.wpl");
      elf     = new WPLELF(module);
      elf.write("c:/temp/test.elf");
    }
    catch( Exception e )
    {
      throw new RuntimeException(e);
    }
  }

  protected static void compile()
  {
    try
    {
      Compiler compiler = new Compiler();
      Module module;

      module = compiler.compile("test/input.wpl");
      compiler.defDump();
      compiler.getMessages().print();

      if( !compiler.getMessages().hasErrors() )
      {
        VirtualMachine vm = new VirtualMachine();
        vm.addExec(module);
      }
      System.out.print("\n");
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
}
