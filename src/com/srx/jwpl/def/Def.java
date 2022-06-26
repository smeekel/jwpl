package com.srx.jwpl.def;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

public class Def
{
  public Def      parent;
  public String   value;
  public EDefType type;
  public long     flags;

  protected LinkedList<Def> children  = null;
  protected LinkedList<OP>  ops       = null;
  protected HashMap<String, String> aux;

  public Def()
  {
    parent  = null;
    type    = EDefType.NONE;
    flags   = 0;
    value   = null;
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
    children.add(child);
  }
}
