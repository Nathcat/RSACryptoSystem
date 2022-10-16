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

    /**
     * Convert a String into a BigInteger array for RSA processing
     * @param message The String to be converted
     * @return The resulting BigInteger array
     */
    public static BigInteger[] StringToBigIntegerArray(String message) {
        BigInteger[] result = new BigInteger[message.length()];

        for (int i = 0; i < message.length(); i++) {
            result[i] = BigInteger.valueOf((int) message.charAt(i));
        }

        return result;
    }

    /**
     * Convert a BigInteger array to a String
     * @param message The BigInteger array to be converted
     * @return The resulting String
     */
    public static String BigIntegerArrayToString(BigInteger[] message) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < message.length; i++) {
            sb.append((char) message[i].intValueExact());
        }

        return sb.toString();
    }
}
