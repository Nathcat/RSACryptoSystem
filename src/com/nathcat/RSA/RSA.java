package com.nathcat.RSA;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Methods for generating and using RSA asymmetric encryption keys.
 *
 * @author Nathan "Nathcat" Baines
 */

public class RSA {
    /**
     * Generate an RSA public key/private key pair.
     * @return An RSA key pair
     * @throws NoSuchAlgorithmException Thrown by crypto-secure generation of random integers
     */
    public static KeyPair GenerateRSAKeyPair() throws NoSuchAlgorithmException {
        // Generate two random 2048-bit prime numbers
        BigInteger p = new BigInteger(2048, 1, new SecureRandom());
        BigInteger q = new BigInteger(2048, 1, new SecureRandom());

        // n = p * q
        BigInteger n = p.multiply(q);
        // Standard value for e (public key exponent) is 65537
        BigInteger e = new BigInteger("65537");
        // ed = 1 (mod (p - 1) * (q - 1)), d is the private key exponent
        BigInteger d = e.modInverse(p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

        return new KeyPair(
                new PublicKey(n, e),
                new PrivateKey(n, d)
        );
    }
}
