package gui;


import client.Observer;
import client.WAMBoard;
import client.WAMNetworkClient;
import common.WAMException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.util.List;


/**
 * A JavaFX GUI for the networked Connect Four game.
 *
 * @author Lea Boyadjian
 * @author Gavin Burris
 * @author Tony Jiang
 */

public class WAMGUI extends Application implements Observer<WAMBoard> {

    /** The model*/
    private WAMBoard board;

    /**This label is dedicated for score*/
    private Label score;

    /** connection to network interface to server*/
    private WAMNetworkClient serverConn;

    /**This label is dedicated for the end result of the game*/
    private Label outcome;

    /** This label is dedicated for the mole image */
    private Image mole = new Image(getClass().getResourceAsStream("mort2.png"));

    /** This label is dedicated for the hole image */
    private Image empty = new Image(getClass().getResourceAsStream("holey.png"));

    /** This is a global gridpane*/
    private GridPane game;

    /** A list of buttons */
    private Button[][] buttonList;

    /** Player Scores is stored in an array of int */
    private int[] playerScores;
    /** the player number */
    private int playerNumber;

    /**
     * This sets everything up and ensures everything is ready to go
     */
    @Override
    public void init() throws WAMException, InterruptedException {
        // get the command line args
        List<String> args = getParameters().getRaw();
        // get host info and port from command line
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        // Start the network client listener thread
        this.serverConn = new WAMNetworkClient(host, port);
        this.board = serverConn.getBoard();
        this.buttonList = new Button[board.getColumns()][board.getRows()];

        this.board.addObserver(this);
        this.playerScores = board.getPlayerScores();
        this.playerNumber = serverConn.getPlayerNumber();
    }


    /**
     * Construct the layout for the game.
     *
     * @param stage container (window) in which to render the GUI
     * @throws Exception if there is a problem
     */
    @Override
    public void start(Stage stage) {
        this.score = new Label("Score:");
        this.outcome = new Label("");
        this.game = new GridPane();
        BorderPane borderpane = new BorderPane();

        for (int col = 0; col < board.getColumns(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Button button = new Button("", new ImageView(empty));
                int y = col;
                int x = row;
                button.setOnAction((event) -> {
                    serverConn.sendWhack(y, x);

                });
                game.add(button, col, row);
                buttonList[col][row] = button;

            }
        }

        HBox hBox = new HBox();
        hBox.getChildren().addAll(score,outcome);
        hBox.setSpacing(160);

        borderpane.setCenter(game);
        borderpane.setBottom(hBox);

        Scene scene = new Scene(borderpane);
        stage.setTitle("Whack A Mole!");
        stage.setScene(scene);
        stage.show();
        this.serverConn.startListener();

    }


    /**
     * GUI updates
     */
    private void refresh() {
        this.score.setText("Score: " + this.serverConn.getPlayerScore());

        for (int i = 0; i < this.board.getColumns(); i++) {
            for (int j = 0; j < this.board.getRows(); j++) {
                if (this.board.getContents(i, j).equals(WAMBoard.Move.MOLE_SYMBOL)) {
                    this.buttonList[i][j].setGraphic(new ImageView(mole));
                } else {
                    this.buttonList[i][j].setGraphic(new ImageView(empty));

                }
            }
        }

        WAMBoard.Status status = board.getStatus();
        switch (status) {
            case ERROR:
                break;
            case I_WON:
                outcome.setText("You won. Yay! :D");
                break;
            case I_LOST:
                outcome.setText("You lost. Boo! :(");
                break;
            case TIE:
                outcome.setText("Tie game. Meh");
                break;
        }
    }
    /**
     * GUI is closing, so close the network connection. Server will get the message.
     */
    @Override
    public void stop() {
        //this.serverConn.close();
    }

    /**
     * Called by the model, client.ConnectFourBoard, whenever there is a state change
     * that needs to be updated by the GUI.
     *
     */
    public void update(WAMBoard wamboard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    /**
     * The main method expects the host and port.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ConnectFourGUI host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}