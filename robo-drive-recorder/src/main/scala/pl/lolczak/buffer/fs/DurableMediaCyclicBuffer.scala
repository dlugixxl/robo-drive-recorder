package pl.lolczak.buffer.fs

import java.io.{RandomAccessFile, File}
import pl.lolczak.buffer.{BufferTooSmallException, MediaCyclicBuffer}
import pl.lolczak.io.stream.Serializer
import scala.util.Try
import pl.lolczak.buffer.fs.SerializationConstants._
import scala.util.Success
import scala.util.Failure
import scala.Some
import DurableMediaCyclicBuffer._
import com.typesafe.scalalogging.slf4j.Logging

/**
 *
 *
 * @author Lukasz Olczak
 */
class DurableMediaCyclicBuffer[A](private val location: File, private val bufferSize: Long)
                                 (implicit private val serializer: Serializer[A]) extends MediaCyclicBuffer[A] with Logging {

  private val effectiveSize = bufferSize - HeaderLength

  private var lastOffset: Long = _

  private var currentOffset: Long = _

  private var file: RandomAccessFile = _

  init()

  private def init() = {
    logger.info(s"Initializing buffer in $location with size $effectiveSize")
    if (!location.exists()) {
      location.createNewFile()
    }
    file = new RandomAccessFile(location, "rws")
    initStructure()
  }

  private def initStructure() = {
    file.setLength(bufferSize)
    file.seek(0)
    file.writeLong(NullOffset)
    lastOffset = NullOffset
    currentOffset = HeaderLength
    logger.debug(s"Buffer $location initialized")
  }

  override def getLast(): Option[A] = {
    if (lastOffset == NullOffset) None
    else readLastChunk() match {
      case Success(e) => Some(e)
      case Failure(_) => None
    }
  }

  private def readLastChunk(): Try[A] = {
    file.seek(lastOffset)
    val frame = BufferFrame.readFrom(file)

    serializer.deserialize(frame.content)
  }

  override def getLast(max: Int): Option[List[A]] = {
    if (lastOffset == NullOffset) None
    else {
      val frames = readFrames(lastOffset, max).reverse

      val elements = frames.map(frame => serializer.deserialize(frame.content))
      val (successes, failures) = elements.partition(_.isSuccess)
      if (failures.isEmpty) Some(successes.map(_.get))
      else None
    }
  }

  private def readFrames(offset: Long, max: Int): List[BufferFrame] =
    if (max == 0) Nil
    else
    if (offset == NullOffset) Nil
    else {
      val headFrame = readFrame(offset)
      if (isNextFrameOverwritten(offset, headFrame.previousFrameOffset))
        headFrame :: Nil
      else
        headFrame :: readFrames(headFrame.previousFrameOffset, max - 1)
    }

  private def isNextFrameOverwritten(offset: Long, prev: Long): Boolean = {
    if (prev < offset) {
      currentOffset > prev && currentOffset <= offset
    } else {
      //overlapping occurred
      currentOffset > prev
    }
  }

  private def readFrame(offset: Long) = {
    file.seek(offset)
    BufferFrame.readFrom(file)
  }

  override def put(element: A): Unit = {
    val elementBytes = serializer.serialize(element)
    if (elementBytes.length > effectiveSize) {
      throw new BufferTooSmallException(bufferSize = bufferSize, elementSize = elementBytes.length)
    }
    val frame = new BufferFrame(lastOffset, elementBytes)
    val nextOffset = currentOffset + frame.size
    if (nextOffset > bufferSize) {
      currentOffset = HeaderLength
      logger.info("Buffer overlapped")
    }
    file.seek(currentOffset)
    frame.writeTo(file)
    updateOffset(frame.size)
  }

  private def updateOffset(frameSize: Int) {
    lastOffset = currentOffset
    currentOffset += frameSize
    file.seek(0)
    file.writeLong(lastOffset)
  }

  override def size: Long = bufferSize

}

private object DurableMediaCyclicBuffer {

  val HeaderLength = 8

}
