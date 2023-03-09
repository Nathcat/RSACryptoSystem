package com.nathcat.RSA;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Represents a chunk of 64 bytes
 */
public class ByteChunk implements Serializable {
    private final byte[] lostBytes;  // The lost bytes in the conversion from byte array to integer
    public BigInteger integer;       // The big integer created from the original bytes array
    private final boolean flipSign;  // Will the integer sign need to be changed?

    public ByteChunk(byte[] bytes) {
        this.integer = new BigInteger(bytes);
        this.lostBytes = new byte[bytes.length];

        if (bytes.length - this.integer.toByteArray().length >= 0)
            System.arraycopy(bytes, 0, lostBytes, 0, bytes.length - this.integer.toByteArray().length);

        if (this.integer.compareTo(BigInteger.ZERO) < 0) {
            this.flipSign = true;
            this.integer = this.integer.abs();
        } else {
            this.flipSign = false;
        }
    }

    /**
     * Create a from this chunk, taking into account the number of leading zeros in the original byte array
     *
     * @return The byte array
     */
    public byte[] GetByteArray() {
        // Flip the sign of the integer if required
        if (this.flipSign) this.integer = this.integer.multiply(BigInteger.valueOf(-1));

        return EncryptedObject.CombineByteArrays(this.lostBytes, this.integer.toByteArray());
    }

    public String toString() {
        return Arrays.toString(this.GetByteArray());
    }
}
