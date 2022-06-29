package com.srx.jwpl.vm.module;

import java.util.EnumSet;

public class Variable
{
  public EVariableTypes     type = EVariableTypes.NONE;
  public EnumSet<VarFlags>  flags = EnumSet.noneOf(VarFlags.class);
  public String             name;
  //public Object             value;

}
