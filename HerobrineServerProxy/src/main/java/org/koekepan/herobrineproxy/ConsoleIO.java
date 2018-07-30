package org.koekepan.herobrineproxy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jline.console.ConsoleReader;

public class ConsoleIO {
	private static ConsoleReader reader = null;

	// setup ConsoleReader
	static {
		try { 
			reader = new ConsoleReader(System.in, System.out);
			reader.setExpandEvents(false);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	// print string to console
	public synchronized static void println(String output) {
		String timestamp = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + " INFO]: ";

		String message = timestamp + output + "\n";
		
		try {
			reader.print(ConsoleReader.RESET_LINE + "");
			reader.flush();
			System.out.write(message.getBytes());
			System.out.flush();

			try {
				reader.drawLine();
			} catch (Throwable e) {
				reader.getCursorBuffer().clear();
			}

			reader.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	// read string from console
	public static String readLine() throws IOException {
		reader.print(ConsoleReader.RESET_LINE + "");
		reader.flush();
		return reader.readLine(">", null);
	}

}
