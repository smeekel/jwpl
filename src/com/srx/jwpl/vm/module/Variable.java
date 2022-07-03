package com.srx.jwpl.vm.module;

import java.util.EnumSet;

public class Variable
{
  public EVariableTypes     type;
  public Object             value;
  public EnumSet<EVarFlags> flags;

  public Variable()
  {
    flags = EnumSet.noneOf(EVarFlags.class);
  }

  public Variable(EVariableTypes type)
  {
    this.type = type;
  }

  public static Variable none()
  {
    return new Variable(EVariableTypes.NONE);
  }
}
