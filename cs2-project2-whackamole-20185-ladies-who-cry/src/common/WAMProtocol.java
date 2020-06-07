package common;

/**
 * The interface provides constants for all of the
 * messages that are communicated between the server and the
 * clients.<br>
 *
 * All messages are strings. The string begins with the command name.
 * Any arguments follow. The arguments are separated from the command
 * and from each other with blanks.
 *
 * It is likely that messages are sent using a
 * {@link java.io.PrintWriter#print(String)} method, which means that there
 * would be new line characters involved. It is safest to assume white
 * space of any kind at the start and end of the messages.
 *
 * Note that dimensions of the board are given as number of rows and number
 * of columns. However, mole positions are numbered serially, in row-major
 * order, starting with zero. This has been done to allow for more irregularly
 * placed mole-holes in future versions of the game.
 *
 * @author RIT CS
 */
public interface WAMProtocol {
    /**
     * From server: sent to the client after the client
     * initially opens a {@link java.net.Socket} connection to the server.<br>
     * The dimensions of the board are sent in the request, #rows first.<br>
     * The third argument is the number of players.<br>
     * A final, fourth argument is the player number. Players are numbered
     * upward consecutively starting with 0 based on the time of connection.
     *
     *  For example if there are 6 rows and 7 columns and this
     *  is the second player of 3: "WELCOME 6 7 3 1"
     */
    public static final String WELCOME = "WELCOME";

    /**
     * From server: inform client that a mole has popped up.<br>
     *     One argument: the mole number
     */
    public static final String MOLE_UP = "MOLE_UP";

    /**
     * From server: inform client that a mole has dropped down.
     * This message is sent even if it was whacked or if the game server
     * has pulled it down.<br>
     *     One argument: the mole number
     */
    public static final String MOLE_DOWN = "MOLE_DOWN";

    /**
     * From client: inform server that it has whacked a mole.
     * (Game server must then decide if a mole was really up at that
     * location. Only then would points be awarded. If it is a miss,
     * points may be subtracted.<br>
     *     Arguments: the mole number, the player number
     */
    public static final String WHACK = "WHACK";

    /**
     * From server: client is informed of every player's score.<br>
     *     As many integer arguments as there are players.
     *     Numbers are in order of the players' numbers.
     *     These are sent to all clients as often as any player's
     *     score changes.
     */
    public static final String SCORE = "SCORE";

    /**
     * Message sent from the server to the client when the client has won the
     * game.
     */
    public static final String GAME_WON = "GAME_WON";

    /**
     * Message sent from the server to the client when the client has lost the
     * game.
     */
    public static final String GAME_LOST = "GAME_LOST";

    /**
     * Message sent from the server to the client when the client has tied with
     * others in the game to win.
     */
    public static final String GAME_TIED = "GAME_TIED";

    /**
     * Request sent from the server to the client when any kind of error has
     * resulted from a bad client response. No response is expected from the
     * client and the connection is terminated (as is the game).<br>
     * The entire argument "list" should be interpreted as a string to be
     * displayed.
     */
    public static final String ERROR = "ERROR";

}
