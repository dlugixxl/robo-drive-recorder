package pl.lolczak.buffer.fs

/**
 *
 *
 * @author Lukasz Olczak
 */
class FrameInfo(val offset: Long, val size: Long) {

  def nextOffset = offset + size

}