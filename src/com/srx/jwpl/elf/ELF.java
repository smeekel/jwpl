package com.srx.jwpl.elf;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ELF
{
  public static void test() throws IOException
  {
    DataOutputStream out = new DataOutputStream(new FileOutputStream("c:/temp/test.elf"));
    Header64 header = new Header64();

    header.e_abi      = (byte)0x30;
    header.e_type     = (byte)0x02;
    header.e_machine  = 0x0200;

    header.write(out);
    out.close();

  }
}
