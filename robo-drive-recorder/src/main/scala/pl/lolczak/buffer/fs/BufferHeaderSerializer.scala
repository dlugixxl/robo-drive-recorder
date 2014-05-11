package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializationException, SerializerInputStream, SerializerOutputStream, Serializer}
import pl.lolczak.buffer.fs.SerializationConstants._
import scala.Some

/**
 *
 *
 * @author Lukasz Olczak
 */
private[fs] object BufferHeaderSerializer extends Serializer[BufferMetadata] {

  val PreambleValue = 0x05AAA.toShort

  val PostambleValue = 0x0555A.toShort

  @throws(classOf[SerializationException])
  def deserialize(inStream: SerializerInputStream): BufferMetadata = {
    val preamble = inStream.readShort()
    if (preamble != PreambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    val lastOffset = inStream.readLong()
    val lastSize = inStream.readLong()
    val lastFrame = if (lastOffset != NullOffset) Some(new FrameInfo(lastOffset, lastSize)) else None
    val bufferSize = inStream.readLong()
    val postamble = inStream.readShort()
    if (postamble != PostambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    new BufferMetadata(lastFrame, bufferSize)
  }

  def serialize(element: BufferMetadata, outStream: SerializerOutputStream): Unit = {
    outStream.writeShort(PreambleValue)
    element.lastFrameOption match {
      case None => outStream.writeLong(NullOffset); outStream.writeLong(NullOffset)
      case Some(lastFrame) => outStream.writeLong(lastFrame.offset); outStream.writeLong(lastFrame.size)
    }
    outStream.writeLong(element.bufferSize)
    outStream.writeShort(PostambleValue)
  }

  def sizeOf(element: BufferMetadata): Long = 2 * ShortLength + 3 * LongLength

}
