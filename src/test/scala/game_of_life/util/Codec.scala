package game_of_life.util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

trait Codec {

  def encode(obj: AnyRef): Array[Byte]

  def decode[A >: Null](array: Array[Byte]): A
}

trait SerializationCodec extends Codec {

  def encode(obj: AnyRef): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    try {
      oos writeObject obj
    } finally {
      oos close ()
    }
    baos.toByteArray
  }

  def decode[A >: Null](array: Array[Byte]): A = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(array))
    try {
      (ois readObject ()).asInstanceOf[A]
    } finally {
      ois close ()
    }
  }
}

object SerializationCodec extends SerializationCodec
