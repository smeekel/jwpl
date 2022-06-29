package com.srx.jwpl.cgen;

import com.srx.jwpl.vm.module.Variable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Scope
{
  protected static class Node
  {
    Variable var;
    int level;

    public Node()
    {
    }

    public Node(@NotNull Variable var, int level)
    {
      this.var    = var;
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

    public void add(@NotNull Variable var, int level)
    {
      nodes.addLast(new Node(var, level));
    }

    public boolean isEmpty()
    {
      return nodes.isEmpty();
    }

  }

  protected HashMap<String, Chain> scopeTree = new HashMap<>();
  protected int level = 0;


  public Scope()
  {
  }

  public Scope(Variable var)
  {
    add(var);
    push();
  }

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

  public void add(@NotNull Variable var)
  {
    if( !scopeTree.containsKey(var.name) )
      scopeTree.put(var.name, new Chain());

    scopeTree.get(var.name).add(var, level);
  }

  public void addRoot(@NotNull Variable var)
  {
    if( !scopeTree.containsKey(var.name) )
      scopeTree.put(var.name, new Chain());

    Chain chain = scopeTree.get(var.name);
    Node  node;

    node = chain.nodes.isEmpty() ? null : chain.nodes.getFirst() ;
    if( node!=null && node.level==0 )
      throw new ICEException("Attempted to add '%s' to root scope chain; slot already occupied", var.name);

    node = new Node(var, 0);
    chain.nodes.addFirst(node);
  }

  public Variable findImmediate(@NotNull String name)
  {
    Chain chain = scopeTree.get(name);
    if( chain==null ) return null;

    Node last = chain.nodes.getLast();
    if( last!=null && last.level==level )
      return last.var;

    return null;
  }

  public Variable findFirst(@NotNull String name)
  {
    Chain chain = scopeTree.get(name);
    if( chain==null ) return null;

    Node last = chain.nodes.getLast();

    return last!=null ? last.var : null;
  }

}
