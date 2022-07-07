package com.srx.jwpl.elf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ELFHeader64
{
  public int    e_magic;
  public byte   e_class;
  public byte   e_data;
  public byte   e_headerVersion;
  public byte   e_abi;
  public byte   e_abiVersion;
  public short  e_type;
  public short  e_machine;
  public int    e_elfVersion;
  public long   e_programEntry;
  public long   e_phOffset;
  public long   e_shOffset;
  public int    e_flags;
  public short  e_ehSize;
  public short  e_phEntSize;
  public short  e_phNumber;
  public short  e_shEntSize;
  public short  e_shNumber;
  public short  e_shStringIndex;

  public ELFHeader64()
  {
    e_magic         = 0x7F454C46;
    e_class         = 2; // 2 = 64bit
    e_data          = 1; // 1 = Little endian
    e_headerVersion = 1;
    e_elfVersion    = 1;
    e_phOffset      = 0;
    e_ehSize        = 0x40; // size of this header
    e_phEntSize     = 0x38; // size of program header elements
    e_shEntSize     = 0x40; // size of section header elements

    e_abi           = 0x30; // WPL ABI id
    e_abiVersion    = 1;
    e_machine       = 0x200; // WPL opcode set v20220701 (v1)
  }

  public int getSize()
  {
    return 0x40;
  }

  public void write(OutputStream out) throws IOException
  {
    ByteBuffer buffer = ByteBuffer.allocate(0x40);
    byte[] pad = new byte[7];

    buffer.order(ByteOrder.BIG_ENDIAN);
    buffer.putInt   (e_magic);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.put      (e_class);
    buffer.put      (e_data);
    buffer.put      (e_headerVersion);
    buffer.put      (e_abi);
    buffer.put      (e_abiVersion);
    buffer.put      (pad);
    buffer.putShort (e_type);
    buffer.putShort (e_machine);
    buffer.putInt   (e_elfVersion);
    buffer.putLong  (e_programEntry);
    buffer.putLong  (e_phOffset);
    buffer.putLong  (e_shOffset);
    buffer.putInt   (e_flags);
    buffer.putShort (e_ehSize);
    buffer.putShort (e_phEntSize);
    buffer.putShort (e_phNumber);
    buffer.putShort (e_shEntSize);
    buffer.putShort (e_shNumber);
    buffer.putShort (e_shStringIndex);

    out.write(buffer.array());
  }
}
