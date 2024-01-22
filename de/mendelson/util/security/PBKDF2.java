//$Header: /as2/de/mendelson/util/security/PBKDF2.java 2     24/08/22 12:56 Heller $
package de.mendelson.util.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Allows to generate hash values and validating passwords against these hashes,
 * hashes are stored using the PBKDF2 algorithm
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class PBKDF2 {

    private static final String DELIMITER = "#";
    /**
     * Increase this value in the future if the computers are faster
     */
    private static final int GENERATION_ITERATIONS = 100000;

    /**
     * Validates a passed raw password against a stored has
     *
     * @param originalPassword The raw password
     */
    public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(DELIMITER);
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = hexToByteArray(parts[1]);
        byte[] hash = hexToByteArray(parts[2]);
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = keyFactory.generateSecret(spec).getEncoded();
        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return (diff == 0);
    }

    private static byte[] hexToByteArray(String hexStr) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexStr.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    /**
     * Generates a string that contains the password hash with some additional
     * information in the following format: iterations#salt#hash
     *
     *
     * @param password Password to generate the password hash from
     * @return String that contains the hash
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = password.toCharArray();
        byte[] salt = generateSalt().getBytes();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, GENERATION_ITERATIONS, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return (GENERATION_ITERATIONS + DELIMITER + byteArrayToHex(salt) + DELIMITER + byteArrayToHex(hash));
    }

    private static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return (new String(salt));
    }

    private static String byteArrayToHex(byte[] byteArray) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, byteArray);
        String hex = bi.toString(16);
        int paddingLength = (byteArray.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String originalPassword = "password";
        String wrongPassword = "wrongpassword";
        long time1 = System.currentTimeMillis();
        String generatedSecuredPasswordHash = generateStrongPasswordHash(originalPassword);
        System.out.println("Generated hash in " + (System.currentTimeMillis() - time1) + "ms");
        System.out.println("Generated passwd hash: " + generatedSecuredPasswordHash);
        time1 = System.currentTimeMillis();
        boolean checkPasswdMatch = validatePassword(originalPassword, generatedSecuredPasswordHash);
        System.out.println("Validated password in " + (System.currentTimeMillis() - time1) + "ms");
        System.out.println("Entry " + originalPassword + " matched: " + checkPasswdMatch);
        time1 = System.currentTimeMillis();
        checkPasswdMatch = validatePassword(wrongPassword, generatedSecuredPasswordHash);
        System.out.println("Validated password in " + (System.currentTimeMillis() - time1) + "ms");
        System.out.println("Entry " + wrongPassword + " matched: " + checkPasswdMatch);
    }

}
