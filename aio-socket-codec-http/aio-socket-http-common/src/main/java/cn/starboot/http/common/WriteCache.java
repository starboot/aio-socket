/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common;

import java.util.concurrent.Semaphore;

/**
 * Created by DELL(mxd) on 2022/12/30 13:21
 */
public class WriteCache {

	private final byte[] cacheData;
	private final Semaphore semaphore = new Semaphore(1);
	private long expireTime;


	public WriteCache(long cacheTime, byte[] data) {
		this.expireTime = cacheTime;
		this.cacheData = data;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public byte[] getCacheData() {
		return cacheData;
	}

}
