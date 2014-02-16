package pl.lolczak.buffer

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferChunk(val previousOffset: Long, val chunk: Array[Byte]) {

  val length = chunk.length

}
