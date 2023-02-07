package com.wellsun.bjst_zj_new.kx;

import android.os.Build;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;

public class RSAUtils {
    public static final String KEY_ALGORITHM = "RSA";
    public static final int RSA_MODULUS_LEN = 128;

    public static byte[] publicKeyDecrypt(String str, String str2, String str3, RSAPublicKey rSAPublicKey, byte[] bArr) throws Exception {
        if (str == null || str.equals("")) {
            str = KEY_ALGORITHM;
        }
        if (str2 == null || str2.equals("")) {
            str2 = "ECB";
        }
        if (str3 == null || str3.equals("")) {
            str3 = "PKCS1Padding";
        }
        Cipher instance = Cipher.getInstance(str + "/" + str2 + "/" + str3);
        instance.init(2, rSAPublicKey);
        return instance.doFinal(bArr);
    }

    public static byte[] privateKeyEncrypt(String str, String str2, String str3, RSAPrivateKey rSAPrivateKey, byte[] bArr) throws Exception {
        if (str == null || str.equals("")) {
            str = KEY_ALGORITHM;
        }
        if (str2 == null || str2.equals("")) {
            str2 = "ECB";
        }
        if (str3 == null || str3.equals("")) {
            str3 = "PKCS1Padding";
        }
        Cipher instance = Cipher.getInstance(str + "/" + str2 + "/" + str3);
        instance.init(1, rSAPrivateKey);
        return instance.doFinal(bArr);
    }

    public static RSAPublicKey loadPublicKey(String str, String str2, int i) throws Exception {
        return (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new RSAPublicKeySpec(new BigInteger(str, i), new BigInteger(str2, i)));
    }

    public static RSAPrivateKey loadPrivateKey(String str, String str2, int i) throws Exception {
        return (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new RSAPrivateKeySpec(new BigInteger(str, i), new BigInteger(str2, i)));
    }

    public static byte[] publicKeyEncrypt(String str, String str2, String str3, RSAPublicKey rSAPublicKey, byte[] bArr) throws Exception {
        if (str == null || str.equals("")) {
            str = KEY_ALGORITHM;
        }
        if (str2 == null || str2.equals("")) {
            str2 = "ECB";
        }
        if (str3 == null || str3.equals("")) {
            str3 = "PKCS1Padding";
        }
        Cipher instance = Cipher.getInstance(str + "/" + str2 + "/" + str3);
        instance.init(1, rSAPublicKey);
        return instance.doFinal(bArr);
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String convertStringToHex(String str) {
        char[] charArray = str.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (char c : charArray) {
            stringBuffer.append(Integer.toHexString(c));
        }
        return stringBuffer.toString();
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static String dataSign(String str, String str2) {
        try {
            PrivateKey privateKey = getPrivateKey(str2);
            Signature instance = Signature.getInstance("SHA1withRSA");
            instance.initSign(privateKey);
            instance.update(str.getBytes());
            byte[] sign = instance.sign();
            if (Build.VERSION.SDK_INT >= 26) {
                return Base64.getEncoder().encodeToString(sign);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean verifySign(byte[] bArr, String str, String str2) {
        try {
            PublicKey pubKey = getPubKey(str2);
            Signature instance = Signature.getInstance("SHA1withRSA");
            instance.initVerify(pubKey);
            instance.update(str.getBytes());
            return instance.verify(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static PublicKey getPubKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(new BASE64Decoder().decodeBuffer(str)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(str)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        } catch (InvalidKeySpecException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static Map generateKeyPair() throws Exception {
        HashMap hashMap = new HashMap();
        KeyPairGenerator instance = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        instance.initialize(1024);
        RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey) instance.generateKeyPair().getPrivate();
        hashMap.put("N", rSAPrivateCrtKey.getModulus().toString(16));
        hashMap.put("D", rSAPrivateCrtKey.getPrivateExponent().toString(16));
        return hashMap;
    }
}
