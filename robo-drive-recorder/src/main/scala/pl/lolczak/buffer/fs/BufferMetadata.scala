package pl.lolczak.buffer.fs

import pl.lolczak.buffer.BufferTooSmallException

/**
 *
 *
 * @author Lukasz Olczak
 */
private[fs] class BufferMetadata(val lastFrameOption: Option[FrameInfo], val bufferSize: Long) {

  private val metadataSize = BufferHeaderSerializer.sizeOf(this)

  private val effectiveSize = bufferSize - metadataSize

  @throws(classOf[BufferTooSmallException])
  def shiftOffsetBy(length: Long): BufferMetadata = {
    if (length > effectiveSize) {
      throw new BufferTooSmallException(bufferSize = bufferSize, elementSize = length)
    }

    new BufferMetadata(Some(new FrameInfo(currentFrameOffset, length)), bufferSize)
  }

  def isBufferEmpty = lastFrameOption.isEmpty

  lazy val currentFrameOffset: Long = nextFrameOffset.getOrElse(metadataSize)

  private def nextFrameOffset = for (lastFrame <- lastFrameOption if lastFrame.nextOffset < bufferSize) yield lastFrame.nextOffset

}

private[fs] object EmptyBufferMetadata {

  def apply(bufferSize: Long) = new BufferMetadata(None, bufferSize)

}

private[fs] object FilledBufferMetadata {

  def apply(lastFrame: FrameInfo, bufferSize: Long) = new BufferMetadata(Some(lastFrame), bufferSize)

}

