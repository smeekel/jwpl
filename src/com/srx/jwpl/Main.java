package com.srx.jwpl;

import com.srx.jwpl.cgen.Compiler;


public class Main
{
  public static void main(String[] args)
  {
    try
    {
      Compiler compiler = new Compiler();

      compiler.compile("test/input.wpl");
      compiler.defDump();
      compiler.getMessages().print();

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

}
