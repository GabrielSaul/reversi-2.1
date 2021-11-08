/**
 * Class:       CorruptedSessionException
 * Category:    Error Handling
 * Superclass:  Exception
 * Summary:     This exception is thrown if a corrupted session has been detected
 *              during the attempted loading of a session file.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.21
 */
public class CorruptedSessionException extends Exception
{
    /**
     * (1) Constructor for objects of class CorruptedSessionException
     */
    public CorruptedSessionException(String message)
    {
       super(message);
    }
}
