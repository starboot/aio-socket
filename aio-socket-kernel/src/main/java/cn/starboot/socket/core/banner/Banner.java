package cn.starboot.socket.core.banner;

import java.io.PrintStream;

/**
 * 函数式编程
 *
 * @author MDong
 */
@FunctionalInterface
public interface Banner {

	/**
	 * 打印banner
	 * @param out 输出流
	 */
	void printBanner(PrintStream out);
}
