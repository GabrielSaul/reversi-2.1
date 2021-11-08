import java.io.Serializable;
import java.util.HashSet;

/**
 * Class:       Square
 * Category:    Game Logic, Data
 * Implements:  Serializable
 * Summary:     This class represents a square on the board: A 2-tuple, containing 
 *              an x-coordinate and a y-coordinate. It also contains numerical data 
 *              about which player currently occupies the position in the form of
 *              a stored player ID. Additionally, it contains a set of filled
 *              adjacent squares.
 *              
 *              NOTE: Squares are initialized to store 0 as the default pid. This will be
 *                    used to represent an "empty" square. Any other positive value is
 *                    assumed to be a player ID when a Square is processed by the Board.
 *
 * @author  Gabriel Doyle-Finch 
 * @version 2021.04.19 
 */
public class Square implements Serializable
{   
    // The x-coordinate: Publicly accessible.
    public final int x;
    
    // The y-coordinate: Publicly accessible.
    public final int y;
    
    // The player ID held at this position.
    private int pid;
    
    // All filled adjacent squares.
    HashSet<Square> filledAdjacents;

    /**
     * (1) Constructor of Square objects
     * 
     * @param   x   The assigned x-coordinate.
     * @param   y   The assigned y-coordinate.
     */
    public Square(int x, int y)
    {
        this.x = x;
        this.y = y;
        
        // Initially empty.
        pid = 0;
        
        filledAdjacents = new HashSet<>();
    }
    
    /**
     * Return the player ID held at this square.
     * 
     * @return      The player ID held at this position.
     */
    public int getPID()
    {
        return pid;
    }
    
    /**
     * Return the filled adjacent squares from this square.
     * 
     * @return      The filled adjacent squares.
     */
    public HashSet<Square> getFilledAdjacents()
    {
        return filledAdjacents;
    }
    
    /**
     * Set the player ID held at this position.
     * 
     * @param       pid     The new player ID held at this position.
     */
    public void setPID(int pid)
    {
        this.pid = pid;
    }
    
    /**
     * Add a filled adjacent square, or do nothing if the given
     * square is null.
     * 
     * @param   square      The filled adjacent square to add.
     */
    public void addFilledAdjacent(Square square)
    {
        if (square != null) {
            filledAdjacents.add(square);
        }
    }
}
