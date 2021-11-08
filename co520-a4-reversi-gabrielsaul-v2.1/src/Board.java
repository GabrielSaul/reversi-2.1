import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class:       Board
 * Category:    Game Logic, Data
 * Implements:  Serializable
 * Summary:     This class represents the board state during a game of Reversi, using the player IDs to numerically
 *              represent the state of the board. The Board class also contains methods for efficiently calculating
 *              the legal moves for a given player ID, such that the move position & any flanked positions can be
 *              quickly accessed. The Board class is designed to be serialized, such that information about the 
 *              board's state can be stored when a session is saved.
 *              
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.28
 */

public class Board implements Serializable
{
    // The board's square matrix.
    private Square[][] squares; 

    // The size of the board.
    private int size;

    // The player IDs.
    private int[] pids;

    // The player scores.
    private int[] scores;

    // All empty squares adjacent to filled squares.
    private HashSet<Square> emptyAdjacents;

    // All available moves for each player, mapped to any flanked squares.
    private HashMap<Square, HashSet<Square>>[] moves;

    /**
     * (1) Constructor of Board objects
     * 
     * @param   size    The size of the board to be created.
     * @param   pids    The array of player IDs.
     * 
     * @throws          IllegalArgumentException
     */
    public Board(int size, int[] pids) throws IllegalArgumentException
    {
        // Validate arguments.
        if (pids == null) {
            throw new IllegalArgumentException("Null player IDs array passed to Board constructor");
        }
        else if ((size % pids.length) != 0) {
            throw new IllegalArgumentException("Invalid size passed to Board constructor");
        }

        // Assign the board size & player ID array.
        this.size = size;
        this.pids = pids;

        // Create the game data maps, lists & arrays.
        scores = new int[pids.length];
        emptyAdjacents = new HashSet<>();
        moves = new HashMap[pids.length];
        for (int i = 0; i < pids.length; i++) {
            moves[i] = new HashMap<>();
        }

        // Create the board in its initial state.
        create();
    }

    /**
     * Create the board in its initial setup for a standard 
     * Reversi game, with player pieces (represented as IDs) 
     * set in the center of the board. Leave blank cells as 0.
     */
    private void create()
    {
        // Create the board matrix.
        squares = new Square[size][size];

        // Create square matrix.
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                squares[y][x] = new Square(x, y);
            }
        }

        // Fill central squares.
        for (int y = (size / 2) - 1, i = 0, p = pids.length - 1; i < pids.length; y++, i++) {
            for (int x = (size / 2) - 1, j = 0; j < pids.length; x++, j++, p = (p + 1) % pids.length) {
                squares[y][x].setPID(pids[p]);

                // Set the initial scores.
                scores[pids[p] - 1] += 1;

                // Store the empty adjacent squares.
                setEmptyAdjacents(squares[y][x]);
            }
            p = (p - 1) % pids.length;
        }
    }

    /**
     * Return the square matrix.
     * 
     * @return      The square matrix.
     */
    public Square[][] getSquares()
    {
        return squares;
    }

    /**
     * Return the size of the board.
     * 
     * @return      The size of the board.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Return the score for a given player ID.
     * 
     * @param       pid     The given player ID.
     * @return              The score for a given ID, or -1
     *                      if the player ID doesn't exist.
     */
    public int getScore(int pid)
    {
        if (pid >= 1 && pid <= pids.length) {
            return scores[pid - 1];
        }

        return -1;
    }

    /**
     * Return the flanked squares from a given square
     * by a given player ID.
     * 
     * @param   square      The given square.
     * @param   pid         The given player ID.
     * @return              The flanked squares by the given player ID,
     *                      or null if it doesn't exist.
     */
    public HashSet<Square> getFlankedSquares(Square square, int pid)
    {
        // Return the move if it exists.
        if (pid >= 1 && pid <= pids.length && moves[pid - 1].containsKey(square)) {
            return moves[pid - 1].get(square);
        }

        return null;
    }

    /**
     * Return a set of all legal moves for the given player ID.
     * 
     * @param   pid     The player ID to query.
     * @return          A set of all legal moves for the given player ID, an empty
     *                  set if there are no legal moves for the given player ID, or
     *                  null if the player ID does not exist.
     */
    public HashSet<Square> getLegalMoves(int pid)
    {
        HashSet<Square> legalMoves = null;

        if (pid >= 1 && pid <= pids.length) {
            legalMoves = new HashSet<Square>(moves[pid - 1].keySet());
        }

        return legalMoves;
    }

    /**
     * Check if a move is legal at a given square by a
     * given player ID.
     * 
     * @param   square  The given square.
     * @param   pid     The given player ID.
     * @return          True if the move is legal, else
     *                  false.
     */
    public boolean isMoveLegal(Square square, int pid)
    {
        if (pid >= 1 && pid <= pids.length) {
            return moves[pid - 1].containsKey(square);
        }

        return false;
    }

    /**
     * Check if a given player currently has a legal move on the board.
     * 
     * @param       pid     The player ID to check.
     * @return              True if the given player has a legal move
     *                      on the board, or false if they have no moves
     *                      or the player ID doesn't exist.
     */
    public boolean hasLegalMove(int pid)
    {
        if (pid >= 1 && pid <= pids.length) {
            return !moves[pid - 1].isEmpty();
        }

        return false;
    }

    /**
     * Check if the board is full.
     * 
     * @return      True if the board is full,
     *              else false.
     */
    public boolean isFull()
    {
        return (emptyAdjacents.isEmpty());
    }

    /**
     * Make a move by placing a given player ID into a given
     * square. Throws an illegal move exception if the move
     * attempted is not legal.
     * 
     * @param   square      The square to be filled.
     * @param   pid         The player ID.
     * 
     * @throws              IllegalMoveException
     */
    public void makeMove(Square square, int pid) throws IllegalMoveException
    {
        // Throw an exception if the attempted move is not legal, or if the player ID/square does not exist.
        if (pid < 1 || pid > pids.length) {
            throw new IllegalMoveException("Invalid player ID passed to makeMove");
        }
        else if (!isMoveLegal(square, pid)) {
            throw new IllegalMoveException("Illegal move attempted in makeMove");
        }

        // Fill the square.
        square.setPID(pid);

        // Fill the flanked squares & set scores.
        scores[pid - 1] += 1;
        for (Square flankedSquare : getFlankedSquares(square, pid)) {            
            // Decrement flipped players' score(s).
            scores[flankedSquare.getPID() - 1]--;

            // Flip flanked square.
            flankedSquare.setPID(pid);

            // Increment active player's score.
            scores[pid - 1]++;
        }

        // Make the filled square an illegal move for any player.
        for (int i = 0; i < pids.length; i++) {
            moves[i].remove(square);
        }

        // Update the empty adjacents.
        setEmptyAdjacents(square);
    }

    /**
     * Set the empty adjacent squares from a given square, which
     * is asserted to be filled when this method is called, thus it
     * will be removed from the empty adjacents list.
     * 
     * @param   square    The given square.
     */
    private void setEmptyAdjacents(Square square)
    {
        // Remove the filled square.
        emptyAdjacents.remove(square);

        // Search for empty adjacent squares.
        for (int y = -1, adjY = square.y + y; y <= 1; y++, adjY = square.y + y) {
            for (int x = -1, adjX = square.x + x; adjY >= 0 && adjY < size && x <= 1; x++, adjX = square.x + x) {
                if ((x != 0 || y != 0) && adjX >= 0 && adjX < size && squares[adjY][adjX].getPID() == 0) {
                    emptyAdjacents.add(squares[adjY][adjX]);

                    // Set the filled adjacents for an empty adjacent square.
                    setFilledAdjacents(squares[adjY][adjX]);
                }
            }
        }
    }

    /**
     * Set the filled adjacent squares for a given square, which
     * is asserted to be empty when this method is called.
     * 
     * @param       square      The given square.
     */
    private void setFilledAdjacents(Square square)
    {
        // Search for filled adjacent squares.
        for (int y = -1, adjY = square.y + y; y <= 1; y++, adjY = square.y + y) {
            for (int x = -1, adjX = square.x + x; adjY >= 0 && adjY < size && x <= 1; x++, adjX = square.x + x) {
                if ((adjX != 0 || adjY != 0) && adjX >= 0 && adjX < size && squares[adjY][adjX].getPID() > 0) {
                    square.addFilledAdjacent(squares[adjY][adjX]);
                }
            }
        }
    }

    /**
     * Update the legal move mask, based on the empty squares
     * adjacent to filled squares, for a given player ID.
     * 
     * @param   pid     The player ID for which legal moves will
     *                  be updated.
     */
    public void updateLegalMoves(int pid)
    {
        // Check if the player ID exists.
        if (pid < 1 || pid > pids.length) {
            return;
        }

        // Set for storing flanked squares.
        HashSet<Square> flankedSquareSet = new HashSet<>();

        // Update legal moves from empty adjacent squares.
        moves[pid - 1].clear();
        for (Square empty : emptyAdjacents) {
            for (Square filled : empty.getFilledAdjacents()) {
                // Check if a flank is possible.
                if (filled.getPID() != pid) {
                    // Store the direction.
                    int xDir = filled.x - empty.x, yDir = filled.y - empty.y;

                    // Clear the list of flanked squares.
                    flankedSquareSet.clear();

                    // Store the first (possible) flanked square.
                    flankedSquareSet.add(squares[filled.y][filled.x]);

                    // Search in the given direction for a differing ID.
                    int nextX = filled.x + xDir, nextY = filled.y + yDir;
                    while (nextX >= 0 && nextX < size && nextY >= 0 && nextY < size) {
                        int nextPID = squares[nextY][nextX].getPID();

                        // Empty square found.
                        if (nextPID == 0) {
                            break;
                        }

                        // There is a flank. Move is legal.
                        if (nextPID == pid) {
                            // Add the flanked squares.
                            if (!moves[pid - 1].containsKey(squares[empty.y][empty.x])) {
                                moves[pid - 1].put(squares[empty.y][empty.x], new HashSet<>(flankedSquareSet));
                            }
                            else {
                                moves[pid - 1].get(squares[empty.y][empty.x]).addAll(flankedSquareSet);
                            }

                            break;
                        }

                        // Add a (possible) flanked square.
                        flankedSquareSet.add(squares[nextY][nextX]);

                        nextX += xDir;
                        nextY += yDir;
                    }
                }
            }
        }
    }
}