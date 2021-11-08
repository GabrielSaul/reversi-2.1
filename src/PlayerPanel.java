import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Class:       PlayerPanel
 * Category:    GUI
 * Superclass:  JPanel 
 * Summary:     This class represents the player panel component, which displays information
 *              about a player, such as their disk color, their name, their points in the
 *              current game and their total wins in the session.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.30
 */

public class PlayerPanel extends JPanel
{
    /* * * * * * * * * * * * Class Variables * * * * * * * * * * * */
    
    // Padding size constants.
    private static final int OUTER_PADDING_SIZE = 5;
    private static final int INNER_PADDING_SIZE = 2;
    
    // Background color constants.
    private static final Color ACTIVE_BACKGROUND_COLOR = new Color(0x77, 0xDD, 0x77);

    /* * * * * * * * * * * * Instance Variables * * * * * * * * * * * */
    
    // The default background color.
    private final Color defaultBackgroundColor;
    
    // The associated player.
    private Player player;

    // The displayed disk color name.
    private String diskColorName;

    // The disk color itself.
    private Color diskColor;

    // The name panel.
    private JPanel namePanel;

    // The name input field.
    private JTextField nameField;

    // The current game total label.
    private JLabel currentGameTotalLabel;

    // The win count label.
    private JLabel winCountLabel;

    /**
     * (1) Constructor of PlayerPanel objects
     * 
     * @param   diskColorName   The displayed disk color name.
     * @param   diskColor       The disk color itself.
     */
    public PlayerPanel(String diskColorName, Color diskColor)
    {
        this.diskColorName = diskColorName;
        this.diskColor = diskColor;
        
        defaultBackgroundColor = getBackground();

        create();
    }

    /**
     * Create the player panel in its pre-session state.
     * 
     * The player panel contains 3 separate panels:
     *  1.  The name panel.
     *  2.  The current game panel.
     *  3.  The session panel.
     */
    private void create()
    {   
        // Create the player panel's layout.
        GridLayout playerPanelLayout = new GridLayout(3, 1);

        // Set the player panel's layout & padding.
        playerPanelLayout.setVgap(OUTER_PADDING_SIZE);
        setLayout(playerPanelLayout);

        // Set the player panel's initial titled border.
        setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), diskColorName, TitledBorder.CENTER, TitledBorder.BELOW_TOP));

        // Create the name panel.
        createNamePanel();

        // Create the current game panel.
        createCurrentGamePanel();

        // Create the session panel.
        createSessionPanel();
    }
    
    /**
     * Reset the player panel to its pre-session state.
     */
    public void reset()
    {
        removeAll();
        create();
        setBackground(defaultBackgroundColor);
    }

    /**
     * Create the name panel.
     * 
     * The name panel is initially created with a text input field, which
     * receives textual input that then becomes the player's displayed
     * name during a session.
     */
    private void createNamePanel()
    {
        // Create the name panel.
        namePanel = new JPanel();

        // Create the name panel's layout.
        GridLayout namePanelLayout = new GridLayout(2, 1);

        // Set the name panel's layout & padding.
        namePanelLayout.setVgap(INNER_PADDING_SIZE);
        namePanel.setLayout(namePanelLayout);

        // Create the name input field.
        nameField = new JTextField();

        // Add the name panel's input label.
        namePanel.add(new JLabel("Enter name: "));

        // Add the name panel's input field.
        namePanel.add(nameField);

        // Create the padding panel.
        JPanel paddingPanel = new JPanel();
        paddingPanel.setLayout(new GridLayout(1, 1));
        paddingPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));

        // Add the name panel to the player panel.
        paddingPanel.add(namePanel);
        add(paddingPanel);
    }

    /**
     * Create the current game panel.
     * 
     * The current game panel displays the total number of disks that a player
     * has on the board at any given turn during a game. It is updated at the
     * end of each turn.
     */
    private void createCurrentGamePanel()
    {
        // Create the current game panel.
        JPanel currentGamePanel = new JPanel();

        // Set the current game panel's border & layout.
        currentGamePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Current Game"));
        currentGamePanel.setLayout(new GridLayout(1, 4));

        // Add the "Total" label.
        currentGamePanel.add(new JLabel("Total: "));

        // Create the current game total label.
        currentGameTotalLabel = new JLabel("-");

        // Add the current game total label.
        currentGamePanel.add(currentGameTotalLabel);
        currentGamePanel.add(Box.createHorizontalGlue());

        // Create the padding panel.
        JPanel paddingPanel = new JPanel();
        paddingPanel.setLayout(new GridLayout(1, 1));
        paddingPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));

        // Add the current game panel to the player panel.
        paddingPanel.add(currentGamePanel);
        add(paddingPanel);
    }

    /**
     * Create the session panel.
     * 
     * The session panel displays the total number of wins that a player has
     * during the session. It is updated at the end of each game.
     */
    private void createSessionPanel()
    {
        // Create the session panel.
        JPanel sessionPanel = new JPanel();

        // Set the current game panel's border & layout.
        sessionPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Session"));
        sessionPanel.setLayout(new GridLayout(1, 4));

        // Add the "Total" label.
        sessionPanel.add(new JLabel("Wins: "));

        // Create the current game total label.
        winCountLabel = new JLabel("-");

        // Add the current game total label.
        sessionPanel.add(winCountLabel);
        sessionPanel.add(Box.createHorizontalGlue());
        sessionPanel.add(Box.createHorizontalGlue());

        // Create the padding panel.
        JPanel paddingPanel = new JPanel();
        paddingPanel.setLayout(new GridLayout(1, 1));
        paddingPanel.setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));

        // Add the current game panel to the player panel.
        paddingPanel.add(sessionPanel);
        add(paddingPanel);
    }
    
    /**
     * Check if the name input field is empty or null.
     * 
     * @return      True if the name input field is empty or null, else false.
     */
    public boolean isNameFieldEmpty()
    {
        return (nameField == null || nameField.getText().isEmpty());
    }
    
    /**
     * Create a new player based on the inputted name in the player panel,
     * assigning to the player the inputted name and the disk color
     * associated with the player panel.
     * 
     * @return      The created Player object.
     */
    public Player createPlayer()
    {
        // Create the new player.
        Player newPlayer = new Player(nameField.getText(), diskColorName, diskColor);
        
        // Set the associated player to the newly created player.
        setPlayer(newPlayer);
        
        return newPlayer;
    }
    
    /**
     * Set the associated player to a given player, setting the panels to display
     * the player's name, their current game total and their total wins in the
     * session.
     * 
     * @param   newPlayer       The new associated player.
     */
    public void setPlayer(Player newPlayer)
    {
        // Set the new player.
        player = newPlayer;
        
        // Assign player's information to the player panel.
        diskColorName = player.getDiskColorName();
        diskColor = player.getDiskColor();
        
        // Set the player panel's titled border.
        setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), diskColorName, TitledBorder.CENTER, TitledBorder.BELOW_TOP));
        
        // Set the name panel.
        setNamePanel();
        
        // Update the displayed data.
        update();
    }
    
    /**
     * Set the name panel to display the associated player's name.
     */
    private void setNamePanel()
    {
        // Clear the name panel.
        namePanel.removeAll();

        // Set the name panel's border & layout.
        namePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Player Name"));
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
                
        // Create the name label.
        JLabel nameLabel = new JLabel(player.getName());
        
        // Add the name label to the name panel.
        namePanel.add(nameLabel);
    }
    
    /**
     * Update the displayed data on the player panel.
     */
    public void update()
    {
        // Update the current game total.
        currentGameTotalLabel.setText(String.valueOf(player.getCurrentGameTotal()));
        
        // Update the win count.
        winCountLabel.setText(String.valueOf(player.getWinCount()));
        
        // Indicate if it is currently this player's turn by highlighting the panel.
        if (player.isActive()) {
            setBackground(ACTIVE_BACKGROUND_COLOR);
        }
        else {
            setBackground(defaultBackgroundColor);
        }
    }
}
