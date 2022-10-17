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
     * Encrypt an object
     * @param obj The object to encrypt
     * @return The encypted object
     * @throws PublicKeyException Thrown if the public key is null
     */
    public EncryptedObject encrypt(Object obj) throws PublicKeyException {
        if (this.pub == null) {
            throw new PublicKeyException();
        }

        ByteChunk[] chunks = EncryptedObject.SplitObjectToByteChunks(obj);
        for (int i = 0; i < chunks.length; i++) {
            chunks[i].integer = chunks[i].integer.modPow(this.pub.e, this.pub.n);
        }

        return new EncryptedObject(chunks);
    }

    /**
     * Decrypt an object
     * @param obj The object to decrypt
     * @return The original object
     * @throws PrivateKeyException Thrown if the private key is null
     */
    public Object decrypt(EncryptedObject obj) throws PrivateKeyException {
        if (this.pri == null) {
            throw new PrivateKeyException();
        }

        ByteChunk[] chunks = obj.byteChunks;
        for (int i = 0; i < chunks.length; i++) {
            chunks[i].integer = chunks[i].integer.modPow(this.pri.d, this.pri.n);
        }

        return EncryptedObject.CompileByteChunks(chunks);
    }
}