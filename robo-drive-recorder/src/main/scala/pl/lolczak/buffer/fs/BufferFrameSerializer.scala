package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializerInputStream, SerializerOutputStream, SerializationException, Serializer}
import SerializationConstants._

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferFrameSerializer[A](implicit val contentSerializer: Serializer[A]) extends Serializer[BufferFrame[A]] {

  val PreambleValue = 0x05AAA.toShort

  val PostambleValue = 0x0555A.toShort

  @throws(classOf[SerializationException])
  def deserialize(inStream: SerializerInputStream): BufferFrame[A] = {
    val preamble = inStream.readShort()
    if (preamble != PreambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    val prevOffset = inStream.readLong()
    val prevSize = inStream.readLong()
    val previousFrame = if (prevOffset != NullOffset) Some(new FrameInfo(prevOffset, prevSize)) else None
    val content = contentSerializer.deserialize(inStream)
    val postamble = inStream.readShort()
    if (postamble != PostambleValue) {
      throw new SerializationException("Frame preamble incorrect")
    }
    new BufferFrame(previousFrame, content)
  }

  def serialize(frame: BufferFrame[A], outStream: SerializerOutputStream): Unit = {
    outStream.writeShort(PreambleValue)
    frame.previousFrame match {
      case None => outStream.writeLong(NullOffset); outStream.writeLong(NullOffset)
      case Some(previousFrame) => outStream.writeLong(previousFrame.offset); outStream.writeLong(previousFrame.size)
    }
    contentSerializer.serialize(frame.content, outStream)
    outStream.writeShort(PostambleValue)
  }

  def sizeOf(element: BufferFrame[A]): Long = 2 * ShortLength + 2 * LongLength + contentSerializer.sizeOf(element.content)

}
