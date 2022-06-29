package com.srx.jwpl.def;


import com.srx.jwpl.vm.module.OP;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

public class Def
{
  public Def      parent;
  public String   value;
  public EDefType type;
  public long     flags;
  public int      index;

  protected int nextVarSlot   = 0;
  protected int nextConstSlot = 0;
  protected int nextObjSlot   = 0;
  protected LinkedList<Def> children  = null;
  protected LinkedList<OP>  ops       = null;
  protected HashMap<String, String> aux;

  public Def()
  {
    parent  = null;
    type    = EDefType.NONE;
    flags   = DefFlags.NONE;
    value   = null;
    index   = 0;
  }

  public String getAux(@NotNull String key)
  {
    if( aux==null ) return null;
    return aux.get(key);
  }

  public void setAux(@NotNull String key, String value)
  {
    if( aux==null ) aux = new HashMap<>();
    aux.put(key, value);
  }

  public void addChild(@NotNull Def child)
  {
    if( children==null ) children = new LinkedList<>();

    child.parent = this;
    children.addLast(child);
  }

  public Def findConst(String value)
  {
    if( children!=null )
    {
      for( Def child : children )
      {
        if( child.type != EDefType.CONST )
          continue;
        if( child.value.equals(value) )
          return child;
      }
    }
    return null;
  }

  public int generateVarSlot()
  {
    return ++nextVarSlot;
  }

  public int generateObjSlot()
  {
    return ++nextObjSlot;
  }

  public int generateConstSlot()
  {
    return ++nextConstSlot;
  }

  public void emit(OP op)
  {
    if( ops==null ) ops = new LinkedList<>();
    ops.addLast(op);
  }

  public LinkedList<Def> getChildren()
  {
    return children;
  }

  public boolean isStatic()
  {
    return (flags & DefFlags.F_STATIC) != 0 ;
  }
}
