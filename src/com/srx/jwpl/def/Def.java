package com.srx.jwpl.def;

import java.util.HashMap;
import java.util.LinkedList;

public class Def
{
  public String   value;
  public EDefType type;
  public long     flags;

  protected LinkedList<Def> children  = null;
  protected LinkedList<OP>  ops       = null;
  protected HashMap<String, String> aux;

  public Def()
  {
    flags = 0;
  }

  public String getAux(String key)
  {
    if( aux==null ) return null;
    return aux.get(key);
  }

  public void setAux(String key, String value)
  {
    if( aux==null ) aux = new HashMap<>();
    aux.put(key, value);
  }
}
