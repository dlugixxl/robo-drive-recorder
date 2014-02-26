package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializerInputStream, SerializerOutputStream, SerializationException, Serializer}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

/**
 *
 *
 * @author Lukasz Olczak
 */
object BufferFrameSerializer extends Serializer[BufferFrame] {

  val PreambleValue = 0x05AAA.toShort

  val PostambleValue = 0x0555A.toShort

  @throws(classOf[SerializationException])
  def deserialize(bytes: Array[Byte]): BufferFrame = {
    val inStream = new SerializerInputStream(new ByteArrayInputStream(bytes))
    val preamble = inStream.readShort()
    if (preamble != PreambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    val prevOffset = inStream.readLong()
    val contentLength = inStream.readInt()
    val content = new Array[Byte](contentLength)
    inStream.read(content)
    val postamble = inStream.readShort()
    if (postamble != PostambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    new BufferFrame(previousFrameOffset = prevOffset, content = content)
  }

  def serialize(element: BufferFrame): Array[Byte] = {
    val bytes = new ByteArrayOutputStream()
    val outStream = new SerializerOutputStream(bytes)
    outStream.writeShort(PreambleValue)
    outStream.writeLong(element.previousFrameOffset)
    outStream.writeInt(element.content.length)
    outStream.write(element.content)
    outStream.writeShort(PostambleValue)
    outStream.flush()
    bytes.toByteArray
  }

}
