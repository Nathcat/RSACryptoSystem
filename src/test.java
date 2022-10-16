import com.nathcat.RSA.*;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class test {
    public static void main(String[] args) throws NoSuchAlgorithmException, PublicKeyException, PrivateKeyException, IOException {
        KeyPair pair = RSA.GenerateRSAKeyPair();

        ObjectContainer obj = new ObjectContainer("Hello world");
        System.out.println("Start: " + obj.obj);

        byte[] objBytes = EncryptedObject.SerializeObject(obj);
        assert objBytes != null;
        System.out.println(Arrays.toString(objBytes));

        BigInteger num = new BigInteger(objBytes);
        boolean flipSign = false;
        System.out.println("Original: " + num);

        if (num.compareTo(BigInteger.ZERO) < 0) {
            num = num.abs();
            flipSign = true;
        }

        System.out.println("Encrypting with: " + num);

        BigInteger cipherNum = num.modPow(pair.pub.e, pair.pub.n);
        System.out.println("CipherNum: " + cipherNum);

        BigInteger plainNum = cipherNum.modPow(pair.pri.d, pair.pri.n);
        System.out.println("PlainNum: " + plainNum);

        if (flipSign) {
            plainNum = plainNum.multiply(BigInteger.valueOf(-1));
        }

        System.out.println("Final plain num: " + plainNum);
        objBytes = plainNum.toByteArray();
        System.out.println("Final byte array: " + Arrays.toString(objBytes));

        obj = (ObjectContainer) EncryptedObject.DeserializeObject(objBytes);
        System.out.println("Result: " + obj.obj);
    }
}