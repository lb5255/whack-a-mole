package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server for the Whack-A-Mole game. It sends game-related messages to clients connected to this server in order
 * to progress the Whack-A-Mole game.
 */
public class WAMServer implements WAMProtocol, Runnable{

    /** The server socket which waits for a client connect*/
    private ServerSocket server;

    /** the port number */
    private static int port;

    public WAMServer(int port) throws WAMException {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new WAMException(e);
        }
    }

    /** The number of rows*/
    private static int ROW;

    /** The number of columns */
    private static int COL;

    /** The number of players */
    private static int num_player;

    /** The game duration amount*/
    private static int game_duration_second;

    /**The list of players*/
    private WAMPlayer[] playerList;

    /**
     * Closes the server.
     */
    public void shutDown() {
        this.exit_moles();
        for(WAMPlayer player:playerList) {
            player.close();
        }
    }

    /**
     * Creates a server and calls the run method
     * @throws WAMException
     */
    public static void main(String[] args) throws WAMException {

        if (args.length != 5) {
            System.out.println("Usage: java ConnectFourServer <port>");
            System.exit(1);
        }

        port = Integer.parseInt(args[0]);
        ROW = Integer.parseInt(args[1]);
        COL = Integer.parseInt(args[2]);
        num_player = Integer.parseInt(args[3]);
        game_duration_second = Integer.parseInt(args[4]);
        WAMServer server = new WAMServer(port);
        new Thread(server).start();
    }

    /**
     * Waits for clients to connect
     */
    @Override
    public void run() {
        try {
            WAMPlayer[] players = new WAMPlayer[num_player];
            Socket[] sockets = new Socket[num_player];
            for(int i = 0; i < num_player; i++){
                System.out.println("Waiting for player " + (i+1) + "...");
                sockets[i] = server.accept();
                players[i] = new WAMPlayer(sockets[i], i);
                players[i].connect(ROW, COL, num_player, i);
                System.out.println("Player " + (i+1) +" connected!");
            }


            this.playerList = players;
            System.out.println(WAMProtocol.WELCOME);

            System.out.println("\n");
            System.out.println("Port number: " + port);
            System.out.println("Number of row: " + ROW);
            System.out.println("Number of columns: " + COL);
            System.out.println("Number of players: " + num_player);
            System.out.println("Game duration in seconds: " + game_duration_second);

            System.out.println("Starting game!");

            WAMGame game = new WAMGame(players, COL, ROW, this, game_duration_second);
            for(WAMPlayer player: players) {
                player.setGame(game);
            }

            new Thread(game).start();

        } catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
        } catch (WAMException e) {
            System.err.println("Failed to create players!");
            e.printStackTrace();
        }
    }

    /**
     * Removes the moles from the board once the game is finished.
     */
    public void exit_moles() {
        int mole_num = 0;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                for (WAMPlayer player : playerList) {
                    player.MoleDown(mole_num);
                }
                mole_num++;
            }
        }
    }
}