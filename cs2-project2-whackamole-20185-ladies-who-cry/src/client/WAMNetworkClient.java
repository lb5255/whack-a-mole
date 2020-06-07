package client;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static common.WAMProtocol.*;


/**
 * The client side network interface to WAM game server
 */
public class WAMNetworkClient {
    /**
     * Turn on if standard output debug messages are desired.
     */
    private static final boolean DEBUG = false;

    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint(Object logMsg) {
        if (WAMNetworkClient.DEBUG) {
            System.out.println(logMsg);
        }
    }

    /**
     * client socket to communicate with server
     */
    private Socket clientSocket;
    /**
     * used to read requests from the server
     */
    private Scanner networkIn;
    /**
     * Used to write responses to the server.
     */
    private PrintStream networkOut;
    /**
     * the model which keeps track of the game
     */
    private WAMBoard board;

    /** sentinel loop used to control the main loop */
    private boolean go;
    /** The number of plauer */
    private int playerNumber;
    /** The player score */
    private int playerScore;

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() { return this.go; }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() { this.go = false; }

    /**
     * Hook up with a game server already running and waiting for
     * players to connect.
     * @param host  the name of the host running the server program
     * @param port  the port of the server socket on which the server is listening
     * @throws WAMException If there is a problem opening the connection
     */
    public WAMNetworkClient(String host, int port) throws WAMException {
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            String[] welcome = networkIn.nextLine().split(" ");

            if(welcome[0].equals(WELCOME)) {
                this.board = new WAMBoard(Integer.parseInt(welcome[1]), Integer.parseInt(welcome[2]));
                this.board.setPlayerScores(Integer.parseInt(welcome[3]));
                this.playerNumber = Integer.parseInt(welcome[4]);

            } else {
                throw new WAMException("Expected CONNECT from server");
            }
            WAMNetworkClient.dPrint("Connected to server " + this.clientSocket);
        } catch (IOException e) {
            throw new WAMException(e);
        }
    }

    /** Get the board for Whack A Mole */
    public WAMBoard getBoard() {
        return this.board;
    }
    /** Get the number of player */
    public int getPlayerNumber() {
        return this.playerNumber;
    }

    /** This sends the message Whack to the server */
    public void sendWhack(int col, int row) {
        int mole_number;
        mole_number =(row * (this.board.getColumns())) + col;
        this.networkOut.println(WHACK + " " + mole_number + " " + this.playerNumber);
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
        try {
            this.clientSocket.close();

        } catch (IOException ioe) {
            // squash
        }
        this.board.close();
    }

    /**
     * Called from the GUI when it is ready to start receiving messages
     * from the server.
     */
    public void startListener() {
        new Thread(() -> this.run()).start();
    }

    /** Get the player score */
    public int getPlayerScore() {
        return this.playerScore;
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {
        boolean decision = true;
        while (decision) {
            try {
                String request = this.networkIn.next();
                String[] arguments = this.networkIn.nextLine().trim().split(" ");
                WAMNetworkClient.dPrint("Net message in = \"" + request + '"');
                System.out.println("Net message in = \"" + request + '"');

                switch (request) {
                    case WAMProtocol.MOLE_UP:
                        board.moleUp(Integer.parseInt(arguments[0]));
                        break;
                    case WAMProtocol.MOLE_DOWN:
                        board.moleDown(Integer.parseInt(arguments[0]));
                        break;
                    case WAMProtocol.GAME_WON:
                        this.board.gameWon();
                        this.stop();
                        break;
                    case WAMProtocol.GAME_LOST:
                        this.board.gameLost();
                        this.stop();
                        break;
                    case WAMProtocol.GAME_TIED:
                        this.board.gameTied();
                        this.stop();
                        break;
                    case WAMProtocol.ERROR:
                        break;
                    case WAMProtocol.SCORE:
                        this.playerScore = Integer.parseInt(arguments[playerNumber]);
                        break;
                    default:
                        System.err.println("Unrecognized request: " + request);
                        decision = false;
                        break;
                }
            } catch (NoSuchElementException nse) {
                // Looks like the connection shut down.
                System.out.println("Lost connection to server.");
                decision = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        this.close();

    }
}
