package pl.lolczak.buffer.fs

/**
 *
 *
 * @author Lukasz Olczak
 */
class BufferFrame[A](val previousFrame: Option[FrameInfo], val content: A) {

}

