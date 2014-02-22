package pl.lolczak.io.stream

import java.io.InputStream

import SerializerConstants._

/**
 *
 *
 * @author Lukasz Olczak
 */
class SerializerInputStream(private val inStream: InputStream) extends InputStream {

  def read(): Int = inStream.read()

  override def reset() = inStream.reset()

  override def mark(readlimit: Int) = inStream.mark(readlimit)

  override def close() = inStream.close()

  override def available(): Int = inStream.available()

  override def read(buffer: Array[Byte]): Int = inStream.read(buffer)

  override def markSupported(): Boolean = inStream.markSupported()

  override def read(buffer: Array[Byte], byteOffset: Int, byteCount: Int): Int = inStream.read(buffer, byteOffset, byteCount)

  override def skip(byteCount: Long): Long = inStream.skip(byteCount)

  def readShort(): Short = {
    val byte2 = (read() & ByteMask) << 8
    val byte1 = read() & ByteMask
    (byte2 | byte1).toShort
  }

  def readInt(): Int = {
    val byte4 = (read() & ByteMask) << 24
    val byte3 = (read() & ByteMask) << 16
    val byte2 = (read() & ByteMask) << 8
    val byte1 = read() & ByteMask

    byte4 | byte3 | byte2 | byte1
  }

  def readLong(): Long = {
    val byte8 = (read() & ByteMask) << 56
    val byte7 = (read() & ByteMask) << 48
    val byte6 = (read() & ByteMask) << 40
    val byte5 = (read() & ByteMask) << 32
    val byte4 = (read() & ByteMask) << 24
    val byte3 = (read() & ByteMask) << 16
    val byte2 = (read() & ByteMask) << 8
    val byte1 = read() & ByteMask

    byte8 | byte7 | byte6 | byte5 | byte4 | byte3 | byte2 | byte1
  }

  def readString(): String = {
    val count = readInt()
    val value = new StringBuilder
    (1 to count).foreach {
      v =>
        val char = read()
        if (char <= Utf8OneByteBoundary) {
          value += char.toChar
        } else if (char <= Utf8TwoByteBoundary) {
          value += ((char & 0x1F) << 6 | read() & 0x3F).toChar
        } else {
          value += ((char & 0x1F) << 12 | (read() & 0x3F) << 6 | (read() & 0x3F)).toChar
        }
    }
    value.toString()
  }

  def readBoolean(): Boolean = if (read() == TrueValue) true else false


}
