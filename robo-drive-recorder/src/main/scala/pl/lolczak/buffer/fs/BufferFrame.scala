package pl.lolczak.buffer.fs

import java.io.RandomAccessFile
import SerializationConstants._

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferFrame(val previousFrameOffset: Long, val content: Array[Byte]) {

  val size = 2 * ShortLength + LongLength + IntLength + content.length

  def writeTo(file: RandomAccessFile) {
    file.writeShort(0x0AAAA)
    file.writeLong(previousFrameOffset)
    file.writeInt(content.length)
    file.write(content)
    file.writeShort(0x0AAAA)
  }

}

object BufferFrame {

  def readFrom(file: RandomAccessFile): BufferFrame = {
    val preamble = file.readShort()
    val prevOffset = file.readLong()
    val contentLength = file.readInt()
    val content = new Array[Byte](contentLength)
    file.read(content)
    val postamble = file.readShort()
    new BufferFrame(previousFrameOffset = prevOffset, content = content)
  }

}
