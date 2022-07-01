package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.EVarFlags;
import com.srx.jwpl.vm.module.OP;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Vector;

public class Scope
{
  public enum EDefTypes
  {
    FLASK,
    VAR,
    PARAM
  }

  public static class Def
  {
    public boolean              inTree;
    public Def                  parent;
    public String               name;
    public Integer              index;
    public EDefTypes            type;
    public EnumSet<EVarFlags>   flags;
    public HashMap<String, Def> children;
    public Vector<OP>           ops;
    public Vector<String>       consts;

    protected int stackElements;

    public Def(EDefTypes type)
    {
      this.type     = type;
      children      = new HashMap<>();
      ops           = new Vector<>();
      flags         = EVarFlags.none();
      inTree        = false;
      index         = null;

      stackElements = 0;
    }

    public int allocStackSlot()
    {
      return stackElements++;
    }
  }

  protected Def root;
  protected Def active;

  public Scope()
  {
    root    = new Def(EDefTypes.FLASK);
    active  = root;
    root.consts = new Vector<>();
  }

  public Def getRoot()
  {
    return root;
  }

  public Def getActive()
  {
    return active;
  }

  public boolean doesNameExistInCurrentScope(String name)
  {
    return active.children.containsKey(name);
  }

  public Def findLocal(String name)
  {
    return active.children.get(name);
  }

  public Def findFirst(String name)
  {
    Def outter = active;

    while( outter!=null )
    {
      Def def = outter.children.get(name);
      if( def!=null )
        return def;

      outter = outter.parent;
    }

    return null;
  }

  public void add(Def def)
  {
    active.children.put(def.name, def);
    def.parent  = active;
    def.inTree  = true;
  }

  public void addRoot(Def def)
  {
    root.children.put(def.name, def);
    def.parent  = root;
    def.inTree  = true;
  }

  public void push(Def def)
  {
    if( !def.inTree ) add(def);
    active = def;
  }

  public void pop()
  {
    active = active.parent;
  }

  public int emitConst(String value)
  {
    int index;

    index = root.consts.indexOf(value);
    if( index != -1 ) return index;

    index = root.consts.size();
    root.consts.add(value);

    return index;
  }

  public int calculateParentDelta(Def current, Def sub)
  {
    int delta = 0;

    while( current!=null )
    {
      if( current.children.containsKey(sub.name) )
        return delta;

      delta++;
      current = current.parent;
    }

    return -1;
  }


}
