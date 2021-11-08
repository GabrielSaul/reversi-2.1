import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Class:       Reversi
 * Category:    GUI, Data
 * Summary:     The main class for the Reversi game. This class contains the
 *              main GUI components and manages their usage. The main class
 *              also manages the saving & loading of sessions.
 * Version:     2.1
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.30
 */
public class Reversi
{
    /* * * * * * * * * * * * Class Variables * * * * * * * * * * * */

    // Padding size constants.
    private static final int OUTER_PADDING_SIZE = 10;
    private static final int INNER_PADDING_SIZE = 5;

    // Component dimension constants.
    private static final int BOARD_PANEL_PREF_WIDTH           = 515;
    private static final int BOARD_PANEL_PREF_HEIGHT          = 515;
    private static final int STATUS_PANEL_PREF_WIDTH          = 900;
    private static final int STATUS_PANEL_PREF_HEIGHT         = 35;
    private static final int WARNING_PANEL_PREF_HEIGHT        = 35;
    private static final int WARNING_PANEL_PREF_WIDTH         = 900;
    private static final int PLAYER_DISPLAY_PANEL_PREF_WIDTH  = 300;
    private static final int PLAYER_DISPLAY_PANEL_PREF_HEIGHT = 460;
    private static final int BUTTON_PANEL_PREF_WIDTH          = 300;
    private static final int BUTTON_PANEL_PREF_HEIGHT         = 75;

    // Component background color constants.
    private static final Color MAIN_PANEL_BACKGROUND_COLOR  = new Color(0xC0, 0x28, 0x00);

    // Board size constants.
    private static final int LARGE_BOARD_SIZE    = 10;
    private static final int STANDARD_BOARD_SIZE = 8;
    private static final int SMALL_BOARD_SIZE    = 6;

    // Status constants.
    private static final String DEFAULT_WELCOME_STATUS = "Welcome to Reversi. " +
                                                         "Enter your player names into the right panel and click Play to begin a session.";

    // Saved data constants.
    private static final String[] SAVED_DATA_FILENAME_EXTENSIONS = { ".rvsi", ".serialized", ".ser" };

    // Version info constants.
    private static final String REVERSI_VERSION_INFO = "Reversi v2.1 (2021)";

    /* * * * * * * * * * * * Instance Variables * * * * * * * * * * * */

    // The Reversi JFrame component.
    private JFrame frame;

    // The board panel.
    private BoardPanel boardPanel;

    // The status label.
    private JLabel statusLabel;

    // The warning status label.
    private JLabel warningLabel;

    // The "Play" button.
    private JButton playButton;

    // Disk colors.
    private LinkedHashMap<String, Color> diskColors;

    // The array of players.
    private Player[] players;

    // The array of player panels.
    private PlayerPanel[] playerPanels;

    // The number of players.
    private int playerCount;

    // The active session.
    private Session session;

    // The file chooser.
    private JFileChooser fileChooser;

    // The "File" menu items.
    private JMenuItem newSessionItem;
    private JMenuItem saveSessionItem;
    private JMenuItem quicksaveItem;

    // The "Session" menu.
    private JMenu sessionMenu;

    // The "Game" menu.
    private JMenu gameMenu;

    // The "Board" menu items.
    private LinkedHashMap<Integer, JCheckBoxMenuItem> boardSizeItemMap;

    // Directory for saving sessions.
    private final String SAVED_DATA_DIRECTORY_FILEPATH;
    private final File SAVED_DATA_DIRECTORY;

    // Directory for images.
    private final String IMAGES_DIRECTORY_FILEPATH;

    /**
     * (1) Constructor of Reversi objects
     */
    public Reversi()
    {
        // Detect operating system & create saved data directory filepath.
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            SAVED_DATA_DIRECTORY_FILEPATH = System.getProperty("user.dir") + "\\data\\savedsessions\\";
            IMAGES_DIRECTORY_FILEPATH = System.getProperty("user.dir") + "\\images\\";
        }
        else {
            SAVED_DATA_DIRECTORY_FILEPATH = System.getProperty("user.dir") + "/data/savedsessions/";
            IMAGES_DIRECTORY_FILEPATH = System.getProperty("user.dir") + "/images/";
        }

        // Store the saved data directory.
        SAVED_DATA_DIRECTORY = new File(SAVED_DATA_DIRECTORY_FILEPATH);

        // Create the saved data directory on the current system, if it doesn't exist.
        if (!SAVED_DATA_DIRECTORY.exists()) {
            SAVED_DATA_DIRECTORY.mkdirs();
        }

        // Create the file chooser.
        fileChooser = new JFileChooser(SAVED_DATA_DIRECTORY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Sessions", "rvsi", "serialized", "ser"));

        // Set the disk colors.
        setDiskColors();

        // Create the frame.
        createFrame();

        // Pre-session state.
        session = null;
    }
    
    /**
     * Main method.
     * Create a Reversi object.
     * 
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args)
    {
        Reversi newSession = new Reversi();
    }

    /**
     * Set the disk colors for Reversi and store the number of players.
     */
    private void setDiskColors()
    {
        diskColors = new LinkedHashMap<>();

        diskColors.put("Black", Color.BLACK);
        diskColors.put("White", Color.WHITE);

        playerCount = diskColors.size();
    }

    /**
     * Create the Reversi JFrame.
     */
    private void createFrame()
    {
        // Create the frame.
        frame = new JFrame("Reversi");

        // Configure window listener for closing the frame.
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    quit();
                }
            });

        // Set the frame's icon.
        ImageIcon icon = new ImageIcon(IMAGES_DIRECTORY_FILEPATH + "icon.png");
        frame.setIconImage(icon.getImage());

        // Create the frame's menu bar.
        JMenuBar menuBar = new JMenuBar();

        // Create the "File" menu.
        createFileMenu(menuBar);

        // Create the "Session" menu.
        createSessionMenu(menuBar);

        // Create the "Game" menu.
        createGameMenu(menuBar);

        // Create the "Board" menu.
        createBoardMenu(menuBar);

        // Create the "Help" menu.
        createHelpMenu(menuBar);

        // Add the menu bar to the frame.
        frame.setJMenuBar(menuBar);

        // Store the frame's content pane.
        Container contentPane = frame.getContentPane();

        // Create the main panel.
        createMainPanel(contentPane);

        // Pack the frame & set it to be visible.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Create the "File" menu for the frame's menu bar.
     * 
     * @param   menuBar     The frame's menu bar.
     */
    private void createFileMenu(JMenuBar menuBar)
    {
        // Create the "File" menu.
        JMenu fileMenu = new JMenu("File");

        // Create the "New Session" menu item.
        newSessionItem = new JMenuItem("New Session");
        newSessionItem.addActionListener(e -> startNewSession());
        newSessionItem.setEnabled(false);

        // Create the "Save Session" menu item.
        saveSessionItem = new JMenuItem("Save Session");
        saveSessionItem.addActionListener(e -> saveSession(false));
        saveSessionItem.setEnabled(false);

        // Create the "Quicksave" menu item.
        quicksaveItem = new JMenuItem("Quicksave");
        quicksaveItem.addActionListener(e -> saveSession(true));
        quicksaveItem.setEnabled(false);

        // Create the "Load Session" menu item.
        JMenuItem loadSessionItem = new JMenuItem("Load Session");
        loadSessionItem.addActionListener(e -> loadSession());

        // Create the "Quit" menu item.
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> quit());

        // Add the menu items to the "File" menu.
        fileMenu.add(newSessionItem);
        fileMenu.add(saveSessionItem);
        fileMenu.add(quicksaveItem);
        fileMenu.add(loadSessionItem);
        fileMenu.add(quit);

        // Add the "File" menu to the menu bar.
        menuBar.add(fileMenu);
    }

    /**
     * Create the "Session" menu for the frame's menu bar.
     * 
     * @param   menuBar     The frame's menu bar.
     */
    private void createSessionMenu(JMenuBar menuBar)
    {
        // Create the "Session" menu.
        sessionMenu = new JMenu("Session");
        sessionMenu.setEnabled(false);

        // Create the "New Game" menu item.
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> startNewGame(false));
        sessionMenu.add(newGameItem);

        // Create the "Show Game History menu item.
        JMenuItem gameHistoryItem = new JMenuItem("Show Game History");
        gameHistoryItem.addActionListener(e -> showGameHistory());
        sessionMenu.add(gameHistoryItem);

        // Add the "Session" menu to the menu bar.
        menuBar.add(sessionMenu);
    }

    /**
     * Create the "Game" menu for the frame's menu bar.
     * 
     * @param   menuBar     The frame's menu bar.
     */
    private void createGameMenu(JMenuBar menuBar)
    {
        // Create the "Game" menu.
        gameMenu = new JMenu("Game");
        gameMenu.setEnabled(false);

        // Create the "Show Legal Moves" item.
        JCheckBoxMenuItem showLegalMovesItem = new JCheckBoxMenuItem("Show Legal Moves");
        showLegalMovesItem.addActionListener(e -> showLegalMoves(showLegalMovesItem.getState()));
        gameMenu.add(showLegalMovesItem);

        // Add the "Game " menu to the menu bar.
        menuBar.add(gameMenu);
    }

    /**
     * Create the "Board" menu for the frame's menu bar, which simply
     * contains checkboxes for selecting a size for the board.
     * 
     * @param   menuBar     The frame's menu bar.
     */
    private void createBoardMenu(JMenuBar menuBar)
    {
        // Create the "Board" menu.
        JMenu boardMenu = new JMenu("Board");

        // Create the board size item map.
        boardSizeItemMap = new LinkedHashMap<>();

        // Create the menu items for each board size.
        JCheckBoxMenuItem largeBoardSizeItem = new JCheckBoxMenuItem(String.format("Large (%d × %d)", LARGE_BOARD_SIZE, LARGE_BOARD_SIZE));
        largeBoardSizeItem.addActionListener(e -> resizeBoardPanel(LARGE_BOARD_SIZE, largeBoardSizeItem));
        JCheckBoxMenuItem standardBoardSizeItem = new JCheckBoxMenuItem(String.format("Standard (%d × %d)", 
                    STANDARD_BOARD_SIZE, STANDARD_BOARD_SIZE), true);
        standardBoardSizeItem.addActionListener(e -> resizeBoardPanel(STANDARD_BOARD_SIZE, standardBoardSizeItem));
        JCheckBoxMenuItem smallBoardSizeItem = new JCheckBoxMenuItem(String.format("Small (%d × %d)", SMALL_BOARD_SIZE, SMALL_BOARD_SIZE));
        smallBoardSizeItem.addActionListener(e -> resizeBoardPanel(SMALL_BOARD_SIZE, smallBoardSizeItem));

        // Add the board size items to the map.
        boardSizeItemMap.put(LARGE_BOARD_SIZE, largeBoardSizeItem);
        boardSizeItemMap.put(STANDARD_BOARD_SIZE, standardBoardSizeItem);
        boardSizeItemMap.put(SMALL_BOARD_SIZE, smallBoardSizeItem);

        // Add the radio buttons to button group.
        ButtonGroup resizeBoardButtons = new ButtonGroup();
        resizeBoardButtons.add(largeBoardSizeItem);
        resizeBoardButtons.add(standardBoardSizeItem);
        resizeBoardButtons.add(smallBoardSizeItem);

        // Add the radio buttons to the "Board" menu.
        boardMenu.add(largeBoardSizeItem);
        boardMenu.add(standardBoardSizeItem);
        boardMenu.add(smallBoardSizeItem);

        // Add the "Board" menu to the menu bar.
        menuBar.add(boardMenu);
    }

    /**
     * Create the "Help" menu for the frame's menu bar.
     * 
     * @param   menuBar     The frame's menu bar.
     */
    private void createHelpMenu(JMenuBar menuBar)
    {
        // Create the "Help" menu.
        JMenu helpMenu = new JMenu("Help");

        // Create the "About" menu item.
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, 
                    REVERSI_VERSION_INFO, 
                    "About", 
                    JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        // Add the "Help" menu to the menu bar.
        menuBar.add(helpMenu);
    }

    /**
     * Create the main panel for the GUI:
     * 
     * The main panel contains the game panel in its CENTER region,
     * and the right panel in its EAST region.
     * 
     * @param   contentPane     The content pane of Reversi's frame.
     */
    private void createMainPanel(Container contentPane)
    {
        // Create the main panel.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));
        mainPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));
        mainPanel.setBackground(MAIN_PANEL_BACKGROUND_COLOR);

        // Create the game panel.
        createGamePanel(mainPanel);

        // Create the right panel.
        createRightPanel(mainPanel);

        // Add the main panel to the frame.
        contentPane.add(mainPanel);
    }

    /**
     * Create the game panel for the GUI:
     * 
     * The game panel contains the board panel in its CENTER region,
     * and the message panel in its SOUTH region.
     * 
     * @param   mainPanel       The main panel.
     */
    private void createGamePanel(JPanel mainPanel)
    {
        // Create the game panel.
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout(INNER_PADDING_SIZE, INNER_PADDING_SIZE));
        gamePanel.setBackground(MAIN_PANEL_BACKGROUND_COLOR);

        // Create the board panel.
        createBoardPanel(gamePanel);

        // Create the message panel.
        createMessagePanel(gamePanel);

        // Add the game panel to the main panel's CENTER region.
        mainPanel.add(gamePanel, BorderLayout.CENTER);
    }

    /**
     * Create the board panel for the GUI:
     * 
     * The board panel displays the Reversi game board. The board panel has a set
     * maximum size, which can be completely filled by the Reversi board at its
     * maximum size, or otherwise centers the Reversi board at smaller sizes.
     * 
     * @param   gamePanel       The game panel.
     */
    private void createBoardPanel(JPanel gamePanel)
    {
        // Create the outer board panel;
        JPanel outerBoardPanel = new JPanel();
        outerBoardPanel.setLayout(new BorderLayout(INNER_PADDING_SIZE, INNER_PADDING_SIZE));
        outerBoardPanel.setBackground(MAIN_PANEL_BACKGROUND_COLOR);

        // Create the inner board panel.       
        JPanel innerBoardPanel = new JPanel();
        innerBoardPanel.setLayout(new FlowLayout());
        innerBoardPanel.setBackground(Color.DARK_GRAY);

        // Create the board panel, adding a parent listener.
        boardPanel = new BoardPanel(LARGE_BOARD_SIZE, 
            STANDARD_BOARD_SIZE,
            new MouseAdapter()
            {
                /**
                 * Update when the board panel is clicked.
                 * 
                 * @param   e       The mouse event.
                 */
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    // Do nothing if the board panel is inactive or set warning status if the attempted move was illegal.
                    if (!boardPanel.isActive()) {
                        return;
                    }
                    else if (!boardPanel.isMoveLegal()) {
                        // Set the warning.
                        setWarning("Illegal move");
                        repaintFrame();

                        // Pause execution.
                        Timer timer = new Timer(2000, new ActionListener() 
                                {
                                    @Override
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        setWarning("");
                                    }
                                }
                            );
                        timer.setRepeats(false);
                        timer.start(); 
                        repaintFrame();
                        return;
                    }
                    else {
                        session.setSaved(false);
                        quicksaveItem.setEnabled(true);
                        updateNextTurn();
                    }
                }

                /**
                 * Update when the mouse hovers over the board panel.
                 * 
                 * @param   e       The mouse event.
                 */
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    // Do nothing if the board panel is currently inactive.
                    if (!boardPanel.isActive()) {
                        return;
                    }

                    // Repaint the frame.
                    repaintFrame();
                }

                /**
                 * Update when the mouse hovers out of the board panel.
                 */
                @Override
                public void mouseExited(MouseEvent e)
                {
                    // Do nothing if the board panel is inactive.
                    if (!boardPanel.isActive()) {
                        return;
                    }

                    // Repaint the frame.
                    repaintFrame();
                }
            }
        );
        boardPanel.setPreferredSize(new Dimension(BOARD_PANEL_PREF_WIDTH, BOARD_PANEL_PREF_HEIGHT));
        boardPanel.setBackground(Color.DARK_GRAY);

        // Add the board panel to the inner board panel.
        innerBoardPanel.add(boardPanel);

        // Add the inner board & warning label panels to the outer board panel.
        outerBoardPanel.add(innerBoardPanel, BorderLayout.CENTER);

        // Add the outer board panel to the game panel's CENTER region.
        gamePanel.add(Box.createVerticalStrut(INNER_PADDING_SIZE), BorderLayout.NORTH);
        gamePanel.add(outerBoardPanel, BorderLayout.CENTER);
        gamePanel.add(Box.createVerticalStrut(INNER_PADDING_SIZE), BorderLayout.SOUTH);
    }

    /**
     * Create the message panel for the GUI:
     * 
     * The message panel is used for displaying status & warning messages to the
     * user, such as notifying the players whose turn it is and warnings about
     * erroneous player creation & illegal move attempts.
     * 
     * @param   gamePanel       The game panel.
     */
    private void createMessagePanel(JPanel gamePanel)
    {
        // Create the message panel.
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(2, 1, 0, INNER_PADDING_SIZE));
        messagePanel.setBackground(MAIN_PANEL_BACKGROUND_COLOR);

        // Create the status label.
        statusLabel = new JLabel(DEFAULT_WELCOME_STATUS, JLabel.CENTER);
        statusLabel.setFont(new Font("Franklin Gothic Book", Font.BOLD, 20));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Create the wrapper panels for the status label.
        JPanel statusPanelX = new JPanel();
        JPanel statusPanelY = new JPanel();
        statusPanelX.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanelX.setBackground(Color.DARK_GRAY);
        statusPanelX.setBorder(new EmptyBorder(1, 1, 1, 1));
        statusPanelX.setLayout(new BoxLayout(statusPanelX, BoxLayout.X_AXIS));
        statusPanelX.setPreferredSize(new Dimension(STATUS_PANEL_PREF_WIDTH, STATUS_PANEL_PREF_HEIGHT));
        statusPanelX.add(statusLabel);
        statusPanelY.setBackground(Color.DARK_GRAY);
        statusPanelY.setBorder(new EmptyBorder(1, 1, 1, 1));
        statusPanelY.setLayout(new BoxLayout(statusPanelY, BoxLayout.Y_AXIS));
        statusPanelY.setPreferredSize(new Dimension(STATUS_PANEL_PREF_WIDTH, STATUS_PANEL_PREF_HEIGHT));
        statusPanelY.add(statusPanelX);

        // Create the warning label.
        warningLabel = new JLabel("", JLabel.CENTER);
        warningLabel.setFont(new Font("Franklin Gothic Book", Font.BOLD, 20));
        warningLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        warningLabel.setForeground(Color.RED);

        // Create the wrapper panels for the warning label.
        JPanel warningPanelX = new JPanel();
        JPanel warningPanelY = new JPanel();
        warningPanelX.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningPanelX.setBackground(Color.DARK_GRAY);
        warningPanelX.setBorder(new EmptyBorder(1, 1, 1, 1));
        warningPanelX.setLayout(new BoxLayout(warningPanelX, BoxLayout.X_AXIS));
        warningPanelX.setPreferredSize(new Dimension(WARNING_PANEL_PREF_WIDTH, WARNING_PANEL_PREF_HEIGHT));
        warningPanelX.add(warningLabel);
        warningPanelY.setBackground(Color.DARK_GRAY);
        warningPanelY.setBorder(new EmptyBorder(1, 1, 1, 1));
        warningPanelY.setLayout(new BoxLayout(warningPanelY, BoxLayout.Y_AXIS));
        warningPanelY.setPreferredSize(new Dimension(WARNING_PANEL_PREF_WIDTH, WARNING_PANEL_PREF_HEIGHT));
        warningPanelY.add(warningPanelX);

        // Add the status panel & warning panel to the message panel.
        messagePanel.add(statusPanelY);
        messagePanel.add(warningPanelY);

        // Add the status panel to the game panel's SOUTH region.
        gamePanel.add(messagePanel, BorderLayout.SOUTH);
    }

    /**
     * Create the right panel for the GUI:
     * 
     * The right panel contains the player display in its CENTER region,
     * and the "Play" button panel in its SOUTH region.
     * 
     * @param   mainPanel       The main panel.
     */
    private void createRightPanel(JPanel mainPanel)
    {
        // Create the right panel.
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(INNER_PADDING_SIZE, INNER_PADDING_SIZE));
        rightPanel.setBackground(MAIN_PANEL_BACKGROUND_COLOR);

        // Create the player display panel.
        createPlayerDisplayPanel(rightPanel);

        // Create the button panel.
        createButtonPanel(rightPanel);

        // Add the right panel to the main panel's EAST region.
        mainPanel.add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Create the player display panel for the GUI:
     * 
     * The player display panel contains the player panels in a grid layout,
     * spacing them appropriately.
     * 
     * @param   rightPanel      The right panel.
     */
    private void createPlayerDisplayPanel(JPanel rightPanel)
    {
        // Create the player display panel.
        JPanel playerDisplayPanel = new JPanel();
        playerDisplayPanel.setLayout(new GridLayout(playerCount, 1, INNER_PADDING_SIZE, INNER_PADDING_SIZE));
        playerDisplayPanel.setPreferredSize(new Dimension(PLAYER_DISPLAY_PANEL_PREF_WIDTH, PLAYER_DISPLAY_PANEL_PREF_HEIGHT));

        // Create the player panels.
        createPlayerPanels(playerDisplayPanel);

        // Create the padding panel.
        JPanel paddingPanel = new JPanel();
        paddingPanel.setBackground(Color.DARK_GRAY);
        paddingPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));
        paddingPanel.setLayout(new GridLayout(1, 1));

        // Add the player display panel to the right panel's CENTER region.
        paddingPanel.add(playerDisplayPanel);
        rightPanel.add(paddingPanel, BorderLayout.CENTER);
    }

    /**
     * Create the player panels for the GUI:
     * 
     * The player panels contain information about the current session's
     * players, such as their names, current game score and total game wins
     * in the current session.
     * 
     * In their pre-session state, player panels contain a text field in which
     * players can enter their names. Upon pressing the "Play" button, the
     * entered names (provided they are non-empty) are locked into the player 
     * panels, thus entering their in-session state.
     * 
     * @param   playerDisplayPanel      The player display panel.
     */
    private void createPlayerPanels(JPanel playerDisplayPanel)
    {
        // Initialise the player panels array.
        playerPanels = new PlayerPanel[playerCount];

        // Create a player panel for each disk color & add each created player panel to the array.
        int i = 0;
        for (String diskColorName : diskColors.keySet()) {            
            // Create the player panel.
            PlayerPanel playerPanel = new PlayerPanel(diskColorName, diskColors.get(diskColorName));

            // Add the player panel to the player display panel & player panels array.
            playerDisplayPanel.add(playerPanel);
            playerPanels[i++] = playerPanel;
        }
    }

    /**
     * Create the button panel for the GUI:
     * 
     * This panel contains the "Play" button.
     * 
     * When the "Play" button is pressed, the entered names in the player 
     * panels are locked in, and a Reversi game begins. After the "Play" 
     * button is pressed, it is no longer visible on the GUI during an 
     * active game, and thus unusable. It becomes usable when a game
     * ends & the user is given a choice to begin another game.
     * 
     * @param   rightPanel      The right panel.
     */
    private void createButtonPanel(JPanel rightPanel)
    {
        // Create the button panel.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1));
        buttonPanel.setPreferredSize(new Dimension(BUTTON_PANEL_PREF_WIDTH, BUTTON_PANEL_PREF_HEIGHT));

        // Create the "Play" button.
        playButton = new JButton("Play");
        playButton.addActionListener(e -> startNewGame(false));
        playButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        playButton.setFont(new Font("Tahoma", Font.BOLD, 20));

        // Create the wrapper panel for the "Play" button.
        JPanel playButtonPanel = new JPanel();
        playButtonPanel.setBackground(Color.DARK_GRAY);
        playButtonPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, 
                OUTER_PADDING_SIZE * 6, 
                OUTER_PADDING_SIZE, 
                OUTER_PADDING_SIZE * 6));
        playButtonPanel.setLayout(new GridLayout(1, 1));
        playButtonPanel.add(playButton);

        // Add the "Play" to the button panel.
        buttonPanel.add(playButtonPanel);

        // Add the button panel to the right panel's SOUTH region.
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the players, based on the inputs given to the player panels,
     * checking if they are empty beforehand.
     * 
     * @return      True if the players were created successfully, else false.
     */
    private boolean createPlayers()
    {
        // Initialise the players array.
        players = new Player[playerCount];

        // Check for empty inputs.
        for (PlayerPanel playerPanel : playerPanels) {
            if (playerPanel.isNameFieldEmpty()) {
                return false;
            }
        }

        // No empty inputs. Create the players.
        for (int i = 0; i < playerCount; i++) {
            players[i] = playerPanels[i].createPlayer();
        }

        // Sort the players array.
        Arrays.sort(players);

        // Set the player's IDs to signify their turn ordering.
        for (int i = 0; i < playerCount; i++) {
            players[i].setID(i + 1);
        }

        return true;
    }

    /**
     * Resize the board panel to a given size, warning the user that the current
     * game will be reset if they are in an active game within an active session.
     * Otherwise, just resize the board.
     * 
     * @param   newSize         The new size for the board panel.
     * @param   itemSelected    The menu item that was selected.
     */
    private void resizeBoardPanel(int newSize, JCheckBoxMenuItem itemSelected)
    {
        // Do nothing if the board panel is already displaying the new size.
        if (newSize == boardPanel.getDisplaySize()) {
            return;
        }

        // Check if the user is in an active game.
        if (session != null && session.hasGameStarted() && session.isGameActive()) {
            String[] options = { "Resize Board", "Cancel" };
            int response = JOptionPane.showOptionDialog(frame,
                    "Are you sure you want to resize the board?\nThe current game will be reset.",
                    "Resize Board", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Do not resize the board if the user opts out.
            if (response != 0) {
                itemSelected.setState(false);
                boardSizeItemMap.get(boardPanel.getDisplaySize()).setState(true);
                return;
            }
        }

        // Check if the board panel has been successfully resized.
        try {
            boardPanel.resize(newSize);
        }
        catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, "Could not resize the board.");
            return;
        }

        // Start a new game, if a session is active.
        if (session != null) {
            startNewGame(true);
        }
        else {
            repaintFrame();
        }
    }

    /**
     * Start a new session. Do nothing if no session is currently active,
     * otherwise check with the user via a modal dialog whether they
     * want to start a new session, warning them that any unsaved progress
     * in the current session will be lost.
     */
    private void startNewSession()
    {
        // Do nothing if there is not an active session.
        if (session == null) {
            return;
        }

        // Notify user of unsaved progress.
        if (!session.isSaved()) {
            // Dialog options.
            Object[] options = { "Start New Session", "Cancel" };

            // Check user's response to dialog.
            int response = JOptionPane.showOptionDialog(frame,
                    "Are you sure you want to start a new session?\nAny unsaved progress will be lost.",
                    "New Session",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Exit if the user cancels/quits.
            if (response != 0) {
                return;
            }
        }

        // Nullify the session.
        session = null;

        // Reset the board panel.
        boardPanel.reset();
        boardPanel.setActive(false);

        // Reset the player panels.
        for (PlayerPanel playerPanel : playerPanels) {
            playerPanel.reset();
        }

        // Reset the button panel.
        playButton.setEnabled(true);

        // Reset the status panel.
        setStatus(DEFAULT_WELCOME_STATUS);
        setWarning("");

        // Disable the "File", "Session" and "Game" menu.
        enableFileMenu(false);
        sessionMenu.setEnabled(false);
        gameMenu.setEnabled(false);

        // Repaint the frame.
        repaintFrame();
    }

    /**
     * Save the current session. If no session is currently active,
     * notify the user of this via a message dialog. If the session
     * has not yet been saved, let the user save the session under
     * a chosen filename. Otherwise, save the session as its assigned
     * filename.
     * 
     * @param   quicksave       True if "Quicksave" has been selected,
     *                          else false.
     */
    private boolean saveSession(boolean quicksave)
    {
        // Notify user if there is not an active session.
        if (session == null) {
            JOptionPane.showMessageDialog(frame, "No active session to save.");
            return false;
        }

        // Check if a save file already exists.
        boolean isNewFilename = false;
        File saveFile = null;
        String filepath = "", filename = "", prevFilename = "";
        if (quicksave && session.hasFilename()) {
            filepath = SAVED_DATA_DIRECTORY_FILEPATH + session.getFilename();
        }
        else {
            // Show save dialog.
            fileChooser.showSaveDialog(frame);

            // Do nothing if user does not opt to save.
            saveFile = fileChooser.getSelectedFile();
            if (saveFile == null) {
                return false;
            }

            // Store the created filename & previous filename.
            filename = saveFile.getName();
            prevFilename = session.getFilename();

            // Add file extension if needed.
            if (!isExtensionValid(filename)) {
                filename += SAVED_DATA_FILENAME_EXTENSIONS[0];
            }

            // Set the filename & filepath.
            filepath = SAVED_DATA_DIRECTORY_FILEPATH + filename;
            isNewFilename = true;
        }

        // Check for overwrite.
        if (saveFile != null && saveFile.exists()) {
            String[] options = { "Overwrite", "Cancel" };
            int response = JOptionPane.showOptionDialog(frame, 
                    filename + " already exists. Do you want to overwrite?",
                    "Save Session",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Exit if user opts not to overwrite.
            if (response != 0) {
                JOptionPane.showMessageDialog(frame, "Session was not saved.");
                return false;
            }
        }

        // Serialize & write session.
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath));
            session.setSaved(true);
            if (isNewFilename) {
                session.setFilename(filename);
            }
            oos.writeObject(session);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Could not save session.", "Save Session", JOptionPane.WARNING_MESSAGE);
            session.setSaved(false);
            if (isNewFilename && prevFilename == null) {
                session.setFilename(null);
            }
            return false;
        }

        // Signify that the session has been saved.
        frame.setTitle("Reversi - " + session.getFilename());
        JOptionPane.showMessageDialog(frame, "Session saved successfully.");
        quicksaveItem.setEnabled(false);

        return true;
    }

    /**
     * Load a session via the file chooser, notifying the user that unsaved
     * progress will be lost. Invalid or corrupt files will be handled, first
     * by checking for a valid extension and secondly by testing if the file
     * can be successfully deserialized and stored as a Session object.
     */
    private void loadSession()
    {
        // Show open dialog.
        fileChooser.showOpenDialog(frame);

        // Do nothing if user opts not to open a file.
        File openFile = fileChooser.getSelectedFile();
        if (openFile == null) {
            return;
        }

        // Store the filename.
        String filename = openFile.getName();

        // Check for a valid extension.
        if (!isExtensionValid(filename)) {
            JOptionPane.showMessageDialog(frame, "Invalid file type.", "Load Session", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check for unsaved progress, if a session is active.
        if (session != null && !session.isSaved()) {
            String[] options = { "Load Session", "Cancel" };
            int response = JOptionPane.showOptionDialog(frame, 
                    "Are you sure you want to load a session?\nAny unsaved progress will be lost.",
                    "Load Session",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Exit if the user opts to not load a session.
            if (response != 0) {
                return;
            }
        }

        // Attempt to deserialize.
        Session prevSession = session;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(openFile));
            session = (Session) ois.readObject();
            validateSession(session);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Could not load session.", "Load Session", JOptionPane.WARNING_MESSAGE);
            return;
        }
        catch (ClassNotFoundException | CorruptedSessionException ex) {
            JOptionPane.showMessageDialog(frame, "Corrupted session file.", "Load Session", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Set the players & player panels.
        int i = 0;
        players = session.getPlayers();
        for (PlayerPanel playerPanel : playerPanels) {
            playerPanel.setPlayer(players[i++]);
        }

        // Uncheck "Board" menu item.
        if (boardSizeItemMap.containsKey(boardPanel.getDisplaySize())) {
            boardSizeItemMap.get(boardPanel.getDisplaySize()).setState(false);
        }

        // Setup the board panel.
        boardPanel.setup(session.getCurrentGame());

        // Enable the "File", "Session" and "Game" menus.
        enableFileMenu(true);
        sessionMenu.setEnabled(true);
        gameMenu.setEnabled(true);

        // Correct the "Board" menu.
        if (boardSizeItemMap.containsKey(boardPanel.getDisplaySize())) {
            boardSizeItemMap.get(boardPanel.getDisplaySize()).setState(true);
        }

        // Update the frame's title.
        frame.setTitle("Reversi - " + session.getFilename());

        // Update the turn.
        updateNextTurn();

        // Repaint the frame.
        repaintFrame();
    }

    /**
     * Validate a given Session object to check that it conforms to
     * necessary parameters such that it can be functionally loaded
     * and displayed.
     * 
     * @param   session     The Session object to check.
     * 
     * @throws              CorruptedSessionException
     */
    private void validateSession(Session session) throws CorruptedSessionException
    {
        // Check for a valid number of players.
        if (session.getPlayers().length != playerCount) {
            throw new CorruptedSessionException("Invalid number of players in loaded session");
        }

        // Check for valid game data & a valid board size.
        if (session.getCurrentGame() == null) {
            throw new CorruptedSessionException("Loaded session's current game data is null");
        }
        else if (!boardPanel.isSizeValid(session.getCurrentGame().getBoardSize())) {
            throw new CorruptedSessionException("Invalid board size in loaded session's current game");
        }
    }

    /**
     * Enable/disable certain items in the "File" menu.
     * 
     * @param   enabled     True if the file menu is to be enabled,
     *                      or false if it is to be disabled.
     */
    private void enableFileMenu(boolean enabled)
    {
        newSessionItem.setEnabled(enabled);
        saveSessionItem.setEnabled(enabled);

        if (session != null && !session.isSaved()) {
            quicksaveItem.setEnabled(true);
        }
        else {
            quicksaveItem.setEnabled(false);
        }
    }

    /**
     * Check a given filename for a valid extension.
     * 
     * @return      True if the filename has a valid extension,
     *              else false.
     */
    private boolean isExtensionValid(String filename)
    {
        boolean extensionValid = false;
        if (filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf("."), filename.length());
            for (String defaultExtension : SAVED_DATA_FILENAME_EXTENSIONS) {
                if (extension.equals(defaultExtension)) {
                    extensionValid = true;
                    break;
                }
            }
        }

        return extensionValid;
    }

    /**
     * Show the game history of the current session, otherwise
     * notify the user that there is no game history present.
     */
    private void showGameHistory()
    {
        // Do nothing if there is no session active.
        if (session == null) {
            return;
        }

        // Store the game history list.
        ArrayList<String> gameHistory = session.getGameHistory();

        // If there is no game history present.
        if (gameHistory == null || gameHistory.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No game history to show.");
            return;
        }

        // Otherwise, show game history in dialog.
        String gameHistoryString = "";
        for (String gameData : gameHistory) {
            gameHistoryString += gameData + "\n\n";
        }
        JOptionPane.showMessageDialog(frame, gameHistoryString, "Game History", JOptionPane.INFORMATION_MESSAGE, null);
    }

    /**
     * Set the board panel's "Show Legal Moves" state, i.e. whether or not
     * it is to display a preview of all of the legal moves for the current
     * player.
     * 
     * @param       enabled     True if the legal moves are to be displayed,
     *                          else false.
     */
    private void showLegalMoves(boolean enabled)
    {
        boardPanel.setLegalMovesVisible(enabled);
        repaintFrame();
    }

    /**
     * Start a new game. The "Play" button has been pressed, either during
     * a session to initiate a new game or before a session has been started.
     * 
     * If a session is not currently active, create the players & the session.
     * Otherwise, simply start a new game.
     * 
     * @param   resized     True if the board panel has been recently resized,
     *                      else false.
     */
    private void startNewGame(boolean resized)
    {
        // If there is no session active, create the players & the session.
        if (session == null) {
            // Create the players.
            if (!createPlayers()) {
                setWarning("Please enter a name for each player on the right panel");
                return;
            }

            // Create the session.
            session = new Session(players);

            // Enable the "File", "Session" and "Game" menus.
            enableFileMenu(true);
            sessionMenu.setEnabled(true);
            gameMenu.setEnabled(true);
        }
        else if (session.isGameActive() && session.hasGameStarted() && !resized) {
            // Check if user wants to start a new game during an active game.
            String[] options = { "Start New Game", "Cancel" };
            int response = JOptionPane.showOptionDialog(frame, 
                    "Are you sure you want to start a new game?\nThe current game will be reset.",
                    "New Game",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Do not start a new game if the user opts out.
            if (response != 0) {
                return;
            }
        }

        // Create a new game to fit the size of the board panel.
        session.createGame(boardPanel.getDisplaySize());

        // Set up the board panel for the created game.
        boardPanel.setup(session.getCurrentGame());

        // Set the board panel to be interactive.
        boardPanel.setActive(true);

        // Update the display for the first turn.
        updateNextTurn();

        // Update the player panels.
        updatePlayerPanels();

        // No save since last change.
        session.setSaved(false);
        quicksaveItem.setEnabled(true);

        // Repaint the frame.
        repaintFrame();
    }

    /**
     * Set the status to a given status, or do nothing if the
     * given status is null.
     * 
     * @param   newStatus       The new status.
     */
    private void setStatus(String newStatus)
    {
        // Set the new status & repaint the frame.
        if (newStatus != null) {
            statusLabel.setText(newStatus);
        }
    }

    /**
     * Set the warning label to display a warning message, or
     * do nothing if the given warning message is null.
     * 
     * @param   message       The warning message.
     */
    private void setWarning(String message)
    {
        // Set the warning message & repaint the frame.
        if (message != null) {
            warningLabel.setText(message);
        }
    }

    /**
     * Update the displayed data in the player panels.
     */
    private void updatePlayerPanels()
    {
        for (PlayerPanel playerPanel : playerPanels) {
            playerPanel.update();
        }
    }

    /**
     * Update the display & pass data to prepare for the next turn.
     */
    private void updateNextTurn()
    {
        // Update the player panels.
        updatePlayerPanels();
        repaintFrame();
        
        // Check if the current game is finished.
        if (!session.isGameActive()) {
            setWarning("Game over: Please click Play to start a new game");

            // Check for winning player.
            if (session.getCurrentGame().hasWinningPlayer()) {
                setStatus(session.getCurrentGame().getWinningPlayer() + " wins.");
            }
            else {
                setStatus("Tie.");
            }

            // Disable board panel & reconfigure button panel.
            boardPanel.setActive(false);
            playButton.setEnabled(true);
        }
        else {
            // Clear the warning status label.
            warningLabel.setText("");

            // Update the status for next turn.
            setStatus(session.getCurrentGame().getCurrentPlayer() + "'s turn.");

            // Check if there is a legal move.
            if (!session.hasLegalMove()) {
                boardPanel.setActive(false);
                
                // Pass dialog.
                JOptionPane.showMessageDialog(frame,
                    session.getCurrentGame().getCurrentPlayer() + " has no legal moves. Pass turn.",
                    "No Legal Moves",
                    JOptionPane.WARNING_MESSAGE,
                    null);
                boardPanel.nextTurn();
                boardPanel.setActive(true);
                updateNextTurn();
            }
            else {
                boardPanel.setActive(true);
                playButton.setEnabled(false);
            }
        }
    }

    /**
     * Revalidate & repaint the frame.
     */
    private void repaintFrame()
    {
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Quit the application. Check for unsaved progress & notify the user
     * of this, giving them the option to quit without saving, save & quit
     * or return to the application.
     * 
     * NOTE: A WindowListener will be added to be the frame such that this
     *       method can be called.
     */
    private void quit()
    {
        // Check for unsaved progress.
        if (session != null && !session.isSaved()) {
            String[] options = { "Save & Quit", "Quit Without Saving", "Cancel" };
            int response = JOptionPane.showOptionDialog(frame,
                    "Are you sure you want to quit?\nAny unsaved progress will be lost.",
                    "Quit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Check if the user wants to save. Do not quit if the user does not save or opts out of quitting.
            boolean saved = false;
            if ((response == 0 && !saveSession(true)) || response == -1 || response == 2) {
                return;
            }
        }

        // Terminate application.
        System.exit(0);
    }
}