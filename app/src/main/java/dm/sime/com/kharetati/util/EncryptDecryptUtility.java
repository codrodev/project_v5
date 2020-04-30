package dm.sime.com.kharetati.util;



import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class EncryptDecryptUtility {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static String decryptedString;
    private static String encryptedString;

    public static String DECODE_KEY="OTPG#$%055%&";
    public static void setKey(String myKey) {

        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key, "UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String decodepassword(String encodedPassword) {
        String decodedpassword = "";
        try {
            final String key1 = DECODE_KEY;
            String encodedString = "";
            EncryptDecryptUtility.setKey(key1);
            // encodedString=MpayBusinessObjects.getEncryptedString().trim();
            decodedpassword = EncryptDecryptUtility.decrypt(encodedPassword);
            decodedpassword = EncryptDecryptUtility.getDecryptedString();
            System.out.println(decodedpassword);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return decodedpassword;
    }

    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final String key1 = DECODE_KEY;
            String encodedString = "";
            EncryptDecryptUtility.setKey(key1);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            setEncryptedString(Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),Base64.DEFAULT));

        } catch (Exception e) {

            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;

    }

    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decode(strToDecrypt,Base64.DEFAULT))));

        } catch (Exception e) {

            System.out.println("Error while decrypting: " + e.toString());

        }
        return null;
    }

    public static String getDecryptedString() {
        return decryptedString;
    }

    public static void setDecryptedString(String decryptedString) {
        EncryptDecryptUtility.decryptedString = decryptedString;
    }

    public static String getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(String encryptedString) {
        EncryptDecryptUtility.encryptedString = encryptedString;
    }


    public static void main(String args[]){
        String enp=encrypt(">@yjNe");
        System.out.println(encryptedString);
        System.out.println(decrypt(encryptedString)+"=============="+decryptedString);
    }
}
