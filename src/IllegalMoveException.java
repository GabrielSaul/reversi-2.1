/**
 * Class:       IllegalMoveException
 * Category:    Error Handling
 * Superclass:  Exception
 * Summary:     This exception is thrown if an illegal move is attempted on the board.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.19
 */
public class IllegalMoveException extends Exception
{
    /**
     * (1) Constructor for objects of class IllegalMoveException
     */
    public IllegalMoveException(String message)
    {
       super(message);
    }
}
