package server;
import java.util.LinkedList;
import java.util.Random;
/**
 * Whack-A-Mole Game that serves as an extension to the server.
 *
 */
public class WAMGame implements Runnable {

    private WAMPlayer[] players;
    private int columns;
    private int rows;
    private LinkedList<moleThread> moleList;
    private int[] playerScore;
    private WAMServer server;
    private int gameTime;

    /**
     * Creates a new WAMGame
     * @param players the number of players
     * @param columns the number of columns
     * @param rows the number of rows
     * @param server the server the game's running on
     * @param gameTime the duration of the game
     */
    public WAMGame(WAMPlayer[] players, int columns, int rows, WAMServer server, int gameTime) {
        this.players = players;
        this.columns = columns;
        this.rows = rows;
        this.moleList = new LinkedList<>();
        this.playerScore = new int[players.length];
        this.server = server;
        this.gameTime = gameTime * 1000;

    }


    /**
     * Sends a whack to the server
     * @param mole the mole whacked
     * @param playerNum the player that whacked the mole
     */
    public synchronized void whack(int mole, int playerNum) {
        if(moleList.get(mole).getStatus()) {
            playerScore[playerNum] += 2;
            for (WAMPlayer player : players) {
                player.MoleDown(mole);
                for(WAMPlayer p: players)  {
                    p.getScore(playerScore);
                }

            }
        } else {
            playerScore[playerNum] -= 1;
            for(WAMPlayer p: players)  {
                p.getScore(playerScore);
            }
        }
    }

    /**
     * Runs the thread
     */
    @Override
    public void run() {
        int total = columns * rows;
        moleThread[] m = new moleThread[total];
        for(int i = 0; i < total; i++) {
            moleThread t = new moleThread(i);
            m[i] = t;
            moleList.add(t);

        }
        for(moleThread t: m) {
            t.start();
        }

        for(WAMPlayer player: players) {
            player.start();
        }
        try {
            Thread.sleep(gameTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(moleThread t: m) {
            t.stop();
        }

        displayResult();
        this.server.shutDown();
    }

    /**
     * Displays the result after the time has passed
     */
    public void displayResult() {
        int max = getMax();
        if(this.hasWonGame()) {
            for(int i = 0; i < playerScore.length; i++) {
                if(playerScore[i] == max) {
                    players[i].gameWon();
                } else {
                    players[i].gameLost();
                }
            }
        } else {
            for(int i = 0; i < playerScore.length; i++) {
                if (playerScore[i] == max) {
                    players[i].gameTied();
                } else {
                    players[i].gameLost();
                }
            }
        }
    }

    /**
     * Tests if there is a winner in the game.
     * @return Is there a game winner?
     */
    public boolean hasWonGame() {
        int max = getMax();
        int count = 0;
        for(int i = 0; i < playerScore.length; i++) {
            if(playerScore[i] == max) {
                count += 1;
            }
        }
        if(count == 1) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return the max score
     */
    public int getMax() {
        int max = -100000;
        for(int i: playerScore) {
            if(i > max) {
                max = i;
            }
        }
        return max;
    }

    /**
     * Creates a mole thread that moves up and down.
     */
    public class moleThread extends Thread {
        private int moleNumber;
        private boolean up;
        Random random = new Random();


        /**
         * Constructor for the mole thread
         * @param moleNumber the mole number
         */
        public moleThread(int moleNumber) {
            this.moleNumber = moleNumber;
            this.up = false;
        }

        /**
         * Gets the status of the mole
         * @return is the mole up?
         */
        public boolean getStatus() {
            return this.up;
        }

        /**
         * Runs the mole thread to move it up and down
         */
        public void run() {

        while(true) {
            int upper = 10000;
            int lower = 2000;
            int sleeptime = random.nextInt(upper - lower) + lower;
            int up = 5000;
            int low = 3000;
            int awaketime = random.nextInt(up - low) + low;

                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (WAMPlayer player : players) {
                    player.MoleUp(this.moleNumber);
                    this.up = true;
                }
                try {
                    Thread.sleep(awaketime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (WAMPlayer player : players) {
                    player.MoleDown(moleNumber);
                    this.up = false;
                }
            }
        }
    }
}
