package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class SHT
{
  public int    sh_name;
  public int    sh_type;
  public long   sh_flags;
  public long   sh_addr;
  public long   sh_offset;
  public long   sh_size;
  public int    sh_link;
  public int    sh_info;
  public long   sh_addralign;
  public long   sh_entsize;

  public long file_offset;

  public SHT()
  {
    sh_addralign = 1;
  }

  public void writeHeader(OutputStream out) throws IOException
  {
    ByteBuffer buffer = ByteBuffer.allocate(0x40);

    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putInt   (sh_name);
    buffer.putInt   (sh_type);
    buffer.putLong  (sh_flags);
    buffer.putLong  (sh_addr);
    buffer.putLong  (sh_offset);
    buffer.putLong  (sh_size);
    buffer.putInt   (sh_link);
    buffer.putInt   (sh_info);
    buffer.putLong  (sh_addralign);
    buffer.putLong  (sh_entsize);

    out.write(buffer.array());
  }

  public abstract void writeBody(OutputStream out) throws IOException;


}
