package com.srx.jwpl.vm.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Flask
{
  public String   name;
  public int      stackSize;
  public Flask    module;
  public Flask    parent;

  public List<String>           constPool;
  public List<Variable>         members;
  public Map<String, Variable>  memberNames;
  public Map<Integer, Variable> memberIndexes;
}
