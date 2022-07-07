package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;

public class SHT_progbits extends SHT
{
  public byte[] data;

  public SHT_progbits()
  {
    sh_type  = ESectionTypes.PROGBITS.getValue();
    sh_flags = 0x00;
  }

  @Override
  public void writeBody(OutputStream out) throws IOException
  {
    sh_size = data!=null ? data.length : 0 ;
    if( data!=null ) out.write(data);
  }
}
