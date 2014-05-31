package pl.lolczak.buffer.fs

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, RandomAccessFile, File}
import pl.lolczak.buffer.MediaCyclicBuffer
import pl.lolczak.io.stream.{SerializerInputStream, SerializerOutputStream, Serializer}
import com.typesafe.scalalogging.slf4j.Logging
import scala.annotation.tailrec

/**
 *
 * In Android, FileLock works between processes, but does not work between threads in a process.
 *
 * @author Lukasz Olczak
 */
class FsCyclicBuffer[A](private val location: File, private val bufferSize: Long)
                       (implicit private val serializer: Serializer[A]) extends MediaCyclicBuffer[A] with Logging {

  private val frameSerializer = new BufferFrameSerializer[A]()

  @volatile
  private var metadata: BufferMetadata = EmptyBufferMetadata(bufferSize)

  private val file: RandomAccessFile = createFile()

  private def createFile() = {
    logger.info(s"Initializing buffer in $location with size $metadata.effectiveSize")
    if (!location.exists()) {
      location.createNewFile()
    } else {
      //todo
      //      throw new NotImplementedError()
    }
    new RandomAccessFile(location, "rws")
  }

  private def persistHeader() {
    val headerBytes = new ByteArrayOutputStream()
    val outStream = new SerializerOutputStream(headerBytes)
    BufferHeaderSerializer.serialize(metadata, outStream)
    val bytes = headerBytes.toByteArray
    file.seek(0)
    file.write(bytes)
    outStream.close()
  }

  override def getLast(max: Int): List[A] = {
    metadata.lastFrameOption match {
      case None => Nil
      case Some(lastFrame) => readFrames(lastFrame, max, Nil).map(_.content)
    }
  }

  @tailrec
  private def readFrames(frame: FrameInfo, max: Int, acu: List[BufferFrame[A]]): List[BufferFrame[A]] =
    if (max == 0) acu
    else {
      val headFrame = readFrame(frame)
      val newAcu = headFrame :: acu
      headFrame.previousFrame match {
        case None => newAcu
        case Some(previousFrame) =>
          if (isNextFrameOverwritten(frame.offset, previousFrame.offset))
            newAcu
          else
            readFrames(previousFrame, max - 1, newAcu)
      }
    }

  private def isNextFrameOverwritten(offset: Long, prev: Long): Boolean = {
    if (prev < offset) {
      metadata.currentFrameOffset > prev && metadata.currentFrameOffset <= offset
    } else {
      //overlapping occurred
      metadata.currentFrameOffset > prev
    }
  }

  private def readFrame(frameInfo: FrameInfo): BufferFrame[A] = {
    val buffer = new Array[Byte](frameInfo.size.asInstanceOf[Int])
    file.seek(frameInfo.offset)
    file.read(buffer)
    val byteStream = new ByteArrayInputStream(buffer)
    val inStream = new SerializerInputStream(byteStream)
    frameSerializer.deserialize(inStream)
  }

  override def put(element: A): Unit = {
    val frame = new BufferFrame[A](metadata.lastFrameOption, element)
    val bytes = toBinary(frame)
    file.seek(metadata.currentFrameOffset)
    file.write(bytes)
    metadata = metadata shiftOffsetBy bytes.length
    persistHeader()
  }

  private def toBinary(frame: BufferFrame[A]) = {
    val bytesStream = new ByteArrayOutputStream()
    val outStream = new SerializerOutputStream(bytesStream)
    frameSerializer.serialize(frame, outStream)
    bytesStream.toByteArray
  }

  override def size: Long = bufferSize //todo remove it

}
