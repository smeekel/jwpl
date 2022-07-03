package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.*;

import java.util.Map;
import java.util.Vector;

public class Baker
{
  public static Flask bake(Scope tree)
  {
    Baker baker = new Baker();

    return baker.bakeFlask(tree.getRoot());
  }

  protected Flask root = null;

  protected Baker()
  {
  }

  protected Flask bakeFlask(Scope.Def context)
  {
    Flask flask = new Flask();
    flask.name = context.name;

    if( context.type == Scope.EDefTypes.FLASK && context.flags.contains(EVarFlags.F_GLOBAL) )
    {
      return flask;
    }

    if( root==null )
      root = flask;
    if( context.consts!=null )
      flask.consts = new Vector<>(context.consts);
    if( context.ops!=null )
      flask.ops = bakeOpcodes(context.ops);


    for( Map.Entry<String, Scope.Def> set : context.children.entrySet() )
    {
      Scope.Def child = set.getValue();
      Variable  var;

      if( child.type == Scope.EDefTypes.VAR )
      {
        var = new Variable();
        var.type  = EVariableTypes.NONE;
        var.flags = child.flags;
        flask.set(set.getKey(), var);
      }
      else if( child.type == Scope.EDefTypes.FLASK )
      {
        Flask childFlask = bakeFlask(child);

        var = new Variable();
        var.type  = EVariableTypes.FLASK;
        var.value = childFlask;
        var.flags = child.flags;
        childFlask.parent = flask;
        childFlask.consts = root.consts;
        flask.set(set.getKey(), var);
      }
    }

    return flask;
  }

  private Vector<OP> bakeOpcodes(Vector<OP> src)
  {
    Vector<OP> out = new Vector<>(src);

    for( OP op : out )
    {
      if( op.op==EOP.B || op.op==EOP.BF )
      {
        int start = out.indexOf(op);
        int end   = out.indexOf(op.aux);

        //System.out.printf("> Delta = %d (%d, %d)\n", end-start, start, end);
        op.a = end - start;
      }
    }

    return out;
  }


}
