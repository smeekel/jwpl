package com.srx.jwpl.vm.module;

import com.srx.jwpl.vm.INativeMethod;

import java.util.HashMap;

public class Flask
{
  public Flask                      parent;
  public String                     name;
  public HashMap<String, Variable>  members;
  public INativeMethod              external;
  public Module                     module;
  public int                        opFirst;
  public int                        opCount;



  public Flask()
  {
    this.members  = new HashMap<>();
  }

  public Variable get(String name)
  {
    return members.get(name);
  }

  public void set(String name, Variable value)
  {
    members.put(name, value);
  }
}
