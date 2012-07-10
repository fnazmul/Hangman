package reactor;

import hangman.AcceptHandle;
import hangman.TCPTextHandle;
import java.util.ArrayList;
import reactorapi.*;

public class Dispatcher {
	// these variables keeps the handlers and events generated
	public ArrayList eventHandlers = new ArrayList();
	public ArrayList events = new ArrayList();

	public Dispatcher() {

	}

	public void handleEvents() throws InterruptedException {
		for (;;) {
			// if there is no event then it breaks else the first event in queue
			// is selected and processed
			if (eventHandlers.size() < 1) {
				break;
			} else {
				Object[] temp = select();
				((EventHandler) temp[0]).handleEvent(temp[1]);
			}
		}
	}

	// this function is to add handler
	public synchronized void addHandler(EventHandler<?> h) {
		eventHandlers.add(h);

		// this is an anonymous thread that will perform the read operation on
		// handle
		// to use the handler "h", we have reassigned "h" into final object "hF"
		// so that it can be used inside the anonymous thread.

		final EventHandler hF = h;
		new Thread(new Runnable() {
			public EventHandler hTHandler = hF;

			public void run() {
				for (;;) {
					// reading new incoming events in handlers
					Object event = hTHandler.getHandle().read();
					// if there is new event then it is queued to events list of
					// corresponding handler
					insert(hTHandler, event);

					if (event == null) {
						break;
					}
				}
			}
		}).start();

		notifyAll();
	}

	public synchronized void removeHandler(EventHandler<?> h) {
		eventHandlers.remove(h);
		h = null;
		notifyAll();
	}

	// if no events then it waits until there is any event. When an event is
	// received then first event is read and then removed
	public synchronized Object[] select() {
		while (events.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {

			}
		}
		Object[] temp = (Object[]) events.get(0);

		events.remove(0);
		notifyAll();

		return temp;
	}

	// new incoming events are added to handlers
	public synchronized void insert(EventHandler h, Object o) {
		if (events.size() < 150) {
			events.add(new Object[] { h, o });
			notifyAll();
		}
	}

	// this function receives messages from hangmanserver and prints them to
	// player consoles
	public void BroadCastMessage(String message) {
		for (Object eventHandler : eventHandlers) {
			try {
				((TCPTextHandle) ((EventHandler) eventHandler).getHandle())
						.write(message);
			} catch (Exception ex) {
			}
		}
	}

	// this function is initiated when the game is over. It closes all the
	// handles of tcptexthandle and accepthandle and thus removes all the event
	// handlers
	public void ClearHandlers() {
		for (Object eventHandler : eventHandlers) {
			try {
				((TCPTextHandle) ((EventHandler) eventHandler).getHandle())
						.close();
			} catch (Exception ex) {
				((AcceptHandle) ((EventHandler) eventHandler).getHandle())
						.close();
			}
		}
		eventHandlers.clear();
	}

}