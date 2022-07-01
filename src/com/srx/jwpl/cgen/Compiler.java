package com.srx.jwpl.cgen;

import com.srx.jwpl.antlr.WPLLexer;
import com.srx.jwpl.antlr.WPLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Compiler
{
  protected ErrorListener msgListener;
  protected Scope defTree;


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

  public Object compile(String filename) throws IOException
  {
    FileInputStream fin = new FileInputStream(filename);
    return compile(fin);
  }

  public Object compile(InputStream in) throws IOException
  {
    WPLLexer          lexer;
    WPLParser         parser;
    CommonTokenStream tokens;
    ParseTree         tree;
    TypeGen           phase1;
    ModuleGen         phase2;


    msgListener.clear();
    defTree = null;

    lexer   = new WPLLexer(CharStreams.fromStream(in, StandardCharsets.UTF_8));
    lexer.removeErrorListeners();
    lexer.addErrorListener(msgListener);

    tokens  = new CommonTokenStream(lexer);
    parser  = new WPLParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(msgListener);
    tree = parser.module();

    phase1 = new TypeGen();
    phase1.addMessageListener(msgListener);
    phase1.visit(tree);

    if( !msgListener.hasErrors() )
    {
      phase2 = new ModuleGen(phase1.getDefTree());
      phase2.addMessageListener(msgListener);
      phase2.visit(tree);
      defTree = phase2.getDefTree();
    }

    return null;
  }

  public void defDump()
  {
    DefTreePrinter.print(defTree);
  }
}
