package com.srx.jwpl.cgen;

import com.srx.jwpl.def.Def;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Scope
{
  protected static class Node
  {
    Def def;
    int level;

    public Node()
    {
    }

    public Node(@NotNull Def def, int level)
    {
      this.def    = def;
      this.level  = level;
    }
  }

  protected static class Chain
  {
    public LinkedList<Node> nodes = new LinkedList<>();

    public void remove(int maxLevel)
    {
      Iterator<Node> iterator = nodes.descendingIterator();
      while( iterator.hasNext() )
      {
        Node node = iterator.next();
        if( node.level>maxLevel )
          iterator.remove();
        if( node.level<=maxLevel )
          break;
      }

    }

    public void add(@NotNull Def def, int level)
    {
      nodes.addLast(new Node(def, level));
    }

    public boolean isEmpty()
    {
      return nodes.isEmpty();
    }

  }

  protected HashMap<String, Chain> scopeTree = new HashMap<>();
  protected int level = 0;


  public void push()
  {
    level++;
  }

  public void pop()
  {
    assert level > 0 ;
    level--;

    Iterator<Map.Entry<String, Chain>> iterator = scopeTree.entrySet().iterator();
    while( iterator.hasNext() )
    {
      Map.Entry<String, Chain> entry = iterator.next();
      entry.getValue().remove(level);
      if( entry.getValue().isEmpty() )
        iterator.remove();
    }
  }

  public void add(@NotNull Def def)
  {
    if( !scopeTree.containsKey(def.value) )
      scopeTree.put(def.value, new Chain());

    scopeTree.get(def.value).add(def, level);
  }

  public void addRoot(@NotNull Def def)
  {
    if( !scopeTree.containsKey(def.value) )
      scopeTree.put(def.value, new Chain());

    Chain chain = scopeTree.get(def.value);
    Node  node;

    node = chain.nodes.isEmpty() ? null : chain.nodes.getFirst() ;
    if( node!=null && node.level==0 )
      throw new ICEException("Attempted to add '%s' to root scope chain; slot already occupied", def.value);

    node = new Node(def, 0);
    chain.nodes.addFirst(node);
  }

  public Def findImmediate(@NotNull String name)
  {
    Chain chain = scopeTree.get(name);
    if( chain==null ) return null;

    Node last = chain.nodes.getLast();
    if( last!=null && last.level==level )
      return last.def;

    return null;
  }

  public Def findFirst(@NotNull String name)
  {
    Chain chain = scopeTree.get(name);
    if( chain==null ) return null;

    Node last = chain.nodes.getLast();

    return last!=null ? last.def : null;
  }

}
