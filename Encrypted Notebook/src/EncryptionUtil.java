import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

// Simple helper for encrypting/decrypting note data. Used AI tools to help me with the first 3 classes.
public class EncryptionUtil {
    static final String CIPHER = "AES";
    static final byte[] SALT = "1234567887654321".getBytes();

    // Encrypts a plain UTF-8 string with the given password and returns a Base64 string
    public static String encrypt(String plain, String pass) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER);
        SecretKeySpec key = getKey(pass);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] enc = cipher.doFinal(plain.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(enc);
    }

    // Reverses encrypt(): decodes Base64 then decrypts using AES with the derived key
    public static String decrypt(String enc, String pass) throws Exception {
        byte[] encBytes = Base64.getDecoder().decode(enc);
        Cipher cipher = Cipher.getInstance(CIPHER);
        SecretKeySpec key = getKey(pass);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plain = cipher.doFinal(encBytes);
        return new String(plain, "UTF-8");
    }

    // Derives a 256-bit AES key from the password.
    private static SecretKeySpec getKey(String pass) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest((pass + new String(SALT)).getBytes("UTF-8"));
        return new SecretKeySpec(key, CIPHER);
    }
}
