package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializationException, SerializerOutputStream, SerializerInputStream, Serializer}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

/**
 *
 *
 * @author Lukasz Olczak
 */

object SampleVoSerializer extends Serializer[SampleVo] {

  def deserialize(bytes: Array[Byte]): SampleVo = {
    val inStream = new SerializerInputStream(new ByteArrayInputStream(bytes))
    val className = inStream.readString()
    if (className != "SampleVo") {
      throw new SerializationException("Bad preamble")
    }

    val name = inStream.readString()
    val age = inStream.readInt()
    val mature = inStream.readBoolean()

    new SampleVo(name, age, mature)
  }

  def serialize(element: SampleVo): Array[Byte] = {
    val bytes = new ByteArrayOutputStream()
    val outStream = new SerializerOutputStream(bytes)
    outStream.writeString("SampleVo")
    outStream.writeString(element.name)
    outStream.writeInt(element.age)
    outStream.writeBoolean(element.mature)
    outStream.flush()
    bytes.toByteArray
  }

}

