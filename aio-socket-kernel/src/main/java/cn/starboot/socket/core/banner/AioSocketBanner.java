package cn.starboot.socket.core.banner;

import cn.starboot.socket.core.AioConfig;

import java.io.PrintStream;

public class AioSocketBanner implements Banner {

	private static final String BANNER =
					"  _______       _____  ____    ____  \n" +
					" |__   __|     |_   _||_   \\  /   _| \n" +
					"    | |  ______  | |    |   \\/   |   \n" +
					"    | | |______| | |    | |\\  /| |   \n" +
					"    | |         _| |_  _| |_\\/_| |_  \n" +
					"    |_|        |_____||_____||_____| \n" +
					" ";

	@Override
	public void printBanner(PrintStream printStream) {
		printStream.println(BANNER);
		printStream.println(getTimVersion());
	}

	private String getTimVersion() {
		return "\033[036m :: "+ AioConfig.PROJECT_NAME +" :: \033[0m" + " Kernel version: " + AioConfig.VERSION;
	}
}
