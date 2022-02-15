package com.liyuliang.mytime.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5计算器
 * Created at 2019-05-10 19:19
 *
 * @author LiYuliang
 * @version 1.0
 */

public class Md5Tool {

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String hashKey(String key) {
        String hashKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            hashKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            hashKey = String.valueOf(key.hashCode());
        }
        return hashKey;
    }

}
