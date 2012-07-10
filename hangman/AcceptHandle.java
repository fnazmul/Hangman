package hangman;

import java.net.*;
import java.io.*;
import reactorapi.*;

/**
 * A {@link Handle} that accepts TCP connections and returns {@link Socket}s for
 * them.
 */
public class AcceptHandle implements Handle<Socket> {
	ServerSocket socket;

	/**
	 * Create a new {@link AcceptHandle} on any spare port.
	 * 
	 * @throws IOException
	 *             if no free ports exist or another rare IO error occurs.
	 */
	public AcceptHandle() throws IOException {
		socket = new ServerSocket(0);
		System.out.println("" + socket.getLocalPort());
		System.out.flush();
	}

	/**
	 * Wait for a new connection and return a {@link Socket} for it.
	 * 
	 * @returns the new connection or <code>null</code> on error or if the
	 *          handle is closed.
	 */
	public Socket read() {
		try {
			Socket s = socket.accept();
			return s;
		} catch (IOException ie) {
			return null;
		}
	}

	/**
	 * Close the server socket, interrupting {@link #read()} operation.
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			/* This should not happen and even if it did we don't care. */
		}
	}
}
