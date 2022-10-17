package com.nathcat.RSA;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class EncryptedObject implements Serializable {
    public final ByteChunk[] byteChunks;

    public EncryptedObject(ByteChunk[] byteChunks) {
        this.byteChunks = byteChunks;
    }

    public static byte[] SerializeObject(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            byte[] objBytes = baos.toByteArray();
            oos.close();
            baos.close();

            return objBytes;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object DeserializeObject(byte[] bytes) {
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(baos);
            Object obj = ois.readObject();
            ois.close();
            baos.close();

            return obj;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BigInteger[] ObjectToNumArray(Object obj) {
        byte[] byteArray = EncryptedObject.SerializeObject(obj);
        assert byteArray != null;

        byte[] currentNum = new byte[256];
        ArrayList<BigInteger> numArrayList = new ArrayList<>();

        for (int i = 0; i < byteArray.length; i++) {
            currentNum[i % 256] = byteArray[i];

            if ((i + 1) % 256 == 0) {
                numArrayList.add(new BigInteger(currentNum));
                currentNum = new byte[256];
            }
        }

        if (byteArray.length % 256 != 0) {
            numArrayList.add(new BigInteger(currentNum));
        }

        Object[] arr = numArrayList.toArray();
        BigInteger[] result = new BigInteger[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (BigInteger) arr[i];
        }

        return result;
    }

    public static Object NumArrayToObject(BigInteger[] arr) {
        byte[] byteArray = new byte[arr.length * 256];
        int byteCounter = 0;
        for (BigInteger num : arr) {
            byte[] currentNum = num.toByteArray();
            for (int i = 0; i < currentNum.length; i++) {
                byteArray[byteCounter] = currentNum[i];
                byteCounter++;
            }
        }

        return EncryptedObject.DeserializeObject(byteArray);
    }

    public static ByteChunk[] SplitObjectToByteChunks(Object obj) {
        byte[] bytes = EncryptedObject.SerializeObject(obj);

        ArrayList<byte[]> byte64Chunks = new ArrayList<>();
        ArrayList<Byte> currentBytes = new ArrayList<>();
        for (byte aByte : bytes) {
            currentBytes.add(aByte);

            if (currentBytes.size() == 64) {
                byte[] chunk = new byte[64];
                for (int b = 0; b < 64; b++) {
                    chunk[b] = currentBytes.get(b);
                }

                byte64Chunks.add(chunk);
                currentBytes.clear();
            }
        }

        if ((bytes.length % 64) != 0) {
            byte[] chunk = new byte[currentBytes.size()];
            for (int b = 0; b < currentBytes.size(); b++) {
                chunk[b] = currentBytes.get(b);
            }

            byte64Chunks.add(chunk);
        }

        ByteChunk[] result = new ByteChunk[byte64Chunks.size()];
        for (int i = 0; i < byte64Chunks.size(); i++) {
            result[i] = new ByteChunk(byte64Chunks.get(i));
        }

        return result;
    }

    public static Object CompileByteChunks(ByteChunk[] chunks) {
        ArrayList<byte[]> chunkArrays = new ArrayList<>();
        int byteCount = 0;
        for (ByteChunk chunk : chunks) {
            byte[] arr = chunk.GetByteArray();
            chunkArrays.add(arr);
            byteCount += arr.length;
        }

        byte[] bytes = new byte[byteCount];
        int bytesPointer = 0;
        for (byte[] chunkArray : chunkArrays) {
            System.arraycopy(chunkArray, 0, bytes, bytesPointer, chunkArray.length);
            bytesPointer += chunkArray.length;
        }

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


