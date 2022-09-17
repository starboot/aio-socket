
package io.github.mxd888.socket.utils.lock;

/**
 * @author tanyaowu
 */
public interface ReadWriteLockHandler {

//	public static class ReadWriteRet {
//		public Object	readRet;
//		public Object	writeRet;
//		/**
//		 * 是不是运行了read方法
//		 */
//		public boolean	isReadRunned	= false;
//		/**
//		 * 是不是运行了write方法
//		 */
//		public boolean	isWriteRunned	= false;
//	}

//	/**
//	 * 
//	 * @return
//	 */
//	public void read() throws Exception;

	/**
	 * 
	 * @return
	 */
	public void write() throws Exception;
}
