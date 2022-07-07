package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.Module;
import com.srx.jwpl.vm.module.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class Baker
{
  public static Module bake(Scope tree)
  {
    Baker baker = new Baker();

    baker.bakeFlask(tree.getRoot());

    return baker.module;
  }

  protected Module module;

  protected Baker()
  {
    module = new Module();
    module.flasks   = new LinkedList<>();
    module.opcodes  = new LinkedList<>();
    module.consts   = new LinkedList<>();
  }


  protected Flask bakeFlask(Scope.Def context)
  {
    Flask flask = new Flask();

    flask.name = context.name;
    if( context.flags.contains(EVarFlags.F_GLOBAL) )
      return flask;
    if( context.consts!=null )
      bakeConsts(context.consts);
    if( context.ops!=null )
      bakeOpcodes(flask, context.ops);

    module.flasks.add(flask);
    flask.module = module;

    for( Map.Entry<String, Deque<Scope.Def>> set : context.children.entrySet() )
    {
      Deque<Scope.Def>  children = set.getValue();
      Scope.Def         child;
      Variable          var;

      //
      // Sanity check
      // There should only be 0 or 1 children of a scope chain. Any more would mean a temporary variable
      // was not released correctly.
      //
      assert children.size() < 2 ;

      child = children.peek();
      if( child==null ) continue;

      if( child.type == Scope.EDefTypes.VAR )
      {
        var = new Variable();
        var.type  = EVariableTypes.NONE;
        var.flags = child.flags;
        flask.set(set.getKey(), var);
      }
      else if( child.type == Scope.EDefTypes.FLASK )
      {
        var = new Variable(EVariableTypes.FLASK);
        var.flags = child.flags;

        Flask childFn   = bakeFlask(child);
        childFn.parent  = flask;
        var.value       = childFn;

        flask.set(set.getKey(), var);
      }
    }

    return flask;
  }

  protected void bakeConsts(Vector<String> consts)
  {
    module.consts.addAll(consts);
  }

  protected void bakeOpcodes(Flask flask, Vector<OP> src)
  {
    Vector<OP> dst = new Vector<>(src);

    for( OP op : dst )
    {
      if( op.op==EOP.B || op.op==EOP.BF || op.op==EOP.XENTER )
      {
        int start = dst.indexOf(op);
        int end   = dst.indexOf(op.aux);

        op.a = end - start;
      }
    }

    flask.opFirst = module.opcodes.size();
    module.opcodes.addAll(dst);
    flask.opCount = dst.size();
  }


}
