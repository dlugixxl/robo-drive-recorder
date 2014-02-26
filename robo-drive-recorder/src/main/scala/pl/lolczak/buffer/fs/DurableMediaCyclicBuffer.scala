package pl.lolczak.buffer.fs

import java.io.{ByteArrayOutputStream, RandomAccessFile, File}
import pl.lolczak.buffer.{BufferTooSmallException, MediaCyclicBuffer}
import pl.lolczak.io.stream.{SerializerOutputStream, Serializer}
import pl.lolczak.buffer.fs.SerializationConstants._
import DurableMediaCyclicBuffer._
import com.typesafe.scalalogging.slf4j.Logging
import pl.lolczak.concurrent.lock.{RangeLockManager, RangeLock, LockManager}
import java.nio.channels.FileChannel
import pl.lolczak.concurrent.lock.{WriteLock, Range}
import java.nio.ByteBuffer

/**
 *
 * In Android, FileLock works between processes, but does not work between threads in a process.
 *
 * @author Lukasz Olczak
 */
class DurableMediaCyclicBuffer[A](private val location: File, private val bufferSize: Long)
                                 (implicit private val serializer: Serializer[A]) extends MediaCyclicBuffer[A] with Logging {

  private val effectiveSize = bufferSize - HeaderLength

  private val headerRange = new Range(0, HeaderLength)

  private var lastOffset: Long = _

  private var currentOffset: Long = _

  private var file: RandomAccessFile = _

  private val lockManager: LockManager[RangeLock] = new RangeLockManager

  private var writeChannel: FileChannel = _

  init()

  private def init() = {
    logger.info(s"Initializing buffer in $location with size $effectiveSize")
    if (!location.exists()) {
      location.createNewFile()
    }
    file = new RandomAccessFile(location, "rw")
    writeChannel = file.getChannel

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

  override def getLast(max: Int): List[A] = {
    if (lastOffset == NullOffset) Nil
    else {
      val frames = readFrames(lastOffset, max).reverse

      frames.map(frame => serializer.deserialize(frame.content))
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
    val frameBytes = BufferFrameSerializer.serialize(frame)
    val lock = lockManager.acquireLocks(WriteLock(headerRange), WriteLock(currentOffset, frameBytes.length))
    try {
      //      file.seek(currentOffset)
      //      frame.writeTo(file)
      writeChannel.write(ByteBuffer.wrap(frameBytes), currentOffset)
      updateOffset(frame.size)
      writeChannel.force(false)
    } finally {
      lock.release()
    }
  }

  private def updateOffset(frameSize: Int) {
    lastOffset = currentOffset
    currentOffset += frameSize


    val bytes = new ByteArrayOutputStream(8)
    val outStream = new SerializerOutputStream(bytes)
    outStream.writeLong(lastOffset)
    writeChannel.write(ByteBuffer.wrap(bytes.toByteArray), currentOffset)
    //    file.seek(0)
    //    file.writeLong(lastOffset)
  }

  override def size: Long = bufferSize

}

private object DurableMediaCyclicBuffer {

  val HeaderLength = 8

}
