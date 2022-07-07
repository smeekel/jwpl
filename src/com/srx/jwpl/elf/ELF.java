package com.srx.jwpl.elf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.LinkedList;

public class ELF
{
  protected ELFHeader64       elfHeader;
  protected Deque<Section64>  sections;
  protected SHT_strtab        sectionNames;


  public ELF()
  {
    elfHeader     = new ELFHeader64();
    sections      = new LinkedList<>();
    sectionNames  = new SHT_strtab();

    elfHeader.e_type = 0x02;
    sectionNames.sh_name = sectionNames.addString(".shstrtab");

    addSection(new SHT_null(), null);
  }

  public void addSection(Section64 section, String name)
  {
    if( name!=null ) section.sh_name = sectionNames.addString(name);
    sections.addLast(section);
  }

  public void write(String filename) throws IOException
  {
    FileOutputStream fout     = new FileOutputStream(filename);
    FileChannel      channel  = fout.getChannel();

    elfHeader.e_shNumber      = (short)( sections.size() + 1 );
    elfHeader.e_shStringIndex = (short)sections.size();
    channel.position(elfHeader.getSize());

    sections.addLast(sectionNames);
    for( Section64 section : sections )
    {
      section.sh_offset = channel.position();
      section.writeBody(fout);
    }

    elfHeader.e_shOffset = channel.position();

    for( Section64 section : sections )
      section.writeHeader(fout);

    channel.position(0);
    elfHeader.write(fout);

    sections.removeLast(); // remove the sectionNames section
    fout.close();
  }



}
