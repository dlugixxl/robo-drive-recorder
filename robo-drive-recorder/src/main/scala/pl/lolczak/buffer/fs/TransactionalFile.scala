package pl.lolczak.buffer.fs

/**
 *
 *
 * @author Lukasz Olczak
 */
trait TransactionalFile {

  def read(offset: Long, length: Long): Array[Byte]

  def readInt(offset: Long): Int

  def readLong(offset: Long): Long

  def writeInt(offset: Long, value: Int): Unit

  def writeLong(offset: Long, value: Long): Unit

  def write(offset: Long, bytes: Array[Byte]): Unit

}
