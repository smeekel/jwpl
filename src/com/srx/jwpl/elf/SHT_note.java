package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;

public class SHT_note extends SHT
{
  public StringBuilder value = new StringBuilder();

  public SHT_note()
  {
    sh_type   = ESectionTypes.NOTE.getValue();
    sh_flags  = 0;
  }

  @Override
  public void writeBody(OutputStream out) throws IOException
  {
    String raw = value.toString();
    out.write(raw.getBytes());
    out.write(0);
    sh_size = raw.length()+1;
  }
}
