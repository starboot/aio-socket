/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Mimetypes {
    /* The default MIME type */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static final String MIME_TYPES_FILE_NAME = "mime.types";

    private static Mimetypes mimetypes = null;

    private final HashMap<String, String> extensionToMimetypeMap = new HashMap<String, String>();

    private Mimetypes() {
    }

    public synchronized static Mimetypes getInstance() {
        if (mimetypes != null)
            return mimetypes;

        mimetypes = new Mimetypes();
        try (InputStream is = Mimetypes.class.getClassLoader().getResourceAsStream(MIME_TYPES_FILE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] tokens = StringUtils.splitPreserveAllTokens(line, " \t\n\r\f");
                String mediaType = tokens[0];
                for (int i = 1; i < tokens.length; i++) {
                    String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                    mimetypes.extensionToMimetypeMap.put(fileExtension, mediaType);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load '" + MIME_TYPES_FILE_NAME + "'", ex);
        }
        return mimetypes;
    }

    public String getMimetype(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            String ext = fileName.substring(lastPeriodIndex + 1).toLowerCase();
            if (extensionToMimetypeMap.containsKey(ext)) {
                return extensionToMimetypeMap.get(ext);
            }
        }
        return DEFAULT_MIMETYPE;
    }

    public String getMimetype(File file) {
        return getMimetype(file.getName());
    }

}
