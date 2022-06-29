package com.srx.jwpl;

import com.srx.jwpl.antlr.WPLLexer;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.cgen.Baker;
import com.srx.jwpl.cgen.ModuleGen;
import com.srx.jwpl.def.Def;
import com.srx.jwpl.def.DefPrinter;
import com.srx.jwpl.vm.VirtualMachine;
import com.srx.jwpl.vm.module.Flask;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;


public class Main
{
  public static void main(String[] args)
  {
    try
    {
      FileInputStream   fin   = new FileInputStream("test/input.wpl");
      ErrorListener     msgs  = ErrorListener.INSTANCE;
      WPLLexer          lexer;
      WPLParser         parser;
      CommonTokenStream tokens;

      lexer   = new WPLLexer(CharStreams.fromStream(fin, StandardCharsets.UTF_8));
      lexer.removeErrorListeners();
      lexer.addErrorListener(msgs);

      tokens  = new CommonTokenStream(lexer);
      parser  = new WPLParser(tokens);
      parser.removeErrorListeners();
      parser.addErrorListener(msgs);

      ModuleGen gen = new ModuleGen();
      gen.addMessageListener(msgs);
      gen.visit(parser.module());

      DefPrinter.print(gen.getRootDef());
      msgs.printMessages();

      if( !msgs.hasErrors() )
        phase2(gen.getRootDef());
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  protected static void phase2(@NotNull Def rootDef)
  {
    VirtualMachine  vm;
    Flask           mod;

    vm  = new VirtualMachine();
    mod = Baker.bake(rootDef);
    vm.importFlask(mod);

  }

}
