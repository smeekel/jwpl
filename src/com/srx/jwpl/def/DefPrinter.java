package com.srx.jwpl.def;

import com.srx.jwpl.vm.module.OP;

import java.util.LinkedList;
import java.util.List;

public class DefPrinter
{
  protected StringBuilder sb = new StringBuilder();

  public static void print(Def root)
  {
    DefPrinter printer = new DefPrinter();

    printer.sb.append("------------ \u001b[36mDEFs\u001B[0m ------------\n");
    printer.print_def(root, 0);
    printer.sb.append("------------------------------\n");

    System.out.println(printer);
  }

  protected void print_def(Def def, int level)
  {
    final String indent = "  ".repeat(level);
    String defType;
    String slot;
    List<String> flags;


    defType = switch( def.type )
    {
      case NONE   -> ".NONE";
      case CLASS  -> "CLASS";
      case VAR    -> "..VAR";
      case CONST  -> "CONST";
    };


    flags = new LinkedList<>();
    if( (def.flags & DefFlags.F_CONST)!=0 )
      flags.add("CONST");
    if( (def.flags & DefFlags.F_PARAM)!=0 )
      flags.add("PARAM");
    if( (def.flags & DefFlags.F_PRIVATE)!=0 )
      flags.add("PRI");
    if( (def.flags & DefFlags.F_PROTECTED)!=0 )
      flags.add("PRO");
    if( (def.flags & DefFlags.F_PUBLIC)!=0 )
      flags.add("PUB");
    if( (def.flags & DefFlags.F_EXTERNAL)!=0 )
      flags.add("EXT");


    slot = "";
    if( def.index>0 )
      slot = String.format("%04d ", def.index);


    sb.append(String.format(
      "%s\u001b[34m%5s\u001B[0m %s'\u001b[35m%s\u001B[0m' \u001b[32m%s\u001B[0m\n",
      indent,
      defType,
      slot,
      def.value!=null ? def.value : "",
      String.join(",", flags)
    ));

    if( def.children!=null )
    {
      for( Def child : def.children )
      {
        if( child.type!=EDefType.CLASS )
          print_def(child, level + 1);
      }
      for( Def child : def.children )
      {
        if( child.type==EDefType.CLASS )
          print_def(child, level + 1);
      }
    }

    if( def.ops!=null && def.ops.size()>0 )
      print_ops(def, level);
  }

  protected void print_ops(Def def, int level)
  {
    final String indent = "  ".repeat(level+1);

    sb.append(indent);
    sb.append("[ OPCODES ]\n");
    for( OP opcode : def.ops )
    {
      String name = "\u001b[36m" + String.format("%-8s", DefPrinter.opcodeToString(opcode.op)) + "\u001b[0m";

      switch( opcode.op )
      {
        case MOV:
        case PUSHK:
        case PUSH:
        case PUSHFN:
        case CALL:
        case RET:
          sb.append(String.format(
            "%s%s %d\n",
            indent,
            name,
            opcode.a
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

  public static String opcodeToString(EOP op)
  {
    return switch( op )
    {
      case NOP      -> "nop";
      case MOV      -> "mov";
      case PUSH     -> "push";
      case PUSHFN   -> "pushfn";
      case PUSHK    -> "pushk";
      case PUSHI    -> "pushi";
      case PUSHTHIS -> "pushthis";
      case PUSHNULL -> "pushnull";
      case POP      -> "pop";
      case CASTI    -> "casti";
      case CALL     -> "call";
      case RET      -> "ret";
      case ADD      -> "add";
      case INC      -> "inc";
      case GET      -> "get";
    };
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }
}
