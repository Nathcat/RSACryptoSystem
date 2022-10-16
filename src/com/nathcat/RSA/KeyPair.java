package com.nathcat.RSA;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Contains a pair of RSA encryption keys
 *
 * @author Nathan "Nathcat" Baines
 */

public class KeyPair implements Serializable {
    public PublicKey pub = null;
    public PrivateKey pri = null;

    public KeyPair(PublicKey pub, PrivateKey pri) {
        this.pub = pub;
        this.pri = pri;
    }

    /**
     * Give a string representation of this KeyPair
     * @return string representation of this KeyPair
     */
    public String toString() {
        String result = "";
        if (this.pub != null) {
            result += "Public key----------\nn = " + this.pub.n.toString() + "\ne = " + this.pub.e.toString();
        }

        if (this.pub != null && this.pri != null) {
            result += "\n\n";
        }

        if (this.pri != null) {
            result += "Private Key-----\nn = " + this.pri.n.toString() + "\nd = " + this.pri.d.toString();
        }

        return result;
    }

    /**
     * Encrypt a BigInteger array
     * @param message The BigInteger array to encrypt
     * @return Encrypted BigInteger array
     * @deprecated Use object encryption methods instead
     */
    public BigInteger[] encrypt(BigInteger[] message) throws PublicKeyException {
        if (this.pub == null) {
            throw new PublicKeyException();
        }

        BigInteger[] result = new BigInteger[message.length];

        for (int i = 0; i < message.length; i++) {
            result[i] = (message[i].modPow(this.pub.e, this.pub.n));
        }

        return result;
    }

    /**
     * Decrypt a BigInteger array
     * @param message The BigInteger array to decrypt
     * @return The decrypted BigInteger array
     * @deprecated Use object encryption methods instead
     */
    public BigInteger[] decrypt(BigInteger[] message) throws PrivateKeyException {
        if (this.pri == null) {
            throw new PrivateKeyException();
        }

        BigInteger[] result = new BigInteger[message.length];

        for (int i = 0; i < message.length; i++) {
            result[i] = (message[i].modPow(this.pri.d, this.pri.n));
        }

        return result;
    }

    /**
     * Encrypt an object
     * @param message The object to encrypt
     * @return The encrypted object
     * @throws PublicKeyException Thrown if this pair has no public key
     */
    public EncryptedObject encrypt(Object message) throws PublicKeyException {
        if (this.pub == null) {
            throw new PublicKeyException();
        }

        BigInteger cipherNum = new EncryptedObject(message)
                .GetNaturalNumber()
                .modPow(this.pub.e, this.pub.n);

        return new EncryptedObject(new EncryptedObject(message).flipSign, cipherNum);
    }

    /**
     * Decrypt an object
     * @param message The object to decrypt
     * @return The decrypted object
     * @throws PrivateKeyException Thrown if this pair has no private key
     */
    public Object decrypt(EncryptedObject message) throws PrivateKeyException {
        if (this.pri == null) {
            throw new PrivateKeyException();
        }

        BigInteger plain = message.GetNaturalNumber().modPow(this.pri.d, this.pri.n);
        message.object = plain;

        return message.GetObject();
    }

    /**
     * Encrypt a large object.
     * @param message The object to encrypt
     * @return Array of encrypted objects
     * @throws PublicKeyException Thrown if the public key on this object is null
     */
    public EncryptedObject[] encryptBigObject(Object message) throws PublicKeyException {
        if (this.pub == null) {
            throw new PublicKeyException();
        }

        ObjectContainer container = new ObjectContainer(message);
        BigInteger[] numArray = EncryptedObject.ObjectToNumArray(container);

        EncryptedObject[] result = new EncryptedObject[numArray.length];
        for (int i = 0; i < numArray.length; i++) {
            result[i] = this.encrypt(numArray[i]);
        }

        return result;
    }

    /**
     * Decrypt a large object
     * @param message The array of encrypted objects you wish to decrypt
     * @return The resulting object
     * @throws PrivateKeyException Thrown if the private key on this object is null
     */
    public Object decryptBigObject(EncryptedObject[] message) throws PrivateKeyException {
        if (this.pri == null) {
            throw new PrivateKeyException();
        }

        BigInteger[] numArray = new BigInteger[message.length];
        for (int i = 0; i < message.length; i++) {
            numArray[i] = (BigInteger) this.decrypt(message[i]);
        }

        return ((ObjectContainer) EncryptedObject.NumArrayToObject(numArray)).obj;
    }
}