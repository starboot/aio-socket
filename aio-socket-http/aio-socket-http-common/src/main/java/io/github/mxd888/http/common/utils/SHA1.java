
package io.github.mxd888.http.common.utils;

import java.security.MessageDigest;


public class SHA1 {

    public static byte[] encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            return messageDigest.digest(str.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}