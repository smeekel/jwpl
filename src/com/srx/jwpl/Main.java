package com.srx.jwpl;

import com.srx.jwpl.antlr.WPLLexer;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.cgen.ModuleGen;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;


public class Main
{
  public static void main(String[] args)
  {
    try
    {
      FileInputStream fin = new FileInputStream("test/input.wpl");
      WPLLexer          lexer;
      WPLParser         parser;
      CommonTokenStream tokens;

      lexer   = new WPLLexer(CharStreams.fromStream(fin, StandardCharsets.UTF_8));
      lexer.removeErrorListeners();
      lexer.addErrorListener(ErrorListener.INSTANCE);

      tokens  = new CommonTokenStream(lexer);
      parser  = new WPLParser(tokens);
      parser.removeErrorListeners();
      parser.addErrorListener(ErrorListener.INSTANCE);

      ModuleGen gen = new ModuleGen();
      gen.visit(parser.module());

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

}
