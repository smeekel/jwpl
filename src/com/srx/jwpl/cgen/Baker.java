package com.srx.jwpl.cgen;

import com.srx.jwpl.def.Def;
import com.srx.jwpl.def.DefFlags;
import com.srx.jwpl.def.EDefType;
import com.srx.jwpl.vm.module.EVariableTypes;
import com.srx.jwpl.vm.module.Flask;
import com.srx.jwpl.vm.module.Variable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.LinkedList;

public class Baker
{
  protected Baker()
  {
  }

  public static Flask bake(@NotNull Def root)
  {
    Baker baker = new Baker();
    Flask module;

    module = baker.bakeModule(root);

    return module;
  }

  protected Flask bakeModule(@NotNull Def context)
  {
    Flask module = new Flask();


    module.name       = context.value;
    module.constPool  = null;
    module.members    = null;
    module.stackSize  = 0;

    bakeConsts  (module, context);
    bakeMembers (module, context);


    return module;
  }

  protected Flask bakeFlasks(@NotNull Def context)
  {
    Flask flask = new Flask();

    flask.name = context.value;

    return flask;
  }

  protected void bakeMembers(@NotNull Flask flask, @NotNull Def context)
  {
    LinkedList<Variable> list = new LinkedList<>();

    for( Def child : context.getChildren() )
    {
      if( child.type == EDefType.CLASS )
      {
        Variable var = new Variable();
        var.type  = EVariableTypes.FLASK;
        var.value = null;
        list.add(var);
      }
      else if( child.type == EDefType.VAR && child.isStatic() )
      {
        Variable var = new Variable();
        var.type  = EVariableTypes.NONE;
        var.value = null;
        list.add(var);
      }
    }

    flask.members = new ArrayList<>(list);
  }

  protected void bakeConsts(@NotNull Flask flask, @NotNull Def context)
  {
    ArrayList<String> list;

    int count = (int)context.getChildren()
      .stream()
      .filter( def -> def.type == EDefType.CONST )
      .count()
      ;

    list = new ArrayList<>(count);
    for( Def child : context.getChildren() )
    {
      if( child.type == EDefType.CONST )
        list.add(child.index, child.value);
    }

    flask.constPool = list;
  }


}
