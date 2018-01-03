package base.utils.codec;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by cl on 2017/11/26.
 * AES加密、解密工具类
 */
public class AESUtils {

    private final static String DEFAULT_CHARSET = "UTF-8";

    private AESUtils() {
    }

    /**
     * 对字符串进行AES加密，默认UTF-8字符集
     */
    public static String encode(String str, String key) {
        return encode(str, key, DEFAULT_CHARSET);
    }

    /**
     * 对字符串进行AES解密，默认UTF-8字符集
     */
    public static String decode(String str, String key) {
        return decode(str, key, DEFAULT_CHARSET);
    }

    /**
     * 对字符串进行AES加密
     */
    public static String encode(String str, String key, String charset) {
        try {
            byte[] bytes = encode(str.getBytes(charset), key.getBytes(charset));
            return Base64Utils.encode(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 对字符串进行AES解密
     */
    public static String decode(String str, String key, String charset) {
        try {
            byte[] bytes = decode(Base64Utils.decode(str), key.getBytes(charset));
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }


    public static byte[] encode(byte[] source, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(generateKey(key).getEncoded(), "AES"));

        return cipher.doFinal(source);
    }

    public static byte[] decode(byte[] bytes, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(generateKey(key).getEncoded(), "AES"));

        return cipher.doFinal(bytes);
    }

    /**
     * 生成key，通过指定seed，防止在linux下报错
     */
    private static SecretKey generateKey(byte[] key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key);
        kgen.init(128, secureRandom);
        return kgen.generateKey();
    }

}
