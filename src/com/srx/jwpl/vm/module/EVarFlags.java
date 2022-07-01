package com.srx.jwpl.vm.module;

import java.util.EnumSet;

public enum EVarFlags
{
  F_PUBLIC,
  F_PROTECTED,
  F_PRIVATE,
  F_STATIC,
  F_CONST,
  F_EXPORT,
  F_EXTERN,
  ;

  public static EnumSet<EVarFlags> none()
  {
    return EnumSet.noneOf(EVarFlags.class);
  }
}
