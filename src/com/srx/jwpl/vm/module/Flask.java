package com.srx.jwpl.vm.module;

import java.util.List;
import java.util.Vector;

public class Flask extends Variable
{
  public Vector<Variable> members;
  public Vector<String>   constPool;

  public Flask()
  {
    type    = EVariableTypes.FLASK;
    members = new Vector<>();
  }

}
