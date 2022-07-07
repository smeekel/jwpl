package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class SHT_strtab extends Section64
{
  protected List<String> stringList = new LinkedList<>();
  protected int offset = 0;


  public SHT_strtab()
  {
    sh_type  = ESectionTypes.STRTAB.getValue();
    sh_flags = 0x20;

    addString("");
  }

  public void writeHeader(OutputStream out) throws IOException
  {
    super.writeHeader(out);
  }

  @Override
  public void writeBody(OutputStream out) throws IOException
  {
    for( String str : stringList )
    {
      out.write(str.getBytes());
      out.write(0);
    }
  }

  public int addString(String value)
  {
    int thisString = offset;

    if( value==null ) value = "";
    stringList.add(value);
    offset += value.length() + 1;

    sh_size = offset;

    return thisString;
  }
}
