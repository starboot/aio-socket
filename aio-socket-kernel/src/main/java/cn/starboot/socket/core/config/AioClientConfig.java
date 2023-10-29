package cn.starboot.socket.core.config;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.exception.AioParameterException;

public class AioClientConfig extends AioConfig {

	/**
	 * 客户端线程数默认为2;就够用了
	 */
	private int kernelThreadNum = 1;

	@Override
	public String getName() {
		return "Client Configuration";
	}

	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public void setKernelThreadNumber(int threadNum) throws AioParameterException {
		if (threadNum < 1) {
			throw new AioParameterException("threadNum cannot be less than 1");
		}
		this.kernelThreadNum = threadNum;
	}

	@Override
	public int getKernelThreadNumber() {
		return this.kernelThreadNum;
	}

}
