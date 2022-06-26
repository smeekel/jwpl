package com.srx.jwpl.def;

import java.util.LinkedList;

public class Scope
{
  public static class Cohort
  {
    protected LinkedList<Def> defs;

    public Cohort()
    {
      defs = new LinkedList<>();
    }

    public void add(Def def)
    {
      defs.push(def);
    }
  }

  protected LinkedList<Cohort> cohorts;
  protected Cohort  bottom;
  protected Cohort  active;

  public Scope()
  {
    cohorts = new LinkedList<>();

    bottom  = new Cohort();
    active  = bottom;
    cohorts.push(active);
  }

  public void push()
  {
    active = new Cohort();
    cohorts.push(active);
  }

  public void pop()
  {
    assert cohorts.size()>1;
    cohorts.pop();
    active = cohorts.getLast();
  }

  public void add(Def def)
  {
    active.add(def);
  }

  public Def findImmediate(String name)
  {
    for( Def iter : active.defs )
    {
      if( iter.value.equals(name) )
        return iter;
    }

    return null;
  }

}
