package hangman;

import reactorapi.*;
import java.net.*;
import java.io.*;

/**
 * A {@link Handle} that reads and writes a line of text at a time from a TCP
 * {@link Socket}.
 */
public class TCPTextHandle implements Handle<String> {
	Socket socket;
	BufferedReader in;
	PrintStream out;

	/**
	 * Create a handle that reads a socket.
	 * 
	 * @param s
	 *            the socket to read from
	 */
	public TCPTextHandle(Socket s) {
		socket = s;
		try {
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (Exception e) {
			throw new RuntimeException("Internal socket error");
		}
	}

	/**
	 * Write a line of text to the socket (adding a newline).
	 * 
	 * @param s
	 *            the text to write
	 */
	public void write(String s) {
		out.println(s);
		out.flush();
	}

	/**
	 * Get a line of text from the socket.
	 * 
	 * @returns the line of text (without the newline), or <code>null</code> on
	 *          error or socket closing.
	 */
	public String read() {
		try {
			return in.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Close the socket, interrupting any pending {@link read()}.
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			/* Whatever happened, there's nothing to do about it. */
		}
	}
}
