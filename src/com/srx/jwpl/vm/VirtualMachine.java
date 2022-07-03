package com.srx.jwpl.vm;

import com.srx.jwpl.vm.module.*;

import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class VirtualMachine
{
  protected Vector<Flask>         modules;
  protected Map<String, Flask>    index;
  protected LinkedList<CallState> callStack;
  protected StackFrame            stack;

  protected static class CallState
  {
    public Flask  flask;
    public int    ip;
    public int    cvals;
    public int    rvals;
  }

  public VirtualMachine()
  {
    modules   = new Vector<>();
    callStack = new LinkedList<>();
    stack     = new StackFrame();
  }

  public void addExec(Flask flask)
  {
    modules.add(flask);
    importGlobals(flask);
    exec(flask);
  }

  protected void importGlobals(Flask flask)
  {
    for( Variable var : flask.members.values() )
    {
      if( var.type != EVariableTypes.FLASK )
        continue;
      if( !var.flags.contains(EVarFlags.F_GLOBAL) )
        continue;

      Flask inner = (Flask)var.value;

      inner.external = switch( inner.name )
      {
        case "print"  -> RunTime::extPrint;
        default       -> RunTime::extStub;
      };
    }
  }

  protected void exec(Flask flask)
  {
    CallState cstate = new CallState();
    cstate.flask = flask;
    cstate.ip    = 0;
    callStack.clear();
    callStack.push(cstate);



    while( !callStack.isEmpty() )
    {
      final OP op = cstate.flask.ops.get(cstate.ip++);

      switch( op.op )
      {
        case NOP, LABEL -> opNOP(op);
        case GETTK    -> opGETTK(op);
        case PUSHK    -> opPUSHK(op);
        case POP      -> opPOP(op);
        case RET      -> opRET(op);
        case PUSHI    -> opPUSHI(op);
        case ASGN     -> opASGN(op);
        case PUSHTHIS -> opPUSHTHIS(op);
        case PUT      -> opPUT(op);
        case GET      -> opGET(op);
        case SGET     -> opSGET(op);
        case CALL     -> opCALL(op);
        case PUSH     -> opPUSH(op);
        case PUSHNULL -> opPUSHNULL(op);
        case COAL     -> opCOAL(op);
        case B        -> opB(op);
        case BF       -> opBF(op);
        default -> throw new RuntimeException(String.format("Missing opcode support: %s", op.op.mnemonic));
      }

      if( (op.op==EOP.CALL || op.op==EOP.RET) && callStack.size()>0 )
        cstate = callStack.getFirst();
    }
  }

  private void opBF(OP op)
  {
    boolean cond = varToBoolean(stack.get(0));

    stack.pop();
    if( !cond ) opB(op);
  }

  private void opB(OP op)
  {
    callStack.getFirst().ip += (op.a - 1);
  }

  private void opCOAL(OP op)
  {
    if( stack.get(1).type == EVariableTypes.NONE && stack.get(0).type != EVariableTypes.NONE )
    {
      stack.set(1, stack.get(0));
    }

    stack.pop();
  }

  private void opPUSHNULL(OP op)
  {
    stack.push(Variable.none());
  }

  private void opSGET(OP op)
  {
    Variable  srcName     = stack.get(0);
    Variable  srcFlask    = stack.get(1);
    String    srcNameStr;
    Flask     flask;

    srcNameStr = varToString(srcName);

    if( srcFlask.value!=null && srcFlask.value.getClass().isAssignableFrom(Flask.class) )
    {
      flask = (Flask) srcFlask.value;

      stack.pop();
      stack.set(0, flask.members.get(srcNameStr));
    }
    else
    {
      stack.pop();
      stack.set(0, Variable.none());
    }
  }

  private void opNOP(OP op)
  {
  }

  private void opPUSH(OP op)
  {
    stack.push(stack.get(op.a));
  }

  private void opGET(OP op)
  {
    Variable  srcName     = stack.get(0);
    Variable  srcFlask    = stack.get(1);
    String    srcNameStr;
    Flask     flask;

    srcNameStr = varToString(srcName);

    if( !srcFlask.value.getClass().isAssignableFrom(Flask.class) )
      throw new RuntimeException(String.format("No such member: %s", srcNameStr));

    flask = (Flask)srcFlask.value;

    stack.pop();
    stack.set(0, flask.members.get(srcNameStr));
  }

  private void opPUT(OP op)
  {
    Variable  src       = stack.get(0);
    Variable  dst       = stack.get(1);
    Variable  dstFlask  = stack.get(2);
    Flask     flask;
    String    dstName;

    flask   = (Flask) dstFlask.value;
    dstName = varToString(dst);
    flask.members.put(dstName, src);

    stack.pop();
    stack.pop();
    stack.set(0, dst);

  }

  private void opPUSHTHIS(OP op)
  {
    pushFlask( callStack.getFirst().flask );
  }

  private void opASGN(OP op)
  {
    Variable src = stack.get(0);
    Variable dst = stack.get(1);

    dst.type  = src.type;
    dst.value = src.value;

    stack.pop();
  }

  private void opPUSHI(OP op)
  {
    Variable var = new Variable(EVariableTypes.INT);
    var.value = op.a;
    stack.push(var);
  }

  private void opRET(OP op)
  {
    CallState me = callStack.getFirst();
    callStack.pop();

    if( callStack.size()>0 )
    {
      stack.leave();

      int total       = me.cvals + op.a + 1;
      int firstSlot   = total - 1;


      System.out.printf("> total slots %d\n", total);
      for( int i=0 ; i<total ; i++ )
      {
        if( i==0 && me.rvals>0 )
        {
          Variable var;

          if( op.a>0 )
            var = stack.get(op.a - 1 - i);
          else
            var = Variable.none();

          stack.set(firstSlot - i, var);
        }
        else
        {
          stack.pop();
        }
      }
    }
  }

  private void opCALL(OP op)
  {
    Variable  varToCall;
    Flask     toCall;
    CallState nextCstate;

    varToCall = stack.get(op.a);
    assert varToCall.type == EVariableTypes.FLASK ;
    toCall = (Flask)varToCall.value;

    if( toCall.external == null )
    {
      nextCstate = new CallState();
      nextCstate.flask  = toCall;
      nextCstate.cvals  = op.a;
      nextCstate.rvals  = op.b;

      stack.enter(op.a);
      callStack.push(nextCstate);
    }
    else
    {
      toCall.external.call(new CallContext(stack, op.a, op.b));
    }
  }

  private void opPOP(OP op)
  {
    stack.pop();
  }

  private void opPUSHK(OP op)
  {
    CallState cstate = callStack.getFirst();
    String    value  = cstate.flask.consts.get(op.a);
    pushStr(value);
  }


  private void opGETTK(OP op)
  {
    CallState cstate  = callStack.getFirst();
    Flask     thisRef = cstate.flask;
    String    memberName;

    for( int i=0 ; i<op.a ; i++ )
      thisRef = thisRef.parent;

    memberName = cstate.flask.consts.get(op.b);
    push( thisRef.members.get(memberName) );
  }



  private void push(Variable var)
  {
    if( var==null )
    {
      stack.push(Variable.none());
    }
    else if( var.type==EVariableTypes.FLASK )
    {
      stack.push( var );
    }
    else
    {
      stack.push(var);
    }
  }

  private void pushStr(String str)
  {
    Variable var = new Variable(EVariableTypes.STRING);
    var.value = str;
    stack.push(var);
  }

  private void pushFlask(Flask flask)
  {
    Variable var = new Variable(EVariableTypes.FLASK);
    var.value = flask;
    stack.push(var);
  }

  public static String varToString(Variable var)
  {
    return switch( var.type )
    {
      case NONE     -> "<NONE>";
      case INT      -> ((Integer)var.value).toString();
      case FLOAT    -> ((Float)var.value).toString();
      case STRING   -> var.value.toString();
      case BOOLEAN  -> ((Boolean)var.value).toString();
      case FLASK    -> String.format("<Flask %s>", ((Flask)var.value).name);
      case REF      -> "<REF>";
    };
  }

  public static boolean varToBoolean(Variable var)
  {
    return switch( var.type )
      {
        case NONE     -> false;
        case INT      -> ((Integer)var.value) != 0;
        case FLOAT    -> ((Float)var.value) != 0.0f;
        case STRING   -> !var.value.toString().isEmpty();
        case BOOLEAN  -> ((Boolean)var.value);
        case FLASK, REF -> true;
      };
  }

}
