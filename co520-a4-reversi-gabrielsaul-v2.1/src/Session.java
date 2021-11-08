import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class:       Session
 * Category:    Data
 * Implements:  Serializable
 * Summary:     This class represents a Reversi session, which contains information about the players and
 *              the current game. The Session class is designed to be serialized, such that a session can
 *              be saved & loaded.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.21
 */

public class Session implements Serializable
{   
    // The players in the session.
    private Player[] players;

    // The current game in the session.
    private Game currentGame;
    
    // Game history data.
    private ArrayList<String> gameHistory;

    // The filename of this session.
    private String filename;

    // Whether or not the session has been saved since the last change.
    private boolean saved;

    /**
     * (1) Constructor of Session objects
     * 
     * @param   players     The players for this session.
     */
    public Session(Player[] players)
    {
        this.players = players;
        currentGame = null;
        gameHistory = new ArrayList<>();
        filename = null;
    }

    /**
     * Return the players for this session.
     * 
     * @return      The players for this session.
     */
    public Player[] getPlayers()
    {
        return players;
    }

    /**
     * Return the currently active game.
     * 
     * @return      The current game.
     */
    public Game getCurrentGame()
    {
        return currentGame;
    }

    /**
     * Return the filename of this session.
     * 
     * @return      The filename of this session.
     */
    public String getFilename()
    {
        return filename;
    }
    
    /**
     * Return the game history list.
     * 
     * @return      The game history list.
     */
    public ArrayList<String> getGameHistory()
    {
        return gameHistory;
    }

    /**
     * Assign a filename to this session.
     * 
     * @param   filename        The assigned filename.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Set the saved status of the session, signifying
     * whether or not it has been saved since the last
     * change.
     * 
     * @param   saved       True if the session has been
     *                      saved since the last change,
     *                      else false.
     */
    public void setSaved(boolean saved)
    {
        this.saved = saved;
    }

    /**
     * Create a new game of a given board size.
     * 
     * @param       boardSize       The given board size for the game to create.
     */
    public void createGame(int boardSize)
    {
        // Save the recent game's data, if it exists.
        if (currentGame != null && currentGame.isFinished()) {
            gameHistory.add(currentGame.toString());
        }
        
        // Create the new game.
        currentGame = new Game(players, boardSize);
    }

    /**
     * Check if a game is currently active.
     * 
     * @return      True if a game is currently active, else false.
     */
    public boolean isGameActive()
    {
        return (currentGame != null) && !currentGame.isFinished();
    }
    
    /**
     * Check if a game has started, i.e. at least one
     * move has been played.
     * 
     * @return      True if the game has started, else
     *              false.
     */
    public boolean hasGameStarted()
    {
        return (currentGame.getMoveCount() >= 1);
    }

    /**
     * Check if the current game has any legal moves on the
     * current turn.
     */
    public boolean hasLegalMove()
    {
        return currentGame.hasLegalMove();
    }

    /**
     * Check if the session has an assigned filename.
     * 
     * @return      True if the session has a filename.
     *              else false.
     */
    public boolean hasFilename()
    {
        return (filename != null);
    }

    /**
     * Check if the session has been saved since the last
     * change.
     * 
     * @return      True if the session has been saved
     *              since the last change, else false.
     */
    public boolean isSaved()
    {  
        return saved;
    }
}