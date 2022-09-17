
package io.github.mxd888.socket.utils.lock;

/**
 * @author tanyaowu
 *
 */
public interface ReadLockHandler<T> {

	/**
	 * 
	 * @param t
	 */
	public void handler(T t);

}
