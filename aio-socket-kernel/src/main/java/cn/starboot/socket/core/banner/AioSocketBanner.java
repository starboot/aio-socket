package cn.starboot.socket.core.banner;

import cn.starboot.socket.core.AioConfig;

import java.io.PrintStream;

public class AioSocketBanner implements Banner {

	private static final String BANNER =
					"   _     ___    ___          ___    ___     ___   _  __  ___   _____ \n" +
					"  /_\\   |_ _|  / _ \\   ___  / __|  / _ \\   / __| | |/ / | __| |_   _|\n" +
					" / _ \\   | |  | (_) | |___| \\__ \\ | (_) | | (__  | ' <  | _|    | |  \n" +
					"/_/ \\_\\ |___|  \\___/        |___/  \\___/   \\___| |_|\\_\\ |___|   |_|  \n";

	@Override
	public void printBanner(PrintStream printStream) {
		printStream.println(BANNER);
		printStream.println(getTimVersion());
	}

	private String getTimVersion() {
		return "\033[036m :: " + AioConfig.PROJECT_NAME + " :: \033[0m" + " Kernel version: " + AioConfig.VERSION;
	}
}
