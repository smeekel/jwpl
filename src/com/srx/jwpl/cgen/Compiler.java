package com.srx.jwpl.cgen;

import com.srx.jwpl.ErrorListener;
import com.srx.jwpl.antlr.WPLLexer;
import com.srx.jwpl.antlr.WPLParser;
import com.srx.jwpl.vm.module.Flask;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Compiler
{
  protected ErrorListener msgListener;


  public Compiler()
  {
    msgListener = new ErrorListener();
  }

  public ErrorListener getMessages()
  {
    return msgListener;
  }

  public boolean didSucceed()
  {
    return msgListener!=null && !msgListener.hasErrors();
  }

  public Flask compile(String filename) throws IOException
  {
    FileInputStream fin = new FileInputStream(filename);
    return compile(fin);
  }

  public Flask compile(InputStream in) throws IOException
  {
    WPLLexer          lexer;
    WPLParser         parser;
    CommonTokenStream tokens;
    TypeGen           phase1;
    ModuleGen         phase2;


    msgListener.clear();

    lexer   = new WPLLexer(CharStreams.fromStream(in, StandardCharsets.UTF_8));
    lexer.removeErrorListeners();
    lexer.addErrorListener(msgListener);

    tokens  = new CommonTokenStream(lexer);
    parser  = new WPLParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(msgListener);

    phase1 = new TypeGen();
    phase1.addMessageListener(msgListener);
    phase1.visit(parser.module());

    phase2 = new ModuleGen();
    phase2.addMessageListener(msgListener);
    phase2.visit(parser.module());

    //return didSucceed() ? phase2.getModule() : null ;
    return null;
  }

}
