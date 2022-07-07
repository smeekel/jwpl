package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ProgramHeader64
{
  public int    p_type;
  public int    p_flags;
  public long   p_offset;
  public long   p_vaddr;
  public long   p_paddr;
  public long   p_filesz;
  public long   p_memsz;
  public long   p_align;

  public ProgramHeader64()
  {
    p_align = 0;
  }

  public void write(OutputStream out) throws IOException
  {
    ByteBuffer buffer = ByteBuffer.allocate(0x38);

    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putInt   (p_type);
    buffer.putInt   (p_flags);
    buffer.putLong  (p_offset);
    buffer.putLong  (p_vaddr);
    buffer.putLong  (p_paddr);
    buffer.putLong  (p_filesz);
    buffer.putLong  (p_memsz);
    buffer.putLong  (p_align);

    out.write(buffer.array());
  }
}
