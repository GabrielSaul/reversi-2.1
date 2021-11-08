import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.HashSet;

/**
 * Class:       BoardPanel
 * Category:    GUI
 * Superclass:  JPanel
 * Summary:     This class represents the board panel component, which displays & manages the
 *              graphical representation of the game board for a Reversi game.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.28
 */
public class BoardPanel extends JPanel
{
    /* * * * * * * * * * * * Class Variables * * * * * * * * * * * */

    // Padding sizes.
    private static final int INNER_PADDING_SIZE = 2;

    // Board colors.
    private static final Color SQUARE_PANEL_BACKGROUND_COLOR = new Color(0x03, 0x96, 0x3C);
    private static final Color SQUARE_PANEL_PREVIEW_COLOR = SQUARE_PANEL_BACKGROUND_COLOR.brighter();
    private static final Color SQUARE_PANEL_BORDER_COLOR = SQUARE_PANEL_BACKGROUND_COLOR.darker();

    // Preview alpha values.
    private static final int SQUARE_PANEL_HOVER_OPACITY = 75;
    private static final int SQUARE_PANEL_PREVIEW_OPACITY = 50;

    /* * * * * * * * * * * * Instance Variables * * * * * * * * * * * */

    // The maximum size of the board panel
    private int maxSize;

    // The displayed size of the board panel.
    private int displaySize;

    // The display bounds.
    private int lowerBound;
    private int upperBound;

    // The board's square panel matrix.
    private SquarePanel[][] squarePanels;

    // The board's square matrix.
    private Square[][] squares;

    // Flanked squares for move previews.
    private HashSet<SquarePanel> flankedSquarePanels;

    // Legal moves for move previews.
    private HashSet<Square> currentLegalMoves;

    // Square where the next move will occur.
    private SquarePanel activeSquarePanel;

    // The associated board object.
    private Board board;

    // The current game.
    private Game currentGame;

    // The current player's ID.
    private int currentPlayerID;

    // Whether or not the board panel is currently interactive.
    private boolean active;

    // Whether or not the last attempted move was legal.
    private boolean moveLegal;

    // Whether or not there is currently a preview visible.
    private boolean isPreview;

    // Whether or not the legal moves preview is visible.
    private boolean showLegalMoves;

    /**
     * (1) Constructor of BoardPanel objects
     * 
     * @param   maxSize         The maximum size of the board panel
     * @param   displaySize     The initial displayed size of the board panel.
     * @param   parentListener  The parent listener for external updates.
     * 
     * @throws                  IllegalArgumentException
     */
    public BoardPanel(int maxSize, int displaySize, MouseListener parentListener) throws IllegalArgumentException
    {
        // Validate size arguments.
        if (maxSize <= 0 || ((maxSize % 2) != 0)) {
            throw new IllegalArgumentException("Invalid maximum size passed to BoardPanel constructor");
        }
        if (displaySize > maxSize || ((displaySize % 2) != 0)) {
            throw new IllegalArgumentException("Invalid display size passed to BoardPanel constructor");
        }

        // Set the maximum size of this board panel.
        this.maxSize = maxSize;

        // Create & size the board panel.
        create(parentListener);
        resize(displaySize);

        // Initialize any preview data.
        flankedSquarePanels = new HashSet<>();

        // Set the board to be inactive initially.
        setActive(false);
    }

    /**
     * Return the board panel's current display size.
     * 
     * @return      The board panel's current display size.
     */
    public int getDisplaySize()
    {
        return displaySize;
    }

    /**
     * Create the game board for the GUI:
     * 
     * The board is created with a maximum amount of squares.
     * 
     * @param   parentListener      The parent listener for external events.
     */
    private void create(MouseListener parentListener)
    {
        // Create the square panel matrix.
        squarePanels = new SquarePanel[maxSize][maxSize];

        // Create & format the layout for the board panel.
        GridLayout boardLayout = new GridLayout(maxSize, maxSize);
        boardLayout.setVgap(INNER_PADDING_SIZE);
        boardLayout.setHgap(INNER_PADDING_SIZE);

        // Set the layout for the board panel.
        setLayout(boardLayout);

        for (int i = 0; i < maxSize; ++i) {
            for (int j = 0; j < maxSize; ++j) {
                // Create a new square panel.
                SquarePanel squarePanel = new SquarePanel(j, i);
                squarePanel.setBorder(new LineBorder(SQUARE_PANEL_BORDER_COLOR));
                squarePanel.setBackground(SQUARE_PANEL_BACKGROUND_COLOR);

                // Add the mouse listener to the square panel.
                squarePanel.addMouseListener(new MouseAdapter()
                    {
                        /**
                         * Update the board panel when a square panel is clicked.
                         * Check for legality & adjust board panel as needed.
                         * 
                         * @param   e       The mouse event.
                         */
                        @Override
                        public void mouseClicked(MouseEvent e)
                        {
                            // Do nothing if the board panel is currently inactive.
                            if (!active || board == null) {
                                return;
                            }

                            // Store the square.
                            Square curr = squares[squarePanel.getYPosition() - lowerBound][squarePanel.getXPosition() - lowerBound];

                            // Do nothing if the move isn't legal.
                            if (!board.isMoveLegal(curr, currentPlayerID)) {
                                moveLegal = false;
                                return;
                            }

                            // Remove the previous legal move preview, if it is active.
                            if (showLegalMoves) {
                                hideLegalMoves();
                            }

                            // Place the disk.
                            placeDisk(squarePanel, curr);
                        }

                        /**
                         * Update the board panel when a square panel is hovered over.
                         * Create a preview of the possible move, if a legal move exists
                         * for the square.
                         * 
                         * @param   e       The mouse event.
                         */
                        @Override
                        public void mouseEntered(MouseEvent e)
                        {
                            // Do nothing if the board panel is currently inactive.
                            if (!active || board == null) {
                                return;
                            }

                            // Store the square.
                            Square curr = squares[squarePanel.getYPosition() - lowerBound][squarePanel.getXPosition() - lowerBound];

                            // Do nothing if the move isn't legal.
                            if (!board.isMoveLegal(curr, currentPlayerID)) {
                                return;
                            }

                            // Show the preview.
                            showPreview(squarePanel, board.getFlankedSquares(curr, currentPlayerID));
                        }

                        /**
                         * Update the board panel when the mouse hovers out of a square
                         * panel. Remove any active previews.
                         * 
                         * @param   e       The mouse event.
                         */
                        @Override
                        public void mouseExited(MouseEvent e)
                        {
                            // Do nothing if the board is currently inactive.
                            if (!active || board == null) {
                                return;
                            }

                            // Remove the preview.
                            removePreview(false);
                        }
                    }
                );

                // Add the parent listener to the square panel.
                squarePanel.addMouseListener(parentListener);

                // Add the square panel to the panel matrix & board panel.
                squarePanels[i][j] = squarePanel;
                add(squarePanels[i][j]);
            }
        }
    }

    /**
     * Resize the board panel:
     * 
     * Set a given dimension of the board to visible,
     * setting all other squares to be invisible.
     * 
     * @param   size        The new displayed size of the board.
     * 
     * @throws              IllegalArgumentException
     */
    public void resize(int size) throws IllegalArgumentException
    {
        // Validate size argument.
        if (!isSizeValid(size)) {
            throw new IllegalArgumentException("Invalid display size passed to resizeBoard");
        }

        // Set the new display size.
        displaySize = size;

        // Set the resizing bounds.
        lowerBound = (int) ((maxSize - displaySize) / 2);
        upperBound = maxSize - lowerBound;

        // Resize the board.
        for (int i = 0; i < maxSize; ++i) {
            for (int j = 0; j < maxSize; ++j) {
                if (i >= lowerBound && i < upperBound && j >= lowerBound && j < upperBound) {
                    squarePanels[i][j].setVisible(true);
                }
                else {
                    squarePanels[i][j].setVisible(false);
                }
            }
        }
    }

    /**
     * Check if a given size is valid for the board panel.
     * 
     * @param   size        The size to check.
     * @return              True if the size is valid,
     *                      else false.
     */
    public boolean isSizeValid(int size)
    {
        if (size <= 0 || size > maxSize || ((size % 2) != 0)) {
            return false;
        }

        return true;
    }

    /**
     * Setup the board panel for a game:
     * 
     * Set the associated game & board object for the board panel,
     * drawing the initial configuration.
     * 
     * @param   game        The current game.
     */
    public void setup(Game game) throws IllegalArgumentException
    {        
        // Set the associated game.
        currentGame = game;

        // Set the current player's ID.
        currentPlayerID = currentGame.getCurrentPlayer().getID();

        // Set the associated board object.
        board = game.getBoard();

        // Draw the board.
        drawBoard(board.getSize());

        // Set the legal move preview, if it is active.
        if (showLegalMoves) {
            showLegalMoves();
        }
    }

    /**
     * Draw the board in its current state.
     * 
     * @param   size    The size of the board to draw.
     * 
     * @throws          IllegalArgumentException
     */
    private void drawBoard(int size) throws IllegalArgumentException
    {
        // Resize the board panel if needed.
        if (size != displaySize) {
            resize(size);
        }

        // Store the board's matrix.
        squares = board.getSquares();

        // Iterate over the matrix, drawing any non-blank squares as discs on the panel matrix.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (squares[i][j].getPID() > 0) {
                    squarePanels[i + lowerBound][j + lowerBound].setDiskPanel(currentGame.getPlayers()[squares[i][j].getPID() - 1].getDiskColor());
                }
                else {
                    squarePanels[i + lowerBound][j + lowerBound].setDiskPanel(null);
                }
            }
        }
    }

    /**
     * Reset the board panel to its pre-session state.
     */
    public void reset()
    {
        // Make every square panel blank.
        for (SquarePanel[] row : squarePanels) {
            for (SquarePanel squarePanel : row) {
                squarePanel.setDiskPanel(null);
            }
        }
    }

    /**
     * Place a disk at a given square panel, checking for
     * legality & updating the board.
     * 
     * @param   squarePanel     The square panel to place the disk (graphically).
     * @param   square          The square to place to disk (logically).
     */
    private void placeDisk(SquarePanel squarePanel, Square square)
    {
        // Do nothing if a preview isn't active.
        if (!isPreview || activeSquarePanel == null) {
            return;
        }

        // Place the player's disk (logically). Check for legality.
        try {
            board.makeMove(square, currentPlayerID);
        }
        catch (IllegalMoveException ex) {
            moveLegal = false;
            return;
        }

        // Move must be legal.
        moveLegal = true;

        // Place the player's disk (graphically).
        Color color = currentGame.getPlayers()[currentPlayerID - 1].getDiskColor();
        squarePanel.setDiskPanel(color);

        // Flip the flanked squares (graphically).
        for (SquarePanel flankedSquarePanel : flankedSquarePanels) {
            flankedSquarePanel.setDiskPanel(color);
        }

        // Remove the preview.
        removePreview(true);

        // Update the player ID.
        nextTurn();
    }

    /**
     * Show a preview of the current move.
     * 
     * @param   squarePanel     The square panel being highlighted.
     * @param   flankedSquares  The flanked squares.
     */
    private void showPreview(SquarePanel squarePanel, HashSet<Square> flankedSquares)
    {
        // Signify that a preview is active.
        isPreview = true;

        // Store the next move panel.
        activeSquarePanel = squarePanel;

        // Highlight the square.
        Color color = currentGame.getPlayers()[currentPlayerID - 1].getDiskColor();
        squarePanel.setDiskPanel(new Color(color.getRed(), color.getGreen(), color.getBlue(), SQUARE_PANEL_HOVER_OPACITY));

        // Highlight any flanked squares.
        for (Square flankedSquare : flankedSquares) {
            squarePanels[flankedSquare.y + lowerBound][flankedSquare.x + lowerBound].setBackground(SQUARE_PANEL_PREVIEW_COLOR);
            flankedSquarePanels.add(squarePanels[flankedSquare.y + lowerBound][flankedSquare.x + lowerBound]);
        }
    }

    /**
     * Remove the active preview, if it exists.
     * 
     * @param   moveTaken       True if a move has just been taken,
     *                          else false.
     */
    private void removePreview(boolean moveTaken)
    {
        // Do nothing if no preview is active.
        if (!isPreview || activeSquarePanel == null) {
            return;
        }

        // Un-highlight the next move square, if no move was taken & the legal moves preview isn't active.
        if (!moveTaken && showLegalMoves) {
            Color color = currentGame.getPlayers()[currentPlayerID - 1].getDiskColor();
            activeSquarePanel.setDiskPanel(new Color(color.getRed(), color.getGreen(), color.getBlue(), SQUARE_PANEL_PREVIEW_OPACITY));
        }
        else if (!moveTaken && !showLegalMoves) {
            activeSquarePanel.setDiskPanel(null);
        }

        // Un-highlight flanked square panels upon exit.
        for (SquarePanel flankedSquarePanel : flankedSquarePanels) {
            flankedSquarePanel.setBackground(SQUARE_PANEL_BACKGROUND_COLOR);
        }

        // Clear preview data.
        flankedSquarePanels.clear();

        // No preview active.
        isPreview = false;
    }

    /**
     * Display a preview of all of the legal moves that are currently on the board.
     */
    private void showLegalMoves()
    {
        // Receive all legal moves for the current player ID.
        currentLegalMoves = board.getLegalMoves(currentPlayerID);

        // Store the preview disk color.
        Color diskColor = currentGame.getPlayers()[currentPlayerID - 1].getDiskColor();
        Color previewColor = new Color(diskColor.getRed(), diskColor.getGreen(), diskColor.getBlue(), SQUARE_PANEL_PREVIEW_OPACITY);

        // Set a preview for each legal move.
        for (Square square : currentLegalMoves) {
            squarePanels[square.y + lowerBound][square.x + lowerBound].setDiskPanel(previewColor);
        }
    }

    /**
     * Hide the prevew of all legal moves that are currently on the board.
     */
    private void hideLegalMoves()
    {
        // Do nothing if no legal moves are stored.
        if (currentLegalMoves == null || currentLegalMoves.isEmpty()) {
            return;
        }

        // Remove the preview for each legal move.
        for (Square square : currentLegalMoves) {
            if (square.getPID() == 0) {
                squarePanels[square.y + lowerBound][square.x + lowerBound].setDiskPanel(null);
            }
        }
    }

    /**
     * Set the legal moves preview for the current player to be visible or invisible.
     * 
     * @param       visible     True if the the legal moves are to be previewed, else
     *                          false if they are to be invisible.
     */
    public void setLegalMovesVisible(boolean visible)
    {
        if (visible) {
            showLegalMoves = true;
            showLegalMoves();
        }
        else {
            showLegalMoves = false;
            hideLegalMoves();
        }
    }

    /**
     * Move to the next turn in the game, updating the
     * current player ID.
     */
    public void nextTurn()
    {
        if (currentGame != null) {
            currentGame.nextTurn();
            currentPlayerID = currentGame.getCurrentPlayer().getID();
        }

        // Set the legal move preview, if it is active.
        if (showLegalMoves) {
            showLegalMoves();
        }
    }

    /**
     * Set the board panel's interactive state.
     * 
     * @param   state   True if the board will become interactive,
     *                  else false.
     */
    public void setActive(boolean state)
    {
        active = state;
    }

    /**
     * Check if the board panel is currently interactive.
     * 
     * @return      True if the board is currently interactive,
     *              else false.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Check if the last move attempted was legal.
     */
    public boolean isMoveLegal()
    {
        return moveLegal;
    }
}