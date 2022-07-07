package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;

public class SHT_null extends Section64
{
  public SHT_null()
  {
    sh_type = ESectionTypes.NULL.getValue();
  }

  @Override
  public void writeBody(OutputStream out)
  {

  }
}
