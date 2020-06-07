package server;


import common.WAMException;
import static common.WAMProtocol.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class that manages the requests and responses to a single client.
 */
public class WAMPlayer extends Thread {
    private int playerNumber;

    private WAMGame game;

    private Socket sock;

    private Scanner scanner;

    private PrintStream printer;

    /**
     * Creates a new WAMPlayer
     * @param sock the socket
     * @param playerNumber the player number
     * @throws WAMException exception protocol for WAM
     */
    public WAMPlayer(Socket sock, int playerNumber) throws WAMException {
        this.playerNumber = playerNumber;
        this.sock = sock;
        try {
            scanner = new Scanner(sock.getInputStream());
            printer = new PrintStream(sock.getOutputStream());
        }
        catch (IOException e) {
            throw new WAMException(e);
        }
    }

    /**
     * Sends a mole down
     * @param moleNumber the mole that comes down
     */
    public void MoleDown(int moleNumber) {
        printer.println( MOLE_DOWN + " " + moleNumber);
    }

    /**
     * Sends a mole up
     * @param moleNumber
     */
    public void MoleUp(int moleNumber) {
        printer.println( MOLE_UP + " " + moleNumber);
    }

    /**
     * Called if the player wins
     */
    public void gameWon() { printer.println(GAME_WON); }

    /**
     * Called if the player loses/
     */
    public void gameLost() {
        printer.println(GAME_LOST);
    }

    /**
     * Called if the player ties.
     */
    public void gameTied() {
        printer.println(GAME_TIED);
    }

    /**
     * Sets the WAMgame in the client
     * @param game the WAMGame the attribute for WAMGame is set to
     */
    public void setGame(WAMGame game) {
        this.game = game;
    }

    /**
     * Gets the scores of all the players
     * @param scores the scores of all the players
     */
    public void getScore(int[] scores) {
        String s = "SCORE";
        for(int i = 0; i < scores.length; i++) {
            s += " " + scores[i];
        }
        printer.println(s);
    }

    /**
     * Sends the connect message to the GUI
     * @param rows the number of rows
     * @param columns the number of columns
     * @param players the number of players
     * @param playerNumber the player number
     */
    public void connect(int rows, int columns, int players, int playerNumber) {
        printer.println(WELCOME + " " + rows + " " + columns + " " + players + " " + playerNumber);
    }

    /**
     * Reads the nextline for the scanner
     * @return The scanner's next line.
     */
    public String read() {
        return scanner.nextLine();
    }

    /**
     * Runs the thread
     */
    @Override
    public void run() {
        while(scanner.hasNextLine()) {
            String[] s = this.read().split(" ");
            game.whack(Integer.parseInt(s[1]), this.playerNumber);

        }
    }

    /**
     * Closes the socket.
     */
    public void close() {
        this.printer.close();
        this.scanner.close();
        try {
            sock.close();

        }
        catch(IOException ioe) {
            // squash
        }
    }

}