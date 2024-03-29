package scot.oskar.konnect.serializer

/**
 * Interface for serializing and deserializing objects.
 */
interface Serializer<T> {
    /**
     * Serializes an object into a byte array.
     *
     * @param obj The object to be serialized.
     * @return The byte array representing the serialized object.
     */
    fun serialize(obj: Any): T

    /**
     * Deserializes a byte array into an object.
     *
     * @param data The byte array to be deserialized.
     * @return The deserialized object.
     */
    fun deserialize(data: T): Any
}