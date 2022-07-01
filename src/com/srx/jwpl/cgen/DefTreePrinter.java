package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.EVarFlags;
import com.srx.jwpl.vm.module.OP;

import java.util.LinkedList;
import java.util.List;

public class DefTreePrinter
{
  protected StringBuilder sb = new StringBuilder();

  public static void print(Scope tree)
  {
    DefTreePrinter printer = new DefTreePrinter();

    if( tree==null ) return;

    printer.sb.append("-------------- \u001b[36mDEFs\u001B[0m --------------\n");
    printer.print_def(tree.getRoot(), 0);
    printer.sb.append("----------------------------------\n");

    System.out.println(printer);
  }

  protected void print_def(Scope.Def def, int level)
  {
    final String indent = "  ".repeat(level);
    String        defType;
    String        slot;
    List<String>  flags;


    defType = switch( def.type )
    {
      case FLASK  -> "FLASK";
      case VAR    -> "..VAR";
      case PARAM  -> "PARAM";
    };


    flags = new LinkedList<>();
    if( def.flags.contains(EVarFlags.F_CONST) )
      flags.add("CONST");
    if( def.flags.contains(EVarFlags.F_PRIVATE) )
      flags.add("PRI");
    if( def.flags.contains(EVarFlags.F_PROTECTED) )
      flags.add("PRO");
    if( def.flags.contains(EVarFlags.F_PUBLIC) )
      flags.add("PUB");
    if( def.flags.contains(EVarFlags.F_STATIC) )
      flags.add("+");


    slot = "";
    if( def.index != null )
      slot = String.format("%04d ", def.index);


    sb.append(String.format(
      "%s\u001b[34m%5s\u001B[0m %s'\u001b[35m%s\u001B[0m' \u001b[32m%s\u001B[0m\n",
      indent,
      defType,
      slot,
      def.name!=null ? def.name : "",
      String.join(",", flags)
    ));

    print_consts  (def, level);
    print_children(def, level);
    print_ops     (def, level);
  }

  protected void print_consts(Scope.Def def, int level)
  {
    final String indent  = "  ".repeat(level+1);

    if( def.consts==null ) return;

    for( int i=0 ; i<def.consts.size() ; i++ )
    {
      sb.append(String.format(
        "%s\u001b[34m%5s\u001B[0m %04d '\u001b[35m%s\u001B[0m'\n",
        indent,
        "CONST",
        i,
        def.consts.get(i)
      ));
    }
  }

  protected void print_children(Scope.Def def, int level)
  {
    if( def.children!=null )
    {
      for( Scope.Def child : def.children.values() )
      {
        if( child.type!= Scope.EDefTypes.FLASK )
          print_def(child, level + 1);
      }
      for( Scope.Def child : def.children.values() )
      {
        if( child.type== Scope.EDefTypes.FLASK )
          print_def(child, level + 1);
      }
    }
  }

  protected void print_ops(Scope.Def def, int level)
  {
    final String titleIndent  = "  ".repeat(level+1);
    final String indent       = titleIndent + "  ";

    if( def.ops==null || def.ops.size()<1 )
      return;

    sb.append(titleIndent);
    sb.append("\u001b[34mOPCODES\u001b[0m\n");
    for( OP opcode : def.ops )
    {
      String name = "\u001b[36m" + String.format("%-8s", opcode.op.toString()) + "\u001b[0m";

      switch( opcode.op )
      {
        case MOV:
        case PUSH:
        case CALL:
        case PUSHTHIS:
        case RET:
          sb.append(String.format(
            "%s%s %d\n",
            indent,
            name,
            opcode.a
          ));
          break;

        case GETTK:
          sb.append(String.format(
            "%s%s %d %d ; this(%d)['%s']\n",
            indent,
            name,
            opcode.a,
            opcode.b,
            opcode.a,
            constLookup(def, opcode.b)
          ));
          break;

        case PUSHK:
          sb.append(String.format(
            "%s%s %d ; '%s'\n",
            indent,
            name,
            opcode.a,
            constLookup(def, opcode.a)
          ));
          break;

        case PUSHI:
          sb.append(String.format(
            "%s%s #%d\n",
            indent,
            name,
            opcode.a
          ));
          break;

        default:
          sb.append(String.format(
            "%s%s\n",
            indent,
            name
          ));
          break;
      }
    }
  }

  protected String constLookup(Scope.Def def, int index)
  {
    while( def!=null && def.consts==null )
      def = def.parent;

    if( def==null || def.consts.size()<index )
      return null;

    return def.consts.get(index);
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }
}
