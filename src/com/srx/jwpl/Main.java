package com.srx.jwpl;

import com.srx.jwpl.cgen.Compiler;
import com.srx.jwpl.elf.ELF;
import com.srx.jwpl.elf.SHT_note;
import com.srx.jwpl.vm.VirtualMachine;
import com.srx.jwpl.vm.module.Flask;

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
      ELF elf = new ELF();
      SHT_note notes = new SHT_note();

      elf.addSection(notes, ".test.notes");
      notes.value.append("Some test text goes here...");

      elf.write("c:/temp/test.elf");
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
