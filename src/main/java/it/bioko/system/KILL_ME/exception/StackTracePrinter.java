package it.bioko.system.KILL_ME.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTracePrinter {

	public static String print(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		e.printStackTrace(writer);
		return e.getMessage() + " - " + e.getCause() + "\n" + stringWriter.getBuffer().toString();
	}
}