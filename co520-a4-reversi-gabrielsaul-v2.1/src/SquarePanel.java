import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Class:       SquarePanel
 * Category:    GUI
 * Superclass:  JPanel
 * Summary:     This class graphically represents a square on the board panel component. A square 
 *              can be empty or contain a colored disk, which is created in the form of a DiskPanel
 *              object; the class definition of which is nested within SquarePanel.
 *
 * @author  Gabriel Doyle-Finch
 * @version 2021.04.19
 */
public class SquarePanel extends JPanel
{
    // The square's position co-ordinates.
    private final int xPos;
    private final int yPos;

    // The disk panel of the square.
    private DiskPanel diskPanel;

    /**
     * (1) Constructor of SquarePanel objects: Initially empty.
     * 
     * @param   x   The x-position of this square.
     * @param   y   The y-position of this square.
     */
    public SquarePanel(int x, int y)
    {
        xPos = x;
        yPos = y;

        // Initially blank.
        diskPanel = null;

        setLayout(new GridLayout(1, 1));
    }

    /**
     * Set the disk panel for this square, creating a new disk panel
     * if no disk panel exists or simply changing the disk color if 
     * it does. Passing in a null color parameter will delete the
     * disk panel by painting it as the same color as the background.
     * 
     * @param   color   The new disk color for this square's disk panel, or
     *                  null if the disk panel is to be deleted.
     */
    public void setDiskPanel(Color color)
    {
        // Set the new disk panel, or delete it.
        if (diskPanel == null && color != null) {
            diskPanel = new DiskPanel(color, getWidth(), getHeight());
            add(diskPanel);
        }
        else if (color != null)  {
            diskPanel.setColor(color);
        }
        else if (diskPanel != null) {
            diskPanel.setColor(getBackground());
        }
        
        repaint();
    }

    /**
     * Return the x-position of this square.
     * 
     * @return      The x-position of this square.
     */
    public int getXPosition()
    {
        return xPos;
    }

    /**
     * Return the y-position of this square.
     * 
     * @return      The y-position of this square.
     */
    public int getYPosition()
    {
        return yPos;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Class:       DiskPanel
     * Superclass:  JPanel
     * Summary:     This class represents a graphical disk, which is painted as a given
     *              color upon creation and can be repainted as another color when
     *              needed.
     *
     * @author  Gabriel Doyle-Finch
     * @version 2021.04.15
     */
    private class DiskPanel extends JPanel
    {
        /* * * * * * * * * * * * Class Variables * * * * * * * * * * * */

        // Padding sizes.
        private static final int OUTER_PADDING_SIZE = 1;

        /* * * * * * * * * * * * Instance Variables * * * * * * * * * * * */

        // Panel dimensions
        private int width;
        private int height;

        // Disk dimensions.
        private int diskWidth;
        private int diskHeight;

        // The color of the disk.
        private Color color;

        /**
         * (1) Constructor of DiskPanel objects
         * 
         * @param   color               The color of the disk.
         * @param   parentPanelWidth    The width of the parent square panel.
         * @param   parentPanelHeight   The height of the parent square panel.
         */
        public DiskPanel(Color color, int squarePanelWidth, int squarePanelHeight)
        {
            // Assign color.
            this.color = color;

            // Calculate panel dimensions.
            width  = squarePanelWidth - (OUTER_PADDING_SIZE * 2);
            height = squarePanelHeight - (OUTER_PADDING_SIZE * 2); 

            // Set the border padding.
            setBorder(new EmptyBorder(OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE, OUTER_PADDING_SIZE));

            // Set the disk dimensions.
            diskWidth  = (int) (width / 1.5);
            diskHeight = (int) (height / 1.5);
        }

        /**
         * Paint the disk.
         * 
         * @param   graphics    The Graphics component.
         */
        @Override
        public void paintComponent(Graphics g) 
        {
            // Set antialiasing.
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set the size of the panel.
            setSize(width, height);

            // Calculate starting position for centering.
            int x = (width - diskWidth) / 2;
            int y = (height - diskHeight) / 2;

            // Set the color.
            g.setColor(color);

            // Draw the disk.
            g.drawOval(x, y, diskWidth, diskHeight);

            // Fill the disk.
            g.fillOval(x, y, diskWidth, diskHeight);
        }  

        /**
         * Set the color of this disk.
         * 
         * @param   color   The new color of the disk.
         */
        public void setColor(Color color)
        {
            this.color = color;
        }
    }
}
