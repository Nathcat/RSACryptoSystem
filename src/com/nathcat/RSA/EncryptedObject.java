package com.nathcat.RSA;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class EncryptedObject implements Serializable {
    public final boolean flipSign;   // Determine whether the sign should be flipped
    public BigInteger object;        // The object as a big integer

    public EncryptedObject(boolean flipSign, BigInteger object) {
        this.flipSign = flipSign;
        this.object = object;
    }

    public EncryptedObject(Object object) {
        BigInteger o = new BigInteger(SerializeObject(object));

        if (o.compareTo(new BigInteger("0")) < 0) {
            this.flipSign = o.compareTo(new BigInteger("0")) < 0;
            this.object = o.abs();
        }
        else {
            this.flipSign = false;
            this.object = o;
        }
    }

    public BigInteger GetInteger() {
        if (this.flipSign) {
            return this.object.multiply(new BigInteger("-1"));
        }
        else {
            return this.object;
        }
    }

    public BigInteger GetNaturalNumber() {
        return this.object;
    }

    public Object GetObject() {
        BigInteger o = this.GetInteger();
        return DeserializeObject(o.toByteArray());
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
}
