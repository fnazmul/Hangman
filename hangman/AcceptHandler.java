package hangman;

import java.io.IOException;
import java.net.Socket;
import reactorapi.EventHandler;
import reactorapi.Handle;

public class AcceptHandler implements EventHandler<Socket> {
	// creates an instance of accepthandle
	private AcceptHandle acceptHandle = null;

	public AcceptHandler() throws IOException {
		acceptHandle = new AcceptHandle();
	}

	public Handle<Socket> getHandle() {
		return acceptHandle;
	}

	// function to handle the events
	public void handleEvent(Socket newSocket) {
		if (newSocket == null) {
			HangmanServer.eventDispatcher.removeHandler(this);
		} else {
			try {
				HangmanServer.eventDispatcher.addHandler(new TCPTextHanlder(
						newSocket));
			} catch (IOException e) {
			}
		}
	}

	public class TCPTextHanlder implements EventHandler<String> {
		// an instance of TCPTextHandle
		public TCPTextHandle tcpTextHandle = null;

		// if the first login then use this variable to print a login message
		public boolean isFirstTime = true;

		// player name to be stored
		public String playerName;

		public TCPTextHanlder(Socket newSocket) throws IOException {
			tcpTextHandle = new TCPTextHandle(newSocket);
		}

		public Handle<String> getHandle() {
			return tcpTextHandle;
		}

		public void handleEvent(String playerInput) {
			// if no input then handler is removed else events are processed
			// function in HangmanServer

			if (playerInput == null) {
				HangmanServer.eventDispatcher.removeHandler(this);
			} else {
				HangmanServer.handleTcpTextEvent(playerInput, this);
			}
		}
	}

}
