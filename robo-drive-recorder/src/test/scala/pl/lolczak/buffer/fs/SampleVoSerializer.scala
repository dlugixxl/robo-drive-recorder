package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializerOutputStream, SerializerInputStream, Serializer}
import scala.util.{Success, Failure, Try}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import scala.util.control.NonFatal

/**
 *
 *
 * @author Lukasz Olczak
 */

object SampleVoSerializer extends Serializer[SampleVo] {

  def deserialize(bytes: Array[Byte]): Try[SampleVo] = {
    try {
      val inStream = new SerializerInputStream(new ByteArrayInputStream(bytes))
      val className = inStream.readString()
      if (className != "SampleVo") {
        return Failure(new Exception("Bad preamble"))
      }

      val name = inStream.readString()
      val age = inStream.readInt()
      val mature = inStream.readBoolean()

      Success(new SampleVo(name, age, mature))
    } catch {
      case NonFatal(ex) => Failure(ex)
    }
  }

  def serialize(element: SampleVo): Array[Byte] = {
    val bytes = new ByteArrayOutputStream()
    val outStream = new SerializerOutputStream(bytes)
    outStream.writeString("SampleVo")
    outStream.writeString(element.name)
    outStream.writeInt(element.age)
    outStream.writeBoolean(element.mature)
    outStream.flush()
    bytes.toByteArray
  }

}

