package com.srx.jwpl.elf;

import com.srx.jwpl.vm.module.Module;
import com.srx.jwpl.vm.module.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.Map;

public class WPLELF extends ELF64
{
  public WPLELF(@NotNull Module module)
  {
    int section;
    section = addFlasks (module);
    addConsts (module, section);
    addOpcodes(module, section);
  }

  private int addFlasks(Module module)
  {
    SHT_progbits  section = new SHT_progbits();
    JSONArray     jroot   = new JSONArray();


    section.sh_flags = 0x20;
    for( Flask flask : module.flasks )
    {
      JSONObject jflask;

      jflask = flaskToJson(flask, module);
      jroot.add(jflask);
    }

    String json = jroot.toJSONString();
    section.data = json.getBytes();

    return this.addSection(section, ".mod.flasks");
  }

  private JSONObject flaskToJson(Flask flask, Module module)
  {
    JSONObject jflask   = new JSONObject();
    JSONObject jmembers = new JSONObject();


    jflask.put("parent",  flask.parent!=null ? flask.parent.name : "");
    jflask.put("name",    flask.name);
    jflask.put("opStart", flask.opFirst);
    jflask.put("opCount", flask.opCount);
    jflask.put("members", jmembers);

    for( Map.Entry<String, Variable> set : flask.members.entrySet() )
    {
      Variable    var   = set.getValue();
      JSONObject  jvar  = new JSONObject();

      jvar.put("type",  var.type.toString());
      jvar.put("flags", EVarFlags.enumToInt(var.flags));
      if( var.type==EVariableTypes.FLASK )
      {
        Flask ref = (Flask)var.value;
        jvar.put("value", ref.name);
      }
      else
      {
        jvar.put("value", var.value!=null ? var.value.toString() : "");
      }

      jmembers.put(set.getKey(), jvar);
    }

    return jflask;
  }

    private void addOpcodes(Module module, int parent)
  {
    SHT_progbits section = new SHT_progbits();
    ByteBuffer buffer;

    section.sh_flags  = 0x04;
    section.sh_link   = parent;
    buffer = ByteBuffer.allocate(module.opcodes.size() * 4);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    for( OP opcode : module.opcodes )
    {
      buffer.put( (byte)opcode.op.id );

      if( opcode.op==EOP.PUSHI )
      {
        int p0 =  opcode.a & 0xFF;
        int p1 = (opcode.a >> 8)  & 0xFF;
        int p2 = (opcode.a >> 16) & 0xFF;

        buffer.put( (byte)p0 );
        buffer.put( (byte)p1 );
        buffer.put( (byte)p2 );
      }
      else
      {
        buffer.put( (byte)opcode.a );
        buffer.put( (byte)opcode.b );
        buffer.put( (byte)opcode.c );
      }
    }

    section.data = buffer.array();
    this.addSection(section, ".mod.op");
  }

  protected void addConsts(Module module, int parent)
  {
    SHT_strtab strings = new SHT_strtab();
    strings.sh_link = parent;

    for( String value : module.consts )
    {
      strings.addString(value);
    }

    this.addSection(strings, ".mod.consts");
  }
}
