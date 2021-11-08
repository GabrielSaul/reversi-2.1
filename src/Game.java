import java.io.Serializable;
import java.lang.StringBuilder;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Class:       Game
 * Category:    Game Logic, Data
 * Implements:  Serializable
 * Summary:     This class represents a game of Reversi, which contains information about the players' standing in
 *              the game and the state of the board (in numerical format). The Game class is designed to be serialized, 
 *              such that information about an active game can be stored when a session is saved.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.19
 */

public class Game implements Serializable
{
    // The players in the game.
    private Player[] players;

    // The currently winning player: Null if there is no outright winner.
    private Player winningPlayer;

    // The game board.
    private Board board;

    // The size of the game board.
    private int boardSize;

    // The current turn.
    private int turnCount;

    // The number of subsequent passes.
    private int passCount;

    // The number of moves played.
    private int moveCount;

    // Whether or not the game is finished.
    private boolean finished;

    // The start date of this game.
    private LocalDateTime startDate;

    /**
     * (1) Constructors of Game objects
     * 
     * @param       players     The players in the game.
     * @param       boardSize   The size of the game board.
     */
    public Game(Player[] players, int boardSize)
    {
        // Set the players.
        this.players = players;
        for (Player player : players) {
            player.setActive(false);
        }

        // Set the start date of this game.
        startDate = LocalDateTime.now();

        // Create a new board, passing in the players' IDs.
        this.boardSize = boardSize;
        this.board = new Board(boardSize, Arrays.stream(players).mapToInt(Player::getID).toArray());

        // Initalize turn, pass & move counters.
        turnCount = passCount = moveCount = 0;

        // Set initial legal move mask.
        board.updateLegalMoves(getCurrentPlayer().getID());

        // Activate the player & set scores.
        getCurrentPlayer().setActive(true);
        setPlayerScores();
    }

    /**
     * Return the players in the game.
     * 
     * @return      The players array.
     */
    public Player[] getPlayers()
    {
        return players;
    }

    /**
     * Return the game board.
     * 
     * @return      The game board.
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Return the size of the game board.
     * 
     * @return      The size of the game board.   
     */
    public int getBoardSize()
    {
        return boardSize;
    }

    /**
     * Return the currently active player.
     * 
     * @return      The currently active player.
     */
    public Player getCurrentPlayer()
    {
        return players[turnCount];
    }

    /**
     * Return the winning player, or null if there
     * is no winning player.
     * 
     * @return      The winning player if it exists,
     *              else null.
     */
    public Player getWinningPlayer()
    {
        return winningPlayer;
    }

    /**
     * Return the current number of moves played.
     * 
     * @return      The current number of moves played.
     */
    public int getMoveCount()
    {
        return moveCount;
    }

    /**
     * Check if the current player has a legal move.
     * 
     * @return      True if the current player has a legal move,
     *              else false.
     */
    public boolean hasLegalMove()
    {
        return board.hasLegalMove(getCurrentPlayer().getID());
    }

    /**
     * Check if the game is finished.
     * 
     * @return      True if the game is over, else false.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Check if the game has a winning player.
     * 
     * @return      True if the game has a winning player,
     *              else false.
     */
    public boolean hasWinningPlayer()
    {
        return (winningPlayer != null);
    }

    /**
     * Move to the next turn, incrementing the
     * turn counter & updating the players' scores.
     */
    public void nextTurn()
    {         
        // Deactivate current player.
        getCurrentPlayer().setActive(false);
        
        // Move to next turn & update legal moves & scores.
        turnCount = (turnCount + 1) % players.length;
        board.updateLegalMoves(getCurrentPlayer().getID());
        setPlayerScores();

        // Check for win.
        if (passCount >= players.length || board.isFull()) {
            finished = true;

            // Increment win count if there is a winning player.
            if (hasWinningPlayer()) {
                winningPlayer.incrementWinCount();
            }

            return;
        }

        // Game continues.
        finished = false;

        // Activate current player.
        getCurrentPlayer().setActive(true);

        // Increment pass counter.
        if (!hasLegalMove()) {
            passCount++;
        }
        else {
            passCount = 0;
        }

        // Increment move counter.
        moveCount++;
    }

    /**
     * Set the player scores & store a winning player, if it exists.
     */
    private void setPlayerScores()
    {
        // Temporary storage for checking winning player.
        Player currentWinningPlayer = null;

        // Assume draw at first.
        winningPlayer = null;

        // Set scores & store winning player, if it exists.
        int i = 0;
        for (Player player : players) {
            player.setCurrentGameTotal(board.getScore(player.getID()));

            // Store first player for comparison & confirm a winner when there's a maximum.
            if (i == 0) {
                currentWinningPlayer = player;
            }
            else if (player.getCurrentGameTotal() > currentWinningPlayer.getCurrentGameTotal()) {
                winningPlayer = currentWinningPlayer = player;
            }
            else if (player.getCurrentGameTotal() < currentWinningPlayer.getCurrentGameTotal()) {
                winningPlayer = currentWinningPlayer;
            }

            i++;
        }
    }

    /**
     * Return a string representation of the game's current state.
     * 
     * @return      A string representation of the game.
     */
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder("");

        // Append the start date & current date.
        str.append("[ Start: " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(startDate));
        str.append(" | End: " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now()) + " ] ");

        // Append the scores.
        int i = 0;
        for (Player player : players) {
            str.append(player + ": " + player.getCurrentGameTotal());
            if (i < players.length - 1) {
                str.append(", ");
            }

            i++;
        }

        // Append the board size.
        str.append(String.format(" [ Board size: %d × %d ]", boardSize, boardSize)); 

        return str.toString();
    }
}