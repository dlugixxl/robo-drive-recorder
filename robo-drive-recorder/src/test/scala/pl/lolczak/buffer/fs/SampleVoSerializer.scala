package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializationException, SerializerOutputStream, SerializerInputStream, Serializer}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

/**
 *
 *
 * @author Lukasz Olczak
 */

object SampleVoSerializer extends Serializer[SampleVo] {

  private val StringSizeLength = 4

  private val TypeNameLength = 12

  private val IntSize = 4

  private val BooleanSize = 1

  @throws(classOf[SerializationException])
  override def deserialize(inStream: SerializerInputStream): SampleVo = {
    val className = inStream.readString()
    if (className != "SampleVo") {
      throw new SerializationException("Bad preamble")
    }

    val name = inStream.readString()
    val age = inStream.readInt()
    val mature = inStream.readBoolean()

    new SampleVo(name, age, mature)
  }

  override def serialize(element: SampleVo, outStream: SerializerOutputStream): Unit = {
    outStream.writeString("SampleVo")
    outStream.writeString(element.name)
    outStream.writeInt(element.age)
    outStream.writeBoolean(element.mature)
  }

  def sizeOf(element: SampleVo): Long = TypeNameLength + StringSizeLength + element.name.length + IntSize + BooleanSize

}

