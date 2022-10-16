package com.nathcat.RSA;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Contains data for an RSA encryption public key
 *
 * @author Nathan "Nathcat" Baines
 */

public class PublicKey implements Serializable {
    public final BigInteger n;
    public final BigInteger e;

    public PublicKey(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }
}
