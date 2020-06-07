package client;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import common.WAMProtocol;

/**
 * This class is dedicated for the board of Whack A mole
 * The model for Whack A Mole
 */
public class WAMBoard {
    /** the number of rows */
    private int rows;
    /** the number of columns */
    private int columns;
    /** PlayerScore is kept within an int array*/
    private int[] playerScores;

    /** Possible statuses of game */
    public enum Status {
        NOT_OVER, I_WON, I_LOST, TIE, ERROR;

        private String message = null;

        public void setMessage( String msg ) {
            this.message = msg;
        }

        @Override
        public String toString() {
            return super.toString() +
                    this.message == null ? "" : ( '(' + this.message + ')' );
        }
    }
    /** This updates the status of the game */
    private Status status;

    /** Used to indicate a move that has been made on the board */
    public enum Move {
        MOLE_SYMBOL,
        NONE
    }

    /** the board */
    private Move[][] board;

    /** the observers of this model */
    private List<Observer<WAMBoard>> observers;

    /** This is the constructor and sets up everything */
    public WAMBoard(int rows, int columns) {
        this.status = Status.NOT_OVER;
        this.observers = new LinkedList<>();
        this.rows = rows;
        this.columns = columns;

        this.board = new Move[columns][rows];
        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {
                board[i][j] = Move.NONE;
            }
        }
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     * @param observer the observer
     */
    public void addObserver(Observer<WAMBoard> observer) {
        this.observers.add(observer);
    }

    /** when the model changes, the observers are notified via their update() method */
    private void alertObservers() {
        for (Observer<WAMBoard> obs : this.observers) {
            obs.update(this);
        }
    }

    /** Set the score for player */
    public void setPlayerScores(int players) {
        this.playerScores = new int[players];
    }

    /** Get the score for the player */
    public int[] getPlayerScores() {
        return this.playerScores;
    }


    /**
     * What is at this square?
     * @param row row number of square
     * @param col column number of square
     * @return the player at the given location
     */
    public Move getContents(int col, int row) {
        return this.board[col][row];
    }

    /**What will happen if the mole is up; the necessary steps for it */
    public void moleUp(int mole) {
        int col = mole % columns;
        int row = mole / columns;
        this.board[col][row] = Move.MOLE_SYMBOL;
        alertObservers();

    }
    /** What will happen if the mole is down; the necessary steps for it */
    public void moleDown(int mole) {
        int col = mole % columns;
        int row = mole / columns;
        this.board[col][row] = Move.NONE;
        alertObservers();
    }

    /** This gets the row */
    public int getRows() { return this.rows; }

    /** This gets the column */
    public int getColumns() { return this.columns; }

    /** This gets the observers */
    public List<Observer<WAMBoard>> getObservers() {
        return observers;
    }

    /** Get game status */
    public Status getStatus() {
        return this.status;
    }

    /** Called when the game has been won by this player */
    public void gameWon() {
        this.status = Status.I_WON;
        alertObservers();
    }

    /** Called when the game has been won by the other player */
    public void gameLost() {
        this.status = Status.I_LOST;
        alertObservers();
    }

    /** Called when the game has been tied */
    public void gameTied() {
        this.status = Status.TIE;
        alertObservers();
    }

    /** The user they may close at any time */
    public void close() {
        alertObservers();
    }

}
