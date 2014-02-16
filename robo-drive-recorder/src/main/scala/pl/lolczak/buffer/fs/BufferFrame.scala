package pl.lolczak.buffer.fs

import java.io.RandomAccessFile
import SerializationConstants._
import BufferFrame._
import pl.lolczak.buffer.BufferFormatException

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferFrame(val previousFrameOffset: Long, val content: Array[Byte]) {

  val size = 2 * ShortLength + LongLength + IntLength + content.length

  def writeTo(file: RandomAccessFile) {
    file.writeShort(PreambleValue)
    file.writeLong(previousFrameOffset)
    file.writeInt(content.length)
    file.write(content)
    file.writeShort(PostambleValue)
  }

}

object BufferFrame {

  val PreambleValue = 0x05AAA

  val PostambleValue = 0x0555A

  def readFrom(file: RandomAccessFile): BufferFrame = {
    val preamble = file.readShort()
    if (preamble != PreambleValue) {
      throw new BufferFormatException("Frame preamble incorrect")
    }
    val prevOffset = file.readLong()
    val contentLength = file.readInt()
    val content = new Array[Byte](contentLength)
    file.read(content)
    val postamble = file.readShort()
    if (postamble != PostambleValue) {
      throw new BufferFormatException("Frame preamble incorrect")
    }
    new BufferFrame(previousFrameOffset = prevOffset, content = content)
  }

}
