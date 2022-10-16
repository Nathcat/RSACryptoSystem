package com.nathcat.RSA;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Contains data for an RSA encryption private key
 *
 * @author Nathan "Nathcat" Baines
 */

public class PrivateKey implements Serializable {
    public final BigInteger n;
    public final BigInteger d;

    public PrivateKey(BigInteger n, BigInteger d) {
        this.n = n;
        this.d = d;
    }
}
