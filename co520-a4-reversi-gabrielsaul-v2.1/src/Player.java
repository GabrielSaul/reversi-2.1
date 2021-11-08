import java.io.Serializable;
import java.awt.Color;

/**
 * Class:       Player
 * Category:    Game Logic, Data
 * Implements:  Comparable, Serializable
 * Summary:     This class represents a player in the game of Reversi. A Player has a name (inputted in a
 *              player panel) and a disk color. The Player class also contains information about a player's
 *              total points in an active game and their total number of wins in the active session. Player
 *              objects are sorted alphabetically, based on their disk color name. This ensures a set order
 *              of play when a game begins.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.19
 */
public class Player implements Comparable, Serializable
{
    // The player's name.
    private String name;
       
    // The player's ID.
    private int pid;
    
    // The player's displayed disk color name.
    private String diskColorName;
    
    // The player's actual disk color.
    private Color diskColor;
    
    // The player's total in the current game.
    private int currentGameTotal;
    
    // The player's win count in the session.
    private int winCount;
    
    // Whether or not this player's turn is currently active.
    private boolean active;
    
    /**
     * (1) Constructor for Player objects
     * 
     * @param   name            The player's name upon creation.
     * @param   diskColorName   The player's displayed disk color name upon creation.
     * @param   diskColor       The player's actual disk color upon creation.
     */
    public Player(String name, String diskColorName, Color diskColor)
    {
        this.name = name;
        this.diskColorName = diskColorName;
        this.diskColor = diskColor;
        
        // Current game total will be updated during a game.
        currentGameTotal = 0;
        
        // Win count will be updated during a session.
        winCount = 0;
    }
    
    /**
     * Return the player's name.
     * 
     * @return      The player's name.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Return the player's numerical ID.
     * 
     * @return      The player's ID.
     */
    public int getID()
    {
        return pid;
    }
    
    /**
     * Return the displayed disk color name.
     * 
     * @return      The player's displayed disk color name.
     */
    public String getDiskColorName()
    {
        return diskColorName;
    }
    
    /**
     * Return the player's actual disk color.
     * 
     * @return      The player's actual disk color.
     */
    public Color getDiskColor()
    {
        return diskColor;
    }
    
    /**
     * Return the player's current game total.
     * 
     * @return      The player's current game total.
     */
    public int getCurrentGameTotal()
    {
        return currentGameTotal;
    }
    
    /**
     * Return the player's win count in the session.
     * 
     * @return      The player's win count in the session.
     */
    public int getWinCount()
    {
        return winCount;
    }
    
    /**
     * Check if it is this player's turn in the
     * current game.
     * 
     * @return      True if it is this player's turn, else
     *              false.
     */
    public boolean isActive()
    {
        return active;
    }
    
    /**
     * Set the player's current game total.
     * 
     * @param   newTotal    The player's new total.
     */
    public void setCurrentGameTotal(int newTotal)
    {
        currentGameTotal = newTotal;
    }
    
    /**
     * Increment the player's win count.
     */
    public void incrementWinCount()
    {
        winCount += 1;
    }
    
    /**
     * Set the player's numerical ID. The ID signifies the
     * player's place in the turn ordering, and is used for
     * the numerical representation of the board state during
     * a game.
     * 
     * @param   pid     The player's new numerical ID.
     */
    public void setID(int pid)
    {
        this.pid = pid;
    }
    
    /**
     * Set the player to active.
     * 
     * @param   active      True if the player is now active, else
     *                      false.
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    /**
     * Compare players by their disk color name, such that they
     * can be sorted alphabetically.
     * 
     * @param       obj      The other object in the comparison.
     */
    @Override
    public int compareTo(Object obj) throws NullPointerException, ClassCastException
    {
        // Ensure that the comparison is with a non-null object that is another Player.
        if (obj == null) {
            throw new NullPointerException();
        }
        else if (!(obj instanceof Player)) {
            throw new ClassCastException();
        }
        
        // Otherwise, return the difference.
        return diskColorName.compareTo(((Player) obj).getDiskColorName());
    }
    
    /**
     * Return a string representation of the player.
     * 
     * @return      A string representation of the player.
     */
    @Override
    public String toString()
    {
        return name + " (" + diskColorName + ")";
    }
}
