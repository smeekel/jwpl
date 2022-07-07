package com.srx.jwpl.elf;

public enum ESectionTypes
{
  NULL          (0x00),
  PROGBITS      (0x01),
  SYMTAB        (0x02),
  STRTAB        (0x03),
  RELA          (0x04),
  HASH          (0x05),
  DYNAMIC       (0x06),
  NOTE          (0x07),
  NOBITS        (0x08),
  REL           (0x09),
  SHLIB         (0x0A),
  DYNSYM        (0x0B),
  INIT_ARRAY    (0x0E),
  FINIT_ARRAY   (0x0F),
  PREINIT_ARRAY (0x10),
  GROUP         (0x11),
  SYMTAB_SHNDX  (0x12),
  NUM           (0x13),
  ;


  private final int id;

  ESectionTypes(int id)
  {
    this.id = id;
  }

  public int getValue()
  {
    return id;
  }
}
