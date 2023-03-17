package com.nathcat.RSA;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class EncryptedObject implements Serializable {
    public final ByteChunk[] byteChunks; // The byte chunks of this encrypted object

    public EncryptedObject(ByteChunk[] byteChunks) {
        this.byteChunks = byteChunks;
    }

    /**
     * Turn an object into an array of bytes
     * @param obj The object to serialize
     * @return A corresponding array of bytes, or null if a byte array could not be created
     */
    public static byte[] SerializeObject(Object obj) {
        // My process here is to pass the object through two input streams which handle different types of data.
        // At the lowest level of course they both handle binary data, hence they are compatible and can pass
        // data to each other.
        try {
            // Create a byte array output stream, this will allow us to get a byte array output from whatever we input into the stream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Now we create a object output stream as a wrapper over the byte array stream.
            // This means that when we pass an object into the object output stream, it will be passed straight into the byte array stream.
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // Writing the object to the object stream and flushing the changes
            oos.writeObject(obj);
            oos.flush();
            // We can now extract the byte array from the byte stream
            byte[] objBytes = baos.toByteArray();
            // ... and close the two streams
            oos.close();
            baos.close();

            return objBytes;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Turn an array of bytes into an object.
     * @param bytes The array of bytes to deserialize.
     * @return The resulting object, or null if no object could be created.
     */
    public static Object DeserializeObject(byte[] bytes) {
        // The process here is effectively the same as in the serialize object method, except in reverse.
        try {
            // Create the byte array INPUT stream from the byte array, this provides the stream with somewhere to get data from.
            ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
            // Create an object input stream as a wrapper of the byte array stream, this allows us to extract objects from the byte array stream.
            ObjectInputStream ois = new ObjectInputStream(baos);
            // Attempt to read an object from the stream.
            Object obj = ois.readObject();
            // Close the streams
            ois.close();
            baos.close();

            return obj;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Split an object into chunks of bytes, each chunk contains 64 bytes.
     * @param obj The object to split.
     * @return An array of ByteChunk objects.
     */
    public static ByteChunk[] SplitObjectToByteChunks(Object obj) {
        // Serialize the object into an array of bytes
        byte[] bytes = EncryptedObject.SerializeObject(obj);

        // Create two lists, one for sets of 64 bytes, and one for the current set of 64 bytes
        ArrayList<byte[]> byte64Chunks = new ArrayList<>();
        ArrayList<Byte> currentBytes = new ArrayList<>();

        for (byte aByte : bytes) {
            // Add a byte to the current bytes list
            currentBytes.add(aByte);

            // If the current bytes list has 64 bytes in it, create a byte array from this and add it to the set of 64 bytes,
            // then clear the current bytes list.
            if (currentBytes.size() == 64) {
                byte[] chunk = new byte[64];
                for (int b = 0; b < 64; b++) {
                    chunk[b] = currentBytes.get(b);
                }

                byte64Chunks.add(chunk);
                currentBytes.clear();
            }
        }

        // If the number of bytes was not a multiple of 64, there will be some bytes left over in the current bytes
        // array after this loop finishes, so create a byte array from the remaining bytes and add then to the set of 64 bytes.
        if ((bytes.length % 64) != 0) {
            byte[] chunk = new byte[currentBytes.size()];
            for (int b = 0; b < currentBytes.size(); b++) {
                chunk[b] = currentBytes.get(b);
            }

            byte64Chunks.add(chunk);
        }

        // Turn the set of 64 bytes into an array of ByteChunk objects
        ByteChunk[] result = new ByteChunk[byte64Chunks.size()];
        for (int i = 0; i < byte64Chunks.size(); i++) {
            result[i] = new ByteChunk(byte64Chunks.get(i));
        }

        return result;
    }

    /**
     * Turn an array of byte chunks in an object.
     * @param chunks The byte chunks to compile.
     * @return The resulting object, or null if one could not be created from the data.
     */
    public static Object CompileByteChunks(ByteChunk[] chunks) {
        // Get the byte arrays from each of the byte chunks
        ArrayList<byte[]> chunkArrays = new ArrayList<>();
        int byteCount = 0;
        for (ByteChunk chunk : chunks) {
            byte[] arr = chunk.GetByteArray();
            chunkArrays.add(arr);
            byteCount += arr.length;
        }

        // Create just a straight array of bytes by copying the chunk arrays into a single, new, empty array
        byte[] bytes = new byte[byteCount];
        int bytesPointer = 0;
        for (byte[] chunkArray : chunkArrays) {
            System.arraycopy(chunkArray, 0, bytes, bytesPointer, chunkArray.length);
            bytesPointer += chunkArray.length;
        }

        // Deserialize of bytes array and return the result
        return EncryptedObject.DeserializeObject(bytes);
    }

    /**
     * Combines byte arrays a and b. Array b is overlaid onto a from the end of a.
     * For example, let a = [1, 2, 3, 4, 5], b = [6, 7, 8]
     * The result will be [1, 2, 6, 7, 8]
     * @param a Array a
     * @param b Array b
     * @return The combined array
     */
    public static byte[] CombineByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];

        int lengthDifference = a.length - b.length;
        if (lengthDifference < 0) {
            throw new IllegalArgumentException("Array a should be larger than or the same size as array b!");
        }

        int cursor = 0;
        while (cursor < lengthDifference) {
            result[cursor] = a[cursor];
            cursor++;
        }

        while ((cursor - lengthDifference) < b.length) {
            result[cursor] = b[cursor - lengthDifference];
            cursor++;
        }

        return result;
    }
}


