package com.snowcat.fajnyobed.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by AntikAdmin on 25. 9. 2014.
 */
public class MD5 {

    public static String getHashString(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(source.getBytes());
            return HexHelper.toString(hash).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
