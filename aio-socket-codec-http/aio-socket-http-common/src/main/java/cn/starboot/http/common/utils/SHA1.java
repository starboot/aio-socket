/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.utils;

import java.security.MessageDigest;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
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
