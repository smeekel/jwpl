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
  F_GLOBAL,
  ;

  public static EnumSet<EVarFlags> none()
  {
    return EnumSet.noneOf(EVarFlags.class);
  }

  public static int enumToInt(EnumSet<EVarFlags> eset)
  {
    int flags = 0;

    if( eset.contains(F_PUBLIC)     ) flags |= 0x0001;
    if( eset.contains(F_PROTECTED)  ) flags |= 0x0002;
    if( eset.contains(F_PRIVATE)    ) flags |= 0x0004;
    if( eset.contains(F_STATIC)     ) flags |= 0x0008;
    if( eset.contains(F_CONST)      ) flags |= 0x0010;
    if( eset.contains(F_EXPORT)     ) flags |= 0x0020;
    if( eset.contains(F_GLOBAL)     ) flags |= 0x0040;

    return flags;
  }
}
