// MysteryPolygon.java
// Author:   Chad Botting
// Course:   CIS 314, Spring 2013
// Date:     February 25, 2013
// Lab0: The Mystery Polygon, draws the state of michigan based on latitude and longitude values of border

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;

public class MysteryPolygon extends JFrame implements ActionListener
{
    // private member variables
    private Polygon mystery;
    private DrawPanel myPanel;
    private JPanel buttonPanel;
    private JButton buttons[];   // an array of button references
    private boolean toggle;
    private int myScale;

    // constructor
    public MysteryPolygon()
    {
        // call the superclass constructor
        super("Mystery Polygon");
        myScale = 40; // so the image is not so small
        toggle = false; // set toggle to default value of false

        // build the button panel
        buttonPanel = new JPanel();
        buttons = new JButton[5];

        buttonPanel.setLayout(new GridLayout(1, buttons.length));

        for(int i = 0; i < buttons.length; i++)
        {
            /*
               adds a new button object to buttons[i] with appropriate text
               adds buttons[i] to the buttonPanel
               adds the 'this' actionListener to button[i]
            */

            buttons[i] = new JButton("Button " + i);
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }
        // build a DrawPanel named myPanel
        myPanel = new DrawPanel();

        // add a mouse listener to this frame
        addMouseListener(new MouseClickHandler());

        // add to the JFrame
        add(myPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setVisible(true);
    } // end of constructor

    // override of ActionPerformed
    public void actionPerformed( ActionEvent e )
    {
        // some code for each of the five buttons
        if(e.getSource() == buttons[0])
        {
            myPanel.setBackground(Color.RED);
        }
        else if (e.getSource() == buttons[1])
        {
            myPanel.setBackground(Color.GREEN);
        }
        else if (e.getSource() == buttons[2])
        {
            myPanel.setBackground(Color.YELLOW);
        }
        else if (e.getSource() == buttons[3])
        {
            myPanel.setBackground(Color.DARK_GRAY);
        }
        else if (e.getSource() == buttons[4])
        {
            String temp = JOptionPane.showInputDialog("New Scale");
            try
            {
                myScale = Integer.parseInt(temp);
                // redraw panel if it worked
                myPanel.draw();
            }
            catch(NumberFormatException myE)
            {
                // the user input a invalid value, reset back to the default
                myScale = 40; // back to the default value
                // redraw panel if it worked
                myPanel.draw();
            }
        } // end of if else
    } // end of actionPerformed method

    // our mouse click handler
    private class MouseClickHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            // get and store the x and y positions when the mouse was clicked
            int xPosition  = e.getX();
            int yPosition = e.getY();

            // did the mouse get clicked inside or outside of the mystery polygon?
            if(mystery.contains(xPosition, yPosition)) // if the position clicked is inside "mystery"
            {
                toggle = true;
                System.out.println("Inside " + xPosition + " " + yPosition);
            }
            else // if the position clicked is outside "mystery"
            {
                toggle = false;
                System.out.println("Outside " + xPosition + " " + yPosition);
            }
            /*
            I hope I am coding this correctly, I watched the video and it seemed as thought you wanted the color to
            change as soon user clicked once inside or once outside the polygon.  The idea of using the name of the
            outer class followed by .this ("MysteryPolygon.this") to call a method in the outer class from the inner
            class comes from a stack overflow post.  The idea of that did not come to me when trying to think of
            how that would work.  I hope this is appropriate Java coding.
            */

            MysteryPolygon.this.myPanel.draw();
        } // end of MouseClick method
    } // end of MouseClickHandler private inner class

    private class DrawPanel extends JPanel
    {
        // These values are negative because we are west of the prime meridian
        // The more negative values are actually closer to zero when normalized on a X,Y grid
        private double xCoordOrig[ ] = {
                -86.9044, -86.1552, -86.5586,
                -85.6077, -84.7143, -85.6365,
                -87.7113, -90.3912, -87.5960,
                -88.4029, -86.9332, -84.8872,
                -83.3600, -83.4464, -84.5126,
                -83.3023, -83.2447, -83.8498,
                -83.9075, -83.6769, -82.9277,
                -82.3514, -82.4090, -83.3888,
                -86.9044};

        // These values are positive because we are north of the equator
        // The larger the value, the more north
        private double yCoordOrig[ ] = {
                41.7735, 42.8685, 44.0500,
                45.2027, 45.7790, 46.1248,
                45.2603, 46.5571, 47.5656,
                46.8452, 46.5282, 46.8452,
                46.0095, 45.8078, 45.9519,
                45.2315, 44.4246, 43.9347,
                43.6466, 43.5889, 44.1076,
                42.9550, 42.6092, 41.7447,
                41.7735};

        // These private variables are needed because the polygon object needs integers, not doubles
        private int xCoord[];
        private int yCoord[];

        // constructor
        private DrawPanel()
        {
            // This constructor works well because we can put in data sets of various sizes
            // and the program will still work
            xCoord = new int[xCoordOrig.length];
            yCoord = new int[yCoordOrig.length];
        } // end of constructor for DrawPanel

        // Here we are overriding the paintComponent method as this class extends JPanel and we are not drawing
        // standard components such as JButton, JLabel, or JTextbox
        public void paintComponent( Graphics g )
        {
            /*
            we need to call the super classes paintComponent method so that the area where we are not specifically
            controlling (the background) will be updated when we change something we are specifically controlling
            (the polygon: drawing it, re-coloring it, resizing it).  Otherwise anything that is not the polygon will
            likely have fragments and we would have no control over the background color.
            */

            super.paintComponent(g);

            /*
            The logic of normalizing the X or latitude values is as follows: All values are negative because we are
            west of the prime meridian.  The smaller (more negative) values are more west and therefore should appear
            closer to the left side of the window.  So all we have to do is shift all values to the positive side of
            the number line with a constant value while keeping them as close to zero as possible.

            The logic of normalizing the Y or longitude values is as follows: The more north we go on the map the
            larger the values of the longitude.  This is due to the fact we are north of the equator.  This is
            opposite of the how java will paint the image, the X coordinate of 0 is the top of the window and 600 is
            the bottom of the window.  The easier way to resolve this is to make the Y values negative, but still as
            close to zero as possible, then take the absolute value or negative of all values.

            This logic would be a little more fun if we were drawing something on the equator or the prime meridian,
            although not that much harder.
            */

            int falseEasting = 92;   // used for x value normalization, by adding 92 we shift all values
            int falseNorthing = -50; // used for y value normalization,
            int scale = myScale;  // myScale is changeable by Button5

            for(int i=0; i < xCoord.length;i++)
            {
                xCoord[i] = (int) ( (xCoordOrig[i] + falseEasting)*scale);
                // System.out.print(xCoord[i] + ", ");
                yCoord[i] = (int) - ( (yCoordOrig[i] + falseNorthing)*scale);
                // System.out.println(yCoord[i]);
            } // end of for loop

            // instantiating the mystery polygon
            mystery = new Polygon(xCoord, yCoord, xCoord.length);

            // logic to control what color the polygon will be
            if(toggle)
            {
                g.setColor(Color.CYAN);
            }
            else
            {
                g.setColor(Color.MAGENTA);
            }

            g.drawPolygon(mystery);  // this draws the polygon's outline, but it is not yet filled in
            g.fillPolygon(mystery); // this fills in the polygon so that it is a solid color

        } // end of PaintComponent method

        public void draw()
        {
            // some code here that will repaint
            repaint();
        }

    } // end of private DrawPanel innerclass
} // end of public class MysteryPolygon
