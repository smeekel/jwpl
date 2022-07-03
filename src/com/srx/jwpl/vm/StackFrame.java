package com.srx.jwpl.vm;

import com.srx.jwpl.vm.module.Variable;

import java.util.LinkedList;

public class StackFrame
{
  LinkedList<Variable>  stack;
  LinkedList<Integer>   frames;
  int frameOffset = 0;

  public StackFrame()
  {
    stack   = new LinkedList<>();
    frames  = new LinkedList<>();
  }


  public Variable get(int i)
  {
    //System.out.printf("> GET(%d) - %d\n", i, calculateIndex(i));
    return stack.get(calculateIndex(i));
  }

  public void set(int i, Variable dst)
  {
    stack.set(calculateIndex(i), dst);
  }

  public void push(Variable var)
  {
    stack.push(var);
  }

  public void pop()
  {
    stack.pop();
  }

  public void enter(int offset)
  {
    frames.push( frameOffset);
    frameOffset = stack.size() - offset;
  }

  public void leave()
  {
    if( frames.size()>0 )
    {
      frameOffset = frames.getFirst();
      frames.pop();
    }
  }

  protected int calculateIndex(int i)
  {
    return i>-1
      ? i
      : (stack.size() - frameOffset) - (-i)
      ;
  }
}
