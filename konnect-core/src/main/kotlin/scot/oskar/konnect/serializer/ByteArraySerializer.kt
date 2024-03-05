package scot.oskar.konnect.serializer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Serializer implementation that serializes objects into byte arrays using Java serialization.
 */
class ByteArraySerializer : Serializer<ByteArray> {

    /**
     * Serializes an object into a byte array.
     *
     * @param obj The object to be serialized.
     * @return The byte array representing the serialized object.
     */
    override fun serialize(obj: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        objectOutputStream.flush()
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Deserializes a byte array into an object.
     *
     * @param data The byte array to be deserialized.
     * @return The deserialized object.
     */
    override fun deserialize(data: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(data)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        return objectInputStream.readObject()
    }
}