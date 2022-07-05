package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.EVarFlags;
import com.srx.jwpl.vm.module.OP;

import java.util.*;

public class Scope
{
  public enum EDefTypes
  {
    FLASK,
    VAR,
    PARAM,
    EXCEPTION,
  }

  public static class Def
  {
    public boolean              inTree;
    public Def                  parent;
    public String               name;
    public Integer              index;
    public EDefTypes            type;
    public EnumSet<EVarFlags>   flags;
    public Vector<OP>           ops;
    public Vector<String>       consts;
    public HashMap<String, Deque<Def>> children;

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
      return -(++stackElements);
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
    Deque<Def> pile = active.children.get(name);
    return pile.peek();
  }

  public Def findFirst(String name)
  {
    Def outter = active;

    while( outter!=null )
    {
      Deque<Def> def = outter.children.get(name);
      if( def!=null )
        return def.peek();

      outter = outter.parent;
    }

    return null;
  }

  public void add(Def def)
  {
    if( !active.children.containsKey(def.name) )
      active.children.put(def.name, new LinkedList<>());

    active.children.get(def.name).push(def);
    def.parent  = active;
    def.inTree  = true;
  }

  public void pushChild(Def def)
  {
    add(def);
  }

  public void popChild(Def def)
  {
    active.children.get(def.name).pop();
  }

  public void addRoot(Def def)
  {
    if( !root.children.containsKey(def.name) )
      root.children.put(def.name, new LinkedList<>());

    root.children.get(def.name).push(def);
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
