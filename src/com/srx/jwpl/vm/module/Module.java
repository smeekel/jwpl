package com.srx.jwpl.vm.module;

import java.util.List;

public class Module
{
  public List<Flask>    flasks;
  public List<OP>       opcodes;
  public List<String>   consts;


  //
  // TODO Maybe store the root flask in a top-level variable. That would save the need to search though the entire
  //      list for it. All depends on how many times this get called.
  //
  public Flask getRootFlask()
  {
    for( Flask flask : flasks )
    {
      if( flask.parent==null )
        return flask;
    }

    throw new RuntimeException("Unable to find root flask in module");
  }
}
