package com.srx.jwpl.vm;

import com.srx.jwpl.vm.module.Flask;

import java.util.LinkedList;

public class VirtualMachine
{
  public LinkedList<Flask> flasks;

  public VirtualMachine()
  {
    flasks = new LinkedList<>();
  }

  public void importFlask(final Flask flask)
  {
    flasks.push(flask);
  }

  public void exec()
  {

  }
}
