/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common;

/**
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HeaderValue {
    /**
     * name
     */
    private String name;
    /**
     * Value 值
     */
    private String value;
    /**
     * 同名Value
     */
    private HeaderValue nextValue;

    public HeaderValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HeaderValue getNextValue() {
        return nextValue;
    }

    public void setNextValue(HeaderValue nextValue) {
        this.nextValue = nextValue;
    }
}
