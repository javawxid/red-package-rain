package com.yunxi.user.util;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES工具
 */
public class AESUtils {

    /**
     * key 密钥 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位
     */
    private static final String key="1234567812345678";
    /**
     * iv 偏移量，长度16
     */
    private static final String iv ="1234567887654321";

    public static String encryptHexString(String plaintext){
        return encryptHex(plaintext,key,iv);
    }

    public static String decryptHexString(String ciphertext){
        return decryptHex(ciphertext,key,iv);
    }

    /**
     * AES加密（16进制）
     *
     * @param plaintext 明文（UTF-8字符串）
     * @param key       密钥
     * @param iv        向量（16位）
     * @return 密文（16进制）
     */
    public static String encryptHex(String plaintext, String key, String iv) {
        // 检查参数
        if (plaintext == null || "".equals((plaintext = plaintext.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (key == null || "".equals((key = key.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (iv == null || "".equals((iv = iv.trim().replaceAll("\\s", ""))) || iv.length() != 16) {
            return null;
        }

        try {
            return bytes2HexStr(encrypt(plaintext.getBytes("utf-8"), key.getBytes("utf-8"), iv.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密（16进制）
     *
     * @param ciphertext 密文（16进制）
     * @param key        密钥
     * @param iv         向量（16位）
     * @return 明文（UTF-8字符串）
     */
    public static String decryptHex(String ciphertext, String key, String iv) {
        // 检查参数
        if (ciphertext == null || "".equals((ciphertext = ciphertext.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (key == null || "".equals((key = key.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (iv == null || "".equals((iv = iv.trim().replaceAll("\\s", ""))) || iv.length() != 16) {
            return null;
        }

        try {
            return new String(decrypt(hexStr2Bytes(ciphertext), key.getBytes("utf-8"), iv.getBytes("utf-8")), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES加密（Base64）
     *
     * @param plaintext 明文（UTF-8字符串）
     * @param key       密钥
     * @param iv        向量（16位）
     * @return 密文（Base64）
     */
    public static String encryptBase64(String plaintext, String key, String iv) {
        // 检查参数
        if (plaintext == null || "".equals((plaintext = plaintext.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (key == null || "".equals((key = key.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (iv == null || "".equals((iv = iv.trim().replaceAll("\\s", ""))) || iv.length() != 16) {
            return null;
        }

        try {
            return Base64.getEncoder().encodeToString(encrypt(plaintext.getBytes("utf-8"), key.getBytes("utf-8"), iv.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密（Base64）
     *
     * @param ciphertext	密文（Base64）
     * @param key       	密钥
     * @param iv        	向量（16位）
     * @return 明文（UTF-8字符串）
     */
    public static String decryptBase64(String ciphertext, String key, String iv) {
        // 检查参数
        if (ciphertext == null || "".equals((ciphertext = ciphertext.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (key == null || "".equals((key = key.trim().replaceAll("\\s", "")))) {
            return null;
        }
        if (iv == null || "".equals((iv = iv.trim().replaceAll("\\s", ""))) || iv.length() != 16) {
            return null;
        }

        try {
            return new String(decrypt(Base64.getDecoder().decode(ciphertext), key.getBytes("utf-8"), iv.getBytes("utf-8")), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES加密
     *
     * @param plaintext 明文
     * @param key       密钥
     * @param iv        向量
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // 构造密钥生成器，指定为 AES 算法,不区分大小写
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        // 新增下面两行，处理 Linux 操作系统下随机数生成不一致的问题
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        random.setSeed(key);
        kgen.init(128, random);

        // 根据字节数组生成AES密钥
        SecretKey secretKey = new SecretKeySpec(kgen.generateKey().getEncoded(), "AES");

        // 根据指定算法AES自成密码器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化密码器（向量必须是16位）
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // 返回密文
        return cipher.doFinal(plaintext);
    }

    /**
     * AES解密
     *
     * @param ciphertext 密文
     * @param key        密钥
     * @param iv         向量
     * @return 明文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // 构造密钥生成器，指定为AES算法,不区分大小写
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        // 新增下面两行，处理 Linux 操作系统下随机数生成不一致的问题
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        random.setSeed(key);
        kgen.init(128, random);

        // 根据字节数组生成AES密钥
        SecretKey secretKey = new SecretKeySpec(kgen.generateKey().getEncoded(), "AES");

        // 根据指定算法AES自成密码器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化密码器（向量必须是16位）
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // 返回明文
        return cipher.doFinal(ciphertext);
    }

    /**
     * 字节 --> 十六进制字符串
     *
     * @param bytes 字节
     * @return 十六进制字符串
     */
    public static String bytes2HexStr(byte[] bytes) {
        // 检查字节是否为空
        if (bytes.length < 1)
            return null;

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);

            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * 十六进制字符串 --> 字节
     *
     * @param hexStr 十六进制字符串
     * @return 字节
     */
    public static byte[] hexStr2Bytes(String hexStr) {
        // 检查十六进制字符串是否为空
        if (hexStr == null || "".equals(hexStr.trim()))
            return null;

        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }

        return result;
    }
}
