package com.srx.jwpl.vm;

import com.srx.jwpl.vm.module.Flask;

import java.util.Deque;
import java.util.LinkedList;

class CallState
{
  public Flask  flask;
  public int    ip;
  public int    cvals;
  public int    rvals;

  public Deque<EHandler> ehandlers;


  public CallState()
  {
    this.ehandlers = new LinkedList<>();
  }

  public static class EHandler
  {
    int destIp;
    int stackSize;
  }
}
