package pl.lolczak.buffer

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferTooSmallException(val message: String) extends Exception(message) {

  def this(bufferSize: Long, elementSize: Long) = this(s"Buffer to small. BufferSize=$bufferSize, elementSize=$elementSize")

}
