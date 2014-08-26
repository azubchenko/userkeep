package com.handycraft.userkeep.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by stanlytwiddle on 6/2/14.
 */
public class DataEncryptor {

    static final String PREF_APP = "pref_app";
    static final String PREF_SALT = "pref_salt";

    private byte[] salt;
    private String deviceId;
    private DecriptorEncriptor decriptorEncriptor;

    public DataEncryptor(Context context) {
        initSalt(context);
        initDeviceId(context);
        initDecriptor(deviceId, salt);
    }

    private void initSalt(Context context) {
        try {

            SharedPreferences prefs = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
            String base64Salt = prefs.getString(PREF_SALT, null);
            if (base64Salt == null) {
                SecretKey secretKey = DataEncryptor.DecriptorEncriptor.generateSalt();
                base64Salt = Base64.encodeToString(secretKey.getEncoded(), Base64.NO_WRAP);
                salt = secretKey.getEncoded();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_SALT, base64Salt);
                editor.commit();
            } else {
                salt = Base64.decode(base64Salt, Base64.NO_WRAP);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initDeviceId(Context context) {
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void initDecriptor(String deviceId, byte[] salt) {
        decriptorEncriptor = new DecriptorEncriptor(deviceId, salt);
    }

    public String encrypt(String data) {
        //encoding
        return decriptorEncriptor.encrypt(data);
    }

    public String decrypt(String encodedData) {
        //encoding
        return decriptorEncriptor.decrypt(encodedData);
    }


    public static class DecriptorEncriptor {
        static private final String ALGORITHM = "AES";
        Cipher ecipher;
        Cipher dcipher;
        SecretKey key;
        private String deviceId;
        private byte[] salt;

        public DecriptorEncriptor(String deviceId, byte[] salt) {

            this.deviceId = deviceId;
            this.salt = salt;
            try {

                key = generateKey();

                ecipher = Cipher.getInstance(ALGORITHM);
                ecipher.init(Cipher.ENCRYPT_MODE, key);

                dcipher = Cipher.getInstance(ALGORITHM);
                dcipher.init(Cipher.DECRYPT_MODE, key);
            } catch (javax.crypto.NoSuchPaddingException e) {

            } catch (java.security.NoSuchAlgorithmException e) {

            } catch (java.security.InvalidKeyException e) {

            } catch (Exception e) {

            }
        }

        public static SecretKey generateSalt() throws NoSuchAlgorithmException {
            // Generate a 256-bit key
            final int outputKeyLength = 256;

            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(outputKeyLength, secureRandom);
            SecretKey key = keyGenerator.generateKey();
            return key;
        }

        public SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
            // Number of PBKDF2 hardening rounds to use. Larger values increase
            // computation time. You should select a value that causes computation
            // to take >100ms.
            final int iterations = 1000;

            // Generate a 256-bit key
            final int outputKeyLength = 256;

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec((deviceId).toCharArray(), salt, iterations, outputKeyLength);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            return secretKey;
        }

        public String decrypt(String str) {
            try {
                // Decode base64 to get bytes
                byte[] dec = Base64.decode(str, Base64.NO_WRAP);

                // Decrypt
                byte[] utf8 = dcipher.doFinal(dec);

                // Decode using utf-8
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (Exception e) {
            }
            return "";
        }

        public String encrypt(String str) {
            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");

                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);

                // Encode bytes to base64 to get a string
                String result = Base64.encodeToString(enc, Base64.NO_WRAP);

                return result;
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (Exception e) {
            }
            return "";
        }
    }
}
