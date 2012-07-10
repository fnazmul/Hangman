package hangman;

import hangman.AcceptHandler.TCPTextHanlder;
import reactor.Dispatcher;
import reactorapi.EventHandler;

public class HangmanServer {
	public static String wordToGuess = null;
	public static StringBuffer guessWord = null;
	public static String gameStatus;
	public static int life = 0;
	public static boolean gameOver = false;
	public static Dispatcher eventDispatcher = null;
	public static AcceptHandler acceptHandler = null;

	public static void main(String[] args) throws Exception {
		// this is the word to be played in hangman
		wordToGuess = args[0];

		// this is total life given to player
		life = Integer.parseInt(args[1]);

		// this is "-" kept in size of the word to be played which is replaced
		// by correct guess letter in game
		guessWord = new StringBuffer(wordToGuess);

		// putting "-" in guessWord
		for (int i = 0; i < wordToGuess.length(); i++) {
			guessWord.setCharAt(i, '-');
		}

		// creating dispatcher, accepthandler and calling addhandler and
		// handleevents
		eventDispatcher = new Dispatcher();
		acceptHandler = new AcceptHandler();
		eventDispatcher.addHandler(acceptHandler);
		eventDispatcher.handleEvents();
	}

	// this function handles the event received from accepthandler
	public static void handleTcpTextEvent(String gS,
			TCPTextHanlder tcpTextHanlder) {

		// if the player has logged in. For the first time this message will be
		// showed
		if (tcpTextHanlder.isFirstTime) {
			tcpTextHanlder.isFirstTime = false;
			tcpTextHanlder.playerName = gS;
			tcpTextHanlder.tcpTextHandle.write(new String(guessWord) + " "
					+ life);
		} else {

			int index = 0;
			char gC = gS.charAt(0);

			// with wrong guess life of player is decreased
			if (wordToGuess.indexOf(gC) == -1)
				life--;

			// correct guess replaces the correct guess letter in guessWord at
			// matching position
			while ((index = wordToGuess.indexOf(gC, index)) != -1) {
				guessWord.setCharAt(index, gC);
				index++;
			}

			// current status to be printed to user
			gameStatus = gC + " " + guessWord + " " + life + " "
					+ tcpTextHanlder.playerName;

			// game is Lost
			if (life < 1) {
				gameStatus = gameStatus
						+ "\n\n Your life is finished. Sorry, try again!";
				gameOver = true;
			}

			// game is won
			if (wordToGuess.equals(guessWord.toString())) {
				gameStatus = gameStatus + "\n\n Bravo! Your Guess is correct.";
				gameOver = true;
			}

			// this handles broadcasting message to the players console
			eventDispatcher.BroadCastMessage(gameStatus);

			// this handles the condition when game is over
			if (gameOver == true) {
				eventDispatcher.ClearHandlers();
			}
		}
	}

}
